package com.creditscoring.infrastructure.rest;

import com.creditscoring.domain.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejo uniforme de errores segun RFC 7807 (application/problem+json).
 * Cada tipo de excepcion de negocio se traduce a un ProblemDetail con un
 * "type" propio, evitando que la capa REST filtre stacktraces o mensajes
 * inconsistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TYPE_BASE = "https://api.creditscoring.com/problems/";

    @ExceptionHandler(ApplicantUnderageException.class)
    public ResponseEntity<ProblemDetail> handleApplicantUnderage(ApplicantUnderageException ex, WebRequest request) {
        ProblemDetail pd = build(HttpStatus.UNPROCESSABLE_ENTITY, "applicant-underage",
                "Solicitante menor de edad", ex.getMessage(), request);
        pd.setProperty("edadMinimaRequerida", ex.getEdadMinimaRequerida());
        pd.setProperty("edadActual", ex.getEdadActual());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(pd);
    }

    @ExceptionHandler(InvalidFinancialDataException.class)
    public ResponseEntity<ProblemDetail> handleInvalidFinancialData(InvalidFinancialDataException ex, WebRequest request) {
        ProblemDetail pd = build(HttpStatus.BAD_REQUEST, "invalid-financial-data",
                "Datos financieros invalidos", ex.getMessage(), request);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(SolicitudNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleSolicitudNotFound(SolicitudNotFoundException ex, WebRequest request) {
        ProblemDetail pd = build(HttpStatus.NOT_FOUND, "solicitud-not-found",
                "Solicitud no encontrada", ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(EvaluacionNoDisponibleException.class)
    public ResponseEntity<ProblemDetail> handleEvaluacionNoDisponible(EvaluacionNoDisponibleException ex, WebRequest request) {
        ProblemDetail pd = build(HttpStatus.NOT_FOUND, "evaluacion-no-disponible",
                "Evaluacion no disponible", ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(AmortizacionNoDisponibleException.class)
    public ResponseEntity<ProblemDetail> handleAmortizacionNoDisponible(AmortizacionNoDisponibleException ex, WebRequest request) {
        ProblemDetail pd = build(HttpStatus.BAD_REQUEST, "amortizacion-no-disponible",
                "Amortizacion no disponible", ex.getMessage(), request);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, WebRequest request) {
        ProblemDetail pd = build(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error",
                "Error interno inesperado", "Ocurrio un error inesperado al procesar la solicitud.", request);
        return ResponseEntity.internalServerError().body(pd);
    }

    /** Sobrescribe el manejo por defecto de Bean Validation (@Valid) para responder tambien en formato RFC 7807. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                    HttpHeaders headers, HttpStatusCode status,
                                                                    WebRequest request) {
        Map<String, String> erroresPorCampo = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erroresPorCampo.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail pd = build(HttpStatus.BAD_REQUEST, "validation-error",
                "Error de validacion", "Uno o mas campos no cumplen las reglas de validacion.", request);
        pd.setProperty("erroresPorCampo", erroresPorCampo);
        return ResponseEntity.badRequest().body(pd);
    }

    private ProblemDetail build(HttpStatus status, String typeSlug, String title, String detail, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setType(URI.create(TYPE_BASE + typeSlug));
        pd.setTitle(title);
        pd.setProperty("timestamp", Instant.now());
        String path = request.getDescription(false).replace("uri=", "");
        pd.setInstance(URI.create(path));
        return pd;
    }
}
