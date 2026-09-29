package com.creditscoring.application.service;

import com.creditscoring.application.dto.SolicitudCreditoRequest;
import com.creditscoring.application.dto.SolicitudCreditoResponse;
import com.creditscoring.application.port.out.ClienteRepositoryPort;
import com.creditscoring.application.port.out.SolicitudRepositoryPort;
import com.creditscoring.domain.exception.ApplicantUnderageException;
import com.creditscoring.domain.exception.InvalidFinancialDataException;
import com.creditscoring.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarSolicitudServiceTest {

    @Mock private ClienteRepositoryPort clienteRepository;
    @Mock private SolicitudRepositoryPort solicitudRepository;

    private RegistrarSolicitudService service;

    @BeforeEach
    void setUp() {
        service = new RegistrarSolicitudService(clienteRepository, solicitudRepository);
    }

    private SolicitudCreditoRequest requestValidoConEdad(int edad) {
        SolicitudCreditoRequest request = new SolicitudCreditoRequest();
        request.setNumeroDocumento("123");
        request.setNombreCompleto("Cliente Test");
        request.setFechaNacimiento(LocalDate.now().minusYears(edad).minusDays(1));
        request.setTipoContrato(TipoContrato.INDEFINIDO);
        request.setAntiguedadMeses(24);
        request.setActividadEconomica("Comercio");
        request.setIngresosMensuales(BigDecimal.valueOf(2_000_000));
        request.setEgresosMensuales(BigDecimal.valueOf(500_000));
        request.setMontoSolicitado(BigDecimal.valueOf(5_000_000));
        request.setPlazoMeses(24);
        return request;
    }

    @ParameterizedTest(name = "edad={0} => rechaza el registro por ser menor de edad")
    @DisplayName("Borde exacto: 17 anios rechaza, 18 anios se acepta (validado en el otro test)")
    @ValueSource(ints = {0, 5, 17})
    void rechazaMenoresDeEdad(int edad) {
        SolicitudCreditoRequest request = requestValidoConEdad(edad);
        assertThrows(ApplicantUnderageException.class, () -> service.registrar(request));
    }

    @Test
    @DisplayName("Acepta exactamente 18 anios (borde de mayoria de edad)")
    void aceptaExactamenteDieciochoAnios() {
        SolicitudCreditoRequest request = requestValidoConEdad(18);
        Cliente clienteGuardado = new Cliente(1L, "123", "Cliente Test", request.getFechaNacimiento(),
                TipoContrato.INDEFINIDO, 24, "Comercio");
        SolicitudCredito solicitudGuardada = new SolicitudCredito(10L, clienteGuardado,
                request.getMontoSolicitado(), 24, request.getIngresosMensuales(), request.getEgresosMensuales(),
                EstadoSolicitud.PENDIENTE, null);

        when(clienteRepository.buscarPorNumeroDocumento("123")).thenReturn(Optional.empty());
        when(clienteRepository.guardar(any())).thenReturn(clienteGuardado);
        when(solicitudRepository.guardar(any())).thenReturn(solicitudGuardada);

        SolicitudCreditoResponse response = service.registrar(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
    }

    @Test
    @DisplayName("Rechaza cuando los egresos mensuales superan los ingresos mensuales")
    void rechazaEgresosMayoresAIngresos() {
        SolicitudCreditoRequest request = requestValidoConEdad(30);
        request.setIngresosMensuales(BigDecimal.valueOf(1_000_000));
        request.setEgresosMensuales(BigDecimal.valueOf(1_500_000));

        assertThrows(InvalidFinancialDataException.class, () -> service.registrar(request));
    }
}
