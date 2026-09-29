package com.creditscoring.domain.model;

import java.util.Objects;

/**
 * Motivo puntual generado por una regla de riesgo durante la evaluacion.
 * tipo distingue entre un motivo que causa rechazo duro (RECHAZO) y uno
 * meramente informativo/de alerta (ALERTA) usado para trazabilidad.
 */
public final class MotivoEvaluacion {

    public enum Tipo { RECHAZO, ALERTA, INFORMATIVO }

    private final String codigo;
    private final String descripcion;
    private final Tipo tipo;
    private final String reglaOrigen;

    public MotivoEvaluacion(String codigo, String descripcion, Tipo tipo, String reglaOrigen) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.reglaOrigen = reglaOrigen;
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public Tipo getTipo() { return tipo; }
    public String getReglaOrigen() { return reglaOrigen; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MotivoEvaluacion)) return false;
        MotivoEvaluacion that = (MotivoEvaluacion) o;
        return Objects.equals(codigo, that.codigo) && Objects.equals(reglaOrigen, that.reglaOrigen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, reglaOrigen);
    }

    @Override
    public String toString() {
        return "[" + tipo + "] " + codigo + ": " + descripcion;
    }
}
