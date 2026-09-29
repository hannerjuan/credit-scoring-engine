package com.creditscoring.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Entidad de dominio pura. No tiene anotaciones de persistencia ni de
 * ningun framework: es un POJO tal como exige la arquitectura hexagonal.
 */
public class Cliente {

    private final Long id;
    private final String numeroDocumento;
    private final String nombreCompleto;
    private final LocalDate fechaNacimiento;
    private final TipoContrato tipoContrato;
    private final int antiguedadMeses;
    private final String actividadEconomica;

    public Cliente(Long id, String numeroDocumento, String nombreCompleto, LocalDate fechaNacimiento,
                    TipoContrato tipoContrato, int antiguedadMeses, String actividadEconomica) {
        this.id = id;
        this.numeroDocumento = Objects.requireNonNull(numeroDocumento, "numeroDocumento es obligatorio");
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "nombreCompleto es obligatorio");
        this.fechaNacimiento = Objects.requireNonNull(fechaNacimiento, "fechaNacimiento es obligatoria");
        this.tipoContrato = Objects.requireNonNull(tipoContrato, "tipoContrato es obligatorio");
        this.antiguedadMeses = antiguedadMeses;
        this.actividadEconomica = actividadEconomica;
    }

    /** Calcula la edad actual del cliente en anios completos, a la fecha dada. */
    public int calcularEdad(LocalDate fechaReferencia) {
        return Period.between(fechaNacimiento, fechaReferencia).getYears();
    }

    public Long getId() { return id; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public TipoContrato getTipoContrato() { return tipoContrato; }
    public int getAntiguedadMeses() { return antiguedadMeses; }
    public String getActividadEconomica() { return actividadEconomica; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(numeroDocumento, cliente.numeroDocumento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroDocumento);
    }
}
