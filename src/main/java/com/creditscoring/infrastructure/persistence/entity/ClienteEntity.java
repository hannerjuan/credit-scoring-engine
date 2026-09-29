package com.creditscoring.infrastructure.persistence.entity;

import com.creditscoring.domain.model.TipoContrato;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 30)
    private String numeroDocumento;

    @Column(name = "nombre_completo", nullable = false, length = 200)
    private String nombreCompleto;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contrato", nullable = false, length = 40)
    private TipoContrato tipoContrato;

    @Column(name = "antiguedad_meses", nullable = false)
    private int antiguedadMeses;

    @Column(name = "actividad_economica", length = 150)
    private String actividadEconomica;
}
