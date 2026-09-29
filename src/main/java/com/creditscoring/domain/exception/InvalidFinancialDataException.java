package com.creditscoring.domain.exception;

/**
 * Se lanza cuando los datos financieros de la solicitud son estructuralmente
 * invalidos para poder ejecutar el motor de scoring (montos negativos,
 * plazos fuera de rango, ingresos en cero, etc.), mas alla de la validacion
 * sintactica de Bean Validation en la capa REST.
 */
public class InvalidFinancialDataException extends RuntimeException {

    public InvalidFinancialDataException(String message) {
        super(message);
    }
}
