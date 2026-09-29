package com.creditscoring.infrastructure.persistence.repository;

import com.creditscoring.infrastructure.persistence.entity.ResultadoEvaluacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultadoEvaluacionJpaRepository extends JpaRepository<ResultadoEvaluacionEntity, Long> {

    Optional<ResultadoEvaluacionEntity> findByIdSolicitud(Long idSolicitud);
}
