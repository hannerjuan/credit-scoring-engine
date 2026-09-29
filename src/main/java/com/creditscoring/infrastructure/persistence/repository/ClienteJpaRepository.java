package com.creditscoring.infrastructure.persistence.repository;

import com.creditscoring.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByNumeroDocumento(String numeroDocumento);
}
