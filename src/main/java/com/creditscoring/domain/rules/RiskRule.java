package com.creditscoring.domain.rules;

/**
 * Contrato Strategy para una politica de riesgo individual.
 *
 * El motor central (ScoringEngine) NUNCA conoce reglas de negocio concretas:
 * solo itera una lista de RiskRule y agrega sus resultados. Esto permite
 * activar, desactivar, reordenar o reponderar politicas SIN tocar la logica
 * central, cumpliendo el requerimiento de "motor de reglas desacoplado".
 */
public interface RiskRule {

    /** Evalua la regla contra el contexto dado y retorna su veredicto individual. */
    RiskRuleResult evaluate(RiskRuleContext context);

    /** Nombre unico y estable de la regla (se usa en detallePuntajePorRegla para auditoria). */
    String getNombre();

    /**
     * Permite activar/desactivar la regla en tiempo de ejecucion sin
     * modificar el ScoringEngine. Por defecto toda regla esta activa.
     */
    default boolean isActiva() {
        return true;
    }
}
