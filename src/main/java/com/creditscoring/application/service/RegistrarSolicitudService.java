package com.creditscoring.application.service;

import com.creditscoring.application.dto.SolicitudCreditoRequest;
import com.creditscoring.application.dto.SolicitudCreditoResponse;
import com.creditscoring.application.port.in.RegistrarSolicitudUseCase;
import com.creditscoring.application.port.out.ClienteRepositoryPort;
import com.creditscoring.application.port.out.SolicitudRepositoryPort;
import com.creditscoring.domain.exception.ApplicantUnderageException;
import com.creditscoring.domain.exception.InvalidFinancialDataException;
import com.creditscoring.domain.model.Cliente;
import com.creditscoring.domain.model.EstadoSolicitud;
import com.creditscoring.domain.model.SolicitudCredito;

import java.time.LocalDate;

/**
 * Orquesta el registro de una nueva solicitud de credito. No contiene logica
 * financiera: delega toda regla de negocio de calculo al dominio y solo
 * coordina puertos de entrada/salida, tal como exige la arquitectura
 * hexagonal.
 */
public class RegistrarSolicitudService implements RegistrarSolicitudUseCase {

    private static final int EDAD_MINIMA_LEGAL = 18;

    private final ClienteRepositoryPort clienteRepository;
    private final SolicitudRepositoryPort solicitudRepository;

    public RegistrarSolicitudService(ClienteRepositoryPort clienteRepository,
                                      SolicitudRepositoryPort solicitudRepository) {
        this.clienteRepository = clienteRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public SolicitudCreditoResponse registrar(SolicitudCreditoRequest request) {
        validarDatosFinancieros(request);

        Cliente cliente = new Cliente(
                null,
                request.getNumeroDocumento(),
                request.getNombreCompleto(),
                request.getFechaNacimiento(),
                request.getTipoContrato(),
                request.getAntiguedadMeses(),
                request.getActividadEconomica());

        int edad = cliente.calcularEdad(LocalDate.now());
        if (edad < EDAD_MINIMA_LEGAL) {
            throw new ApplicantUnderageException(EDAD_MINIMA_LEGAL, edad);
        }

        Cliente clientePersistido = clienteRepository.buscarPorNumeroDocumento(request.getNumeroDocumento())
                .orElseGet(() -> clienteRepository.guardar(cliente));

        SolicitudCredito solicitud = new SolicitudCredito(
                null,
                clientePersistido,
                request.getMontoSolicitado(),
                request.getPlazoMeses(),
                request.getIngresosMensuales(),
                request.getEgresosMensuales(),
                EstadoSolicitud.PENDIENTE,
                null);

        SolicitudCredito guardada = solicitudRepository.guardar(solicitud);

        return new SolicitudCreditoResponse(
                guardada.getId(),
                clientePersistido.getNumeroDocumento(),
                guardada.getMontoSolicitado(),
                guardada.getPlazoMeses(),
                guardada.getEstado(),
                guardada.getFechaSolicitud());
    }

    private void validarDatosFinancieros(SolicitudCreditoRequest request) {
        if (request.getEgresosMensuales().compareTo(request.getIngresosMensuales()) > 0) {
            throw new InvalidFinancialDataException(
                    "Los egresos mensuales (" + request.getEgresosMensuales()
                            + ") no pueden superar los ingresos mensuales (" + request.getIngresosMensuales() + ").");
        }
        if (request.getMontoSolicitado().signum() <= 0) {
            throw new InvalidFinancialDataException("El monto solicitado debe ser mayor a cero.");
        }
        if (request.getPlazoMeses() <= 0) {
            throw new InvalidFinancialDataException("El plazo en meses debe ser mayor a cero.");
        }
    }
}
