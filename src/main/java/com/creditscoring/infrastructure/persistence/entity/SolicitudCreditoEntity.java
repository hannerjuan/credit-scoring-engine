package com.creditscoring.infrastructure.persistence.entity;

import com.creditscoring.domain.model.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_credito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudCreditoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntity cliente;

    @Column(name = "monto_solicitado", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoSolicitado;

    @Column(name = "plazo_meses", nullable = false)
    private int plazoMeses;

    @Column(name = "ingresos_mensuales", nullable = false, precision = 18, scale = 2)
    private BigDecimal ingresosMensuales;

    @Column(name = "egresos_mensuales", nullable = false, precision = 18, scale = 2)
    private BigDecimal egresosMensuales;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSolicitud estado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;
}
