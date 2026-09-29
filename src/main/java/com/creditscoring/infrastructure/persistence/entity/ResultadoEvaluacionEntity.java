package com.creditscoring.infrastructure.persistence.entity;

import com.creditscoring.domain.model.Dictamen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * detallesMotivosJson guarda, en formato JSON, tanto la lista de motivos
 * (rechazo/alerta/informativo) como el desglose de puntaje por regla y la
 * version del motor aplicada: es el respaldo de auditoria inmutable exigido
 * por el requerimiento de trazabilidad.
 */
@Entity
@Table(name = "resultados_evaluacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEvaluacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_solicitud", nullable = false, unique = true)
    private Long idSolicitud;

    @Column(name = "score_obtenido", nullable = false)
    private int scoreObtenido;

    @Column(name = "dti_calculado", nullable = false, precision = 5, scale = 2)
    private BigDecimal dtiCalculado;

    @Column(name = "cuota_estimada", nullable = false, precision = 18, scale = 2)
    private BigDecimal cuotaEstimada;

    @Column(name = "tasa_interes_ea", nullable = false, precision = 7, scale = 4)
    private BigDecimal tasaInteresEA;

    @Enumerated(EnumType.STRING)
    @Column(name = "dictamen", nullable = false, length = 20)
    private Dictamen dictamen;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalles_motivos", nullable = false, columnDefinition = "jsonb")
    private String detallesMotivosJson;

    @Column(name = "version_motor_reglas", nullable = false, length = 60)
    private String versionMotorReglas;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;
}
