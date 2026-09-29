package com.creditscoring.application.service;

import com.creditscoring.application.dto.EvaluacionResponse;
import com.creditscoring.application.port.out.BuroCreditoPort;
import com.creditscoring.application.port.out.ResultadoEvaluacionRepositoryPort;
import com.creditscoring.application.port.out.SolicitudRepositoryPort;
import com.creditscoring.domain.exception.SolicitudNotFoundException;
import com.creditscoring.domain.model.*;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.impl.*;
import com.creditscoring.domain.service.*;
import com.creditscoring.domain.specification.AprobadoSpecification;
import com.creditscoring.domain.specification.DictamenResolver;
import com.creditscoring.domain.specification.RechazadoSpecification;
import com.creditscoring.domain.specification.RevisionManualSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Prueba de integracion de la capa de aplicacion: usa las RiskRule y
 * Specification REALES (motor de scoring completo), pero simula con
 * Mockito los puertos de salida (persistencia y buro), verificando que el
 * caso de uso produce el dictamen correcto en escenarios representativos
 * de cada categoria, incluyendo el veto absoluto de una causal dura.
 */
@ExtendWith(MockitoExtension.class)
class EvaluarSolicitudServiceTest {

    @Mock private SolicitudRepositoryPort solicitudRepository;
    @Mock private ResultadoEvaluacionRepositoryPort resultadoRepository;
    @Mock private BuroCreditoPort buroCreditoPort;

    private EvaluarSolicitudService service;

    @BeforeEach
    void setUp() {
        List<RiskRule> reglas = List.of(
                new EdadMinimaRiskRule(),
                new MoraActivaRiskRule(),
                new CapacidadPagoResidualRiskRule(),
                new DtiRiskRule(),
                new ScoreBuroRiskRule(),
                new HistorialCrediticioRiskRule(),
                new EstabilidadLaboralRiskRule(),
                new NivelIngresosRiskRule());

        DictamenResolver dictamenResolver = new DictamenResolver(
                new RechazadoSpecification(400),
                new AprobadoSpecification(700, BigDecimal.valueOf(40)),
                new RevisionManualSpecification());

        service = new EvaluarSolicitudService(
                solicitudRepository,
                resultadoRepository,
                buroCreditoPort,
                new ScoringEngine(reglas),
                new DtiCalculator(),
                new CapacidadPagoCalculator(),
                new AmortizacionFrancesaCalculator(),
                new TasaInteresAssigner(),
                dictamenResolver);

        // El adaptador de persistencia simplemente retorna lo que recibe (comportamiento tipico de un guardar()).
        when(resultadoRepository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private SolicitudCredito construirSolicitud(TipoContrato tipoContrato, int antiguedadMeses,
                                                 BigDecimal ingresos, BigDecimal egresos,
                                                 BigDecimal monto, int plazoMeses, int edad) {
        Cliente cliente = new Cliente(1L, "111", "Cliente Test", LocalDate.now().minusYears(edad),
                tipoContrato, antiguedadMeses, "Comercio");
        return new SolicitudCredito(1L, cliente, monto, plazoMeses, ingresos, egresos,
                EstadoSolicitud.PENDIENTE, LocalDateTime.now());
    }

    @Test
    @DisplayName("Perfil solido (buen score, buena calificacion, DTI bajo) => APROBADO con tasa preferencial")
    void perfilSolidoQuedaAprobado() {
        SolicitudCredito solicitud = construirSolicitud(TipoContrato.INDEFINIDO, 24,
                BigDecimal.valueOf(10_000_000), BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(5_000_000), 24, 30);
        when(solicitudRepository.buscarPorId(1L)).thenReturn(Optional.of(solicitud));
        when(buroCreditoPort.consultar("111"))
                .thenReturn(new BuroCreditoResultado(CalificacionBuro.A, 800, 0, LocalDateTime.now()));

        EvaluacionResponse response = service.evaluar(1L);

        assertThat(response.getDictamen()).isEqualTo(Dictamen.APROBADO);
        assertThat(response.getScoreObtenido()).isGreaterThanOrEqualTo(700);
        assertThat(response.getDtiCalculado()).isLessThanOrEqualTo(BigDecimal.valueOf(40));
        // Tasa preferencial de calificacion A (12% EA), NO la tasa conservadora de evaluacion (24%).
        assertThat(response.getTasaInteresEA()).isEqualByComparingTo(BigDecimal.valueOf(0.12));
        assertThat(response.getMotivos()).isEmpty();
    }

    @Test
    @DisplayName("Cualquier mora activa rechaza la solicitud sin importar cuan bueno sea el resto del perfil")
    void moraActivaRechazaAunConPerfilExcelente() {
        SolicitudCredito solicitud = construirSolicitud(TipoContrato.INDEFINIDO, 24,
                BigDecimal.valueOf(10_000_000), BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(5_000_000), 24, 30);
        when(solicitudRepository.buscarPorId(1L)).thenReturn(Optional.of(solicitud));
        when(buroCreditoPort.consultar("111"))
                .thenReturn(new BuroCreditoResultado(CalificacionBuro.A, 900, 1, LocalDateTime.now()));

        EvaluacionResponse response = service.evaluar(1L);

        assertThat(response.getDictamen()).isEqualTo(Dictamen.RECHAZADO);
        assertThat(response.getMotivos()).anyMatch(m -> m.getCodigo().equals("MORA_ACTIVA_DETECTADA"));
        // La tasa registrada para un RECHAZADO es la de evaluacion (no se asigna preferencial).
        assertThat(response.getTasaInteresEA()).isEqualByComparingTo(TasaInteresAssigner.TASA_EVALUACION_EA);
    }

    @Test
    @DisplayName("Perfil debil sin causales duras cae en zona gris => REVISION_MANUAL")
    void perfilDebilSinCausalesDurasQuedaEnRevisionManual() {
        SolicitudCredito solicitud = construirSolicitud(TipoContrato.PRESTACION_SERVICIOS, 5,
                BigDecimal.valueOf(1_500_000), BigDecimal.valueOf(400_000),
                BigDecimal.valueOf(3_000_000), 36, 30);
        when(solicitudRepository.buscarPorId(1L)).thenReturn(Optional.of(solicitud));
        when(buroCreditoPort.consultar("111"))
                .thenReturn(new BuroCreditoResultado(CalificacionBuro.C, 650, 0, LocalDateTime.now()));

        EvaluacionResponse response = service.evaluar(1L);

        assertThat(response.getDictamen()).isEqualTo(Dictamen.REVISION_MANUAL);
        assertThat(response.getScoreObtenido()).isBetween(400, 699);
    }

    @Test
    @DisplayName("Capacidad de pago insuficiente (residual por debajo de subsistencia) es causal de rechazo")
    void capacidadDePagoInsuficienteRechaza() {
        // Ingresos apenas superan los egresos: cualquier cuota adicional hunde el residual bajo el umbral.
        SolicitudCredito solicitud = construirSolicitud(TipoContrato.INDEFINIDO, 24,
                BigDecimal.valueOf(1_000_000), BigDecimal.valueOf(950_000),
                BigDecimal.valueOf(20_000_000), 12, 30);
        when(solicitudRepository.buscarPorId(1L)).thenReturn(Optional.of(solicitud));
        when(buroCreditoPort.consultar("111"))
                .thenReturn(new BuroCreditoResultado(CalificacionBuro.A, 800, 0, LocalDateTime.now()));

        EvaluacionResponse response = service.evaluar(1L);

        assertThat(response.getDictamen()).isEqualTo(Dictamen.RECHAZADO);
        assertThat(response.getMotivos()).anyMatch(m -> m.getCodigo().equals("CAPACIDAD_PAGO_INSUFICIENTE"));
    }

    @Test
    @DisplayName("Solicitud inexistente lanza SolicitudNotFoundException")
    void solicitudInexistenteLanzaExcepcion() {
        when(solicitudRepository.buscarPorId(999L)).thenReturn(Optional.empty());
        assertThrows(SolicitudNotFoundException.class, () -> service.evaluar(999L));
    }
}
