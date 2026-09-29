package com.creditscoring.infrastructure.config;

import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.impl.*;
import com.creditscoring.domain.service.*;
import com.creditscoring.domain.specification.AprobadoSpecification;
import com.creditscoring.domain.specification.DictamenResolver;
import com.creditscoring.domain.specification.RechazadoSpecification;
import com.creditscoring.domain.specification.RevisionManualSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * Unico lugar de la aplicacion donde se decide QUE reglas de riesgo estan
 * activas y con que ponderacion/umbral (ademas de los cortes de dictamen).
 * Activar, desactivar o reponderar una politica es cuestion de editar esta
 * configuracion; el ScoringEngine y las clases de dominio nunca cambian.
 *
 * Los umbrales de corte tienen valores por defecto alineados al enunciado
 * (score >= 700 y DTI <= 40% para APROBADO; score < 400 o cualquier causal
 * dura para RECHAZADO; el resto cae en REVISION_MANUAL) pero son
 * configurables via application.yml (prefijo scoring.*).
 */
@Configuration
public class ScoringEngineConfig {

    @Bean
    public DtiRiskRule dtiRiskRule(
            @Value("${scoring.dti.umbral-optimo:35}") BigDecimal umbralOptimo,
            @Value("${scoring.dti.umbral-maximo:45}") BigDecimal umbralMaximo,
            @Value("${scoring.dti.puntos-optimo:300}") int puntosOptimo,
            @Value("${scoring.dti.puntos-intermedio:150}") int puntosIntermedio,
            @Value("${scoring.dti.activa:true}") boolean activa) {
        return new DtiRiskRule(umbralOptimo, umbralMaximo, puntosOptimo, puntosIntermedio, activa);
    }

    @Bean
    public MoraActivaRiskRule moraActivaRiskRule(@Value("${scoring.mora.activa:true}") boolean activa) {
        return new MoraActivaRiskRule(activa);
    }

    @Bean
    public ScoreBuroRiskRule scoreBuroRiskRule(@Value("${scoring.score-buro.activa:true}") boolean activa) {
        return new ScoreBuroRiskRule(activa);
    }

    @Bean
    public EstabilidadLaboralRiskRule estabilidadLaboralRiskRule(
            @Value("${scoring.estabilidad-laboral.activa:true}") boolean activa) {
        return new EstabilidadLaboralRiskRule(activa);
    }

    @Bean
    public HistorialCrediticioRiskRule historialCrediticioRiskRule(
            @Value("${scoring.historial-crediticio.activa:true}") boolean activa) {
        return new HistorialCrediticioRiskRule(activa);
    }

    @Bean
    public NivelIngresosRiskRule nivelIngresosRiskRule(
            @Value("${scoring.nivel-ingresos.umbral-alto:5000000}") BigDecimal umbralAlto,
            @Value("${scoring.nivel-ingresos.umbral-medio:2000000}") BigDecimal umbralMedio,
            @Value("${scoring.nivel-ingresos.activa:true}") boolean activa) {
        return new NivelIngresosRiskRule(umbralAlto, umbralMedio, activa);
    }

    @Bean
    public CapacidadPagoResidualRiskRule capacidadPagoResidualRiskRule(
            @Value("${scoring.capacidad-pago.activa:true}") boolean activa) {
        return new CapacidadPagoResidualRiskRule(activa);
    }

    @Bean
    public EdadMinimaRiskRule edadMinimaRiskRule(
            @Value("${scoring.edad-minima.valor:18}") int edadMinima,
            @Value("${scoring.edad-minima.activa:true}") boolean activa) {
        return new EdadMinimaRiskRule(edadMinima, activa);
    }

    /**
     * Lista de politicas activas en el orden en que se auditan. Reordenar,
     * agregar o quitar una regla aqui es todo lo que se necesita: el
     * ScoringEngine las trata de forma polimorfica.
     */
    @Bean
    public ScoringEngine scoringEngine(DtiRiskRule dtiRiskRule,
                                        MoraActivaRiskRule moraActivaRiskRule,
                                        ScoreBuroRiskRule scoreBuroRiskRule,
                                        EstabilidadLaboralRiskRule estabilidadLaboralRiskRule,
                                        HistorialCrediticioRiskRule historialCrediticioRiskRule,
                                        NivelIngresosRiskRule nivelIngresosRiskRule,
                                        CapacidadPagoResidualRiskRule capacidadPagoResidualRiskRule,
                                        EdadMinimaRiskRule edadMinimaRiskRule) {
        List<RiskRule> reglas = List.of(
                edadMinimaRiskRule,
                moraActivaRiskRule,
                capacidadPagoResidualRiskRule,
                dtiRiskRule,
                scoreBuroRiskRule,
                historialCrediticioRiskRule,
                estabilidadLaboralRiskRule,
                nivelIngresosRiskRule);
        return new ScoringEngine(reglas);
    }

    @Bean
    public DtiCalculator dtiCalculator() {
        return new DtiCalculator();
    }

    @Bean
    public CapacidadPagoCalculator capacidadPagoCalculator() {
        return new CapacidadPagoCalculator();
    }

    @Bean
    public AmortizacionFrancesaCalculator amortizacionFrancesaCalculator() {
        return new AmortizacionFrancesaCalculator();
    }

    @Bean
    public TasaInteresAssigner tasaInteresAssigner() {
        return new TasaInteresAssigner();
    }

    @Bean
    public DictamenResolver dictamenResolver(
            @Value("${scoring.dictamen.score-corte-aprobacion:700}") int scoreCorteAprobacion,
            @Value("${scoring.dictamen.dti-maximo-aprobacion:40}") BigDecimal dtiMaximoAprobacion,
            @Value("${scoring.dictamen.score-minimo-absoluto:400}") int scoreMinimoAbsoluto) {
        RechazadoSpecification rechazado = new RechazadoSpecification(scoreMinimoAbsoluto);
        AprobadoSpecification aprobado = new AprobadoSpecification(scoreCorteAprobacion, dtiMaximoAprobacion);
        RevisionManualSpecification revisionManual = new RevisionManualSpecification();
        return new DictamenResolver(rechazado, aprobado, revisionManual);
    }
}
