package com.creditscoring.infrastructure.persistence.adapter;

import com.creditscoring.application.port.out.SolicitudRepositoryPort;
import com.creditscoring.domain.model.Cliente;
import com.creditscoring.domain.model.SolicitudCredito;
import com.creditscoring.infrastructure.persistence.entity.ClienteEntity;
import com.creditscoring.infrastructure.persistence.entity.SolicitudCreditoEntity;
import com.creditscoring.infrastructure.persistence.repository.SolicitudCreditoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SolicitudRepositoryAdapter implements SolicitudRepositoryPort {

    private final SolicitudCreditoJpaRepository jpaRepository;

    public SolicitudRepositoryAdapter(SolicitudCreditoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SolicitudCredito guardar(SolicitudCredito solicitud) {
        ClienteEntity clienteEntity = new ClienteEntity(
                solicitud.getCliente().getId(),
                solicitud.getCliente().getNumeroDocumento(),
                solicitud.getCliente().getNombreCompleto(),
                solicitud.getCliente().getFechaNacimiento(),
                solicitud.getCliente().getTipoContrato(),
                solicitud.getCliente().getAntiguedadMeses(),
                solicitud.getCliente().getActividadEconomica());

        SolicitudCreditoEntity entity = new SolicitudCreditoEntity(
                solicitud.getId(),
                clienteEntity,
                solicitud.getMontoSolicitado(),
                solicitud.getPlazoMeses(),
                solicitud.getIngresosMensuales(),
                solicitud.getEgresosMensuales(),
                solicitud.getEstado(),
                solicitud.getFechaSolicitud());

        SolicitudCreditoEntity guardada = jpaRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<SolicitudCredito> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private SolicitudCredito toDomain(SolicitudCreditoEntity e) {
        ClienteEntity ce = e.getCliente();
        Cliente cliente = new Cliente(ce.getId(), ce.getNumeroDocumento(), ce.getNombreCompleto(),
                ce.getFechaNacimiento(), ce.getTipoContrato(), ce.getAntiguedadMeses(), ce.getActividadEconomica());

        return new SolicitudCredito(e.getId(), cliente, e.getMontoSolicitado(), e.getPlazoMeses(),
                e.getIngresosMensuales(), e.getEgresosMensuales(), e.getEstado(), e.getFechaSolicitud());
    }
}
