package com.creditscoring.infrastructure.persistence.repository;

import com.creditscoring.infrastructure.persistence.entity.SolicitudCreditoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudCreditoJpaRepository extends JpaRepository<SolicitudCreditoEntity, Long> {
}
