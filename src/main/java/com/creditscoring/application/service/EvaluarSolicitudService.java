package com.creditscoring.application.service;

import com.creditscoring.application.dto.EvaluacionResponse;
import com.creditscoring.application.port.in.EvaluarSolicitudUseCase;
import com.creditscoring.application.port.out.BuroCreditoPort;
import com.creditscoring.application.port.out.ResultadoEvaluacionRepositoryPort;
import com.creditscoring.application.port.out.SolicitudRepositoryPort;
import com.creditscoring.domain.exception.SolicitudNotFoundException;
import com.creditscoring.domain.model.*;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.service.*;
import com.creditscoring.domain.specification.DictamenResolver;
import com.creditscoring.domain.specification.EvaluacionContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Caso de uso "ejecutar el motor de scoring sobre una solicitud". Coordina:
 * consulta al buro simulado -> proyeccion de cuota/DTI/capacidad residual ->
 * ScoringEngine (Strategy) -> DictamenResolver (Specification) -> asignacion
 * de tasa/cupo si aplica -> persistencia inmutable del resultado (auditoria).
 *
 * Esta clase es la unica que conoce los umbrales de negocio de "corte" del
 * producto (score de aprobacion, DTI maximo, score minimo absoluto) y la
 * version del motor de reglas vigente.
 */
public class EvaluarSolicitudService implements EvaluarSolicitudUseCase {

    public static final String VERSION_MOTOR_REGLAS = "scoring-engine-v1.0.0";

    private final SolicitudRepositoryPort solicitudRepository;
    private final ResultadoEvaluacionRepositoryPort resultadoRepository;
    private final BuroCreditoPort buroCreditoPort;
    private final ScoringEngine scoringEngine;
    private final DtiCalculator dtiCalculator;
    private final CapacidadPagoCalculator capacidadPagoCalculator;
    private final AmortizacionFrancesaCalculator amortizacionCalculator;
    private final TasaInteresAssigner tasaInteresAssigner;
    private final DictamenResolver dictamenResolver;

    public EvaluarSolicitudService(SolicitudRepositoryPort solicitudRepository,
                                    ResultadoEvaluacionRepositoryPort resultadoRepository,
                                    BuroCreditoPort buroCreditoPort,
                                    ScoringEngine scoringEngine,
                                    DtiCalculator dtiCalculator,
                                    CapacidadPagoCalculator capacidadPagoCalculator,
                                    AmortizacionFrancesaCalculator amortizacionCalculator,
                                    TasaInteresAssigner tasaInteresAssigner,
                                    DictamenResolver dictamenResolver) {
        this.solicitudRepository = solicitudRepository;
        this.resultadoRepository = resultadoRepository;
        this.buroCreditoPort = buroCreditoPort;
        this.scoringEngine = scoringEngine;
        this.dtiCalculator = dtiCalculator;
        this.capacidadPagoCalculator = capacidadPagoCalculator;
        this.amortizacionCalculator = amortizacionCalculator;
        this.tasaInteresAssigner = tasaInteresAssigner;
        this.dictamenResolver = dictamenResolver;
    }

    @Override
    public EvaluacionResponse evaluar(Long idSolicitud) {
        SolicitudCredito solicitud = solicitudRepository.buscarPorId(idSolicitud)
                .orElseThrow(() -> new SolicitudNotFoundException(idSolicitud));
        Cliente cliente = solicitud.getCliente();

        BuroCreditoResultado buro = buroCreditoPort.consultar(cliente.getNumeroDocumento());

        // Tasa conservadora usada UNICAMENTE para proyectar riesgo antes del dictamen.
        BigDecimal tasaMensualEvaluacion = amortizacionCalculator.convertirEaAMensual(TasaInteresAssigner.TASA_EVALUACION_EA);
        BigDecimal cuotaProyectada = amortizacionCalculator.calcularCuotaMensual(
                solicitud.getMontoSolicitado(), tasaMensualEvaluacion, solicitud.getPlazoMeses());

        BigDecimal dti = dtiCalculator.calcular(solicitud.getIngresosMensuales(), solicitud.getEgresosMensuales(), cuotaProyectada);
        BigDecimal capacidadResidual = capacidadPagoCalculator.calcularCapacidadResidual(
                solicitud.getIngresosMensuales(), solicitud.getEgresosMensuales(), cuotaProyectada);

        int edad = cliente.calcularEdad(LocalDate.now());

        RiskRuleContext context = new RiskRuleContext(
                cliente, solicitud, buro, cuotaProyectada, dti, capacidadResidual,
                CapacidadPagoCalculator.UMBRAL_SUBSISTENCIA_DEFAULT, edad);

        EvaluacionScoring evaluacionScoring = scoringEngine.evaluar(context);

        EvaluacionContext evaluacionContext = new EvaluacionContext(
                evaluacionScoring.getScoreTotal(), dti, evaluacionScoring.tieneCausalDeRechazo());
        Dictamen dictamen = dictamenResolver.resolver(evaluacionContext);

        BigDecimal tasaFinalEA;
        BigDecimal cuotaFinal;
        if (dictamen == Dictamen.APROBADO) {
            tasaFinalEA = tasaInteresAssigner.asignarTasaPreferencial(buro.getCalificacion());
            BigDecimal tasaMensualFinal = amortizacionCalculator.convertirEaAMensual(tasaFinalEA);
            cuotaFinal = amortizacionCalculator.calcularCuotaMensual(
                    solicitud.getMontoSolicitado(), tasaMensualFinal, solicitud.getPlazoMeses());
        } else {
            tasaFinalEA = TasaInteresAssigner.TASA_EVALUACION_EA;
            cuotaFinal = cuotaProyectada;
        }

        ResultadoEvaluacion resultado = new ResultadoEvaluacion(
                null,
                solicitud.getId(),
                evaluacionScoring.getScoreTotal(),
                dti,
                cuotaFinal,
                tasaFinalEA,
                dictamen,
                evaluacionScoring.getMotivos(),
                evaluacionScoring.getDetallePuntajePorRegla(),
                VERSION_MOTOR_REGLAS,
                LocalDateTime.now());

        ResultadoEvaluacion guardado = resultadoRepository.guardar(resultado);

        solicitud.marcarComoEvaluada();
        solicitudRepository.guardar(solicitud);

        return toResponse(guardado);
    }

    static EvaluacionResponse toResponse(ResultadoEvaluacion resultado) {
        List<EvaluacionResponse.MotivoResponse> motivos = resultado.getMotivos().stream()
                .map(m -> new EvaluacionResponse.MotivoResponse(m.getCodigo(), m.getDescripcion(), m.getTipo().name()))
                .toList();

        Map<String, Integer> detalle = new LinkedHashMap<>(resultado.getDetallePuntajePorRegla());

        return new EvaluacionResponse(
                resultado.getIdSolicitud(),
                resultado.getScoreObtenido(),
                resultado.getDtiCalculado(),
                resultado.getCuotaEstimada(),
                resultado.getTasaInteresEA(),
                resultado.getDictamen(),
                motivos,
                detalle,
                resultado.getVersionMotorReglas(),
                resultado.getFechaEvaluacion());
    }
}
