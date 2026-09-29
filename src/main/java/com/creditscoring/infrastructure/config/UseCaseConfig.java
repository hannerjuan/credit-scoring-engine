package com.creditscoring.infrastructure.config;

import com.creditscoring.application.port.out.BuroCreditoPort;
import com.creditscoring.application.port.out.ClienteRepositoryPort;
import com.creditscoring.application.port.out.ResultadoEvaluacionRepositoryPort;
import com.creditscoring.application.port.out.SolicitudRepositoryPort;
import com.creditscoring.application.service.ConsultarAmortizacionService;
import com.creditscoring.application.service.ConsultarEvaluacionService;
import com.creditscoring.application.service.EvaluarSolicitudService;
import com.creditscoring.application.service.RegistrarSolicitudService;
import com.creditscoring.domain.service.AmortizacionFrancesaCalculator;
import com.creditscoring.domain.service.CapacidadPagoCalculator;
import com.creditscoring.domain.service.DtiCalculator;
import com.creditscoring.domain.service.ScoringEngine;
import com.creditscoring.domain.service.TasaInteresAssigner;
import com.creditscoring.domain.specification.DictamenResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ensambla los casos de uso (capa de aplicacion) con sus puertos de salida
 * concretos (adaptadores de persistencia y el mock de buro). Los servicios
 * de aplicacion no llevan anotaciones de Spring: son POJOs orquestadores,
 * consistente con mantener el nucleo libre de framework.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarSolicitudService registrarSolicitudService(ClienteRepositoryPort clienteRepository,
                                                                 SolicitudRepositoryPort solicitudRepository) {
        return new RegistrarSolicitudService(clienteRepository, solicitudRepository);
    }

    @Bean
    public EvaluarSolicitudService evaluarSolicitudService(SolicitudRepositoryPort solicitudRepository,
                                                             ResultadoEvaluacionRepositoryPort resultadoRepository,
                                                             BuroCreditoPort buroCreditoPort,
                                                             ScoringEngine scoringEngine,
                                                             DtiCalculator dtiCalculator,
                                                             CapacidadPagoCalculator capacidadPagoCalculator,
                                                             AmortizacionFrancesaCalculator amortizacionCalculator,
                                                             TasaInteresAssigner tasaInteresAssigner,
                                                             DictamenResolver dictamenResolver) {
        return new EvaluarSolicitudService(solicitudRepository, resultadoRepository, buroCreditoPort, scoringEngine,
                dtiCalculator, capacidadPagoCalculator, amortizacionCalculator, tasaInteresAssigner, dictamenResolver);
    }

    @Bean
    public ConsultarEvaluacionService consultarEvaluacionService(SolicitudRepositoryPort solicitudRepository,
                                                                  ResultadoEvaluacionRepositoryPort resultadoRepository) {
        return new ConsultarEvaluacionService(solicitudRepository, resultadoRepository);
    }

    @Bean
    public ConsultarAmortizacionService consultarAmortizacionService(
            SolicitudRepositoryPort solicitudRepository,
            ResultadoEvaluacionRepositoryPort resultadoRepository,
            AmortizacionFrancesaCalculator amortizacionCalculator) {
        return new ConsultarAmortizacionService(solicitudRepository, resultadoRepository, amortizacionCalculator);
    }
}
