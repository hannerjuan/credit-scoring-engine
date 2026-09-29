package com.creditscoring.infrastructure.persistence.adapter;

import com.creditscoring.application.port.out.ClienteRepositoryPort;
import com.creditscoring.domain.model.Cliente;
import com.creditscoring.infrastructure.persistence.entity.ClienteEntity;
import com.creditscoring.infrastructure.persistence.repository.ClienteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Cliente> buscarPorNumeroDocumento(String numeroDocumento) {
        return jpaRepository.findByNumeroDocumento(numeroDocumento).map(this::toDomain);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity(
                cliente.getId(),
                cliente.getNumeroDocumento(),
                cliente.getNombreCompleto(),
                cliente.getFechaNacimiento(),
                cliente.getTipoContrato(),
                cliente.getAntiguedadMeses(),
                cliente.getActividadEconomica());
        ClienteEntity guardada = jpaRepository.save(entity);
        return toDomain(guardada);
    }

    private Cliente toDomain(ClienteEntity e) {
        return new Cliente(e.getId(), e.getNumeroDocumento(), e.getNombreCompleto(), e.getFechaNacimiento(),
                e.getTipoContrato(), e.getAntiguedadMeses(), e.getActividadEconomica());
    }
}
