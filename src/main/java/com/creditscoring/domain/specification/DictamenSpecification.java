package com.creditscoring.domain.specification;

/**
 * Contrato del patron Specification: encapsula una regla de negocio de
 * "es satisfecha por" de forma componible, en lugar de un bloque de
 * if-else anidados dentro del servicio de aplicacion.
 */
public interface DictamenSpecification {

    boolean isSatisfiedBy(EvaluacionContext context);

    /** Codigo del dictamen que esta Specification representa. */
    String getNombre();
}
