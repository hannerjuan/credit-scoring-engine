package com.creditscoring.domain.exception;

public class SolicitudNotFoundException extends RuntimeException {

    public SolicitudNotFoundException(Long id) {
        super("No existe una solicitud de credito con id " + id);
    }
}
