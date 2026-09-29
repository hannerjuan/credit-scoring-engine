package com.creditscoring.infrastructure.rest;

import com.creditscoring.application.dto.AmortizacionResponse;
import com.creditscoring.application.dto.EvaluacionResponse;
import com.creditscoring.application.dto.SolicitudCreditoRequest;
import com.creditscoring.application.dto.SolicitudCreditoResponse;
import com.creditscoring.application.port.in.ConsultarAmortizacionUseCase;
import com.creditscoring.application.port.in.ConsultarEvaluacionUseCase;
import com.creditscoring.application.port.in.EvaluarSolicitudUseCase;
import com.creditscoring.application.port.in.RegistrarSolicitudUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada (driving adapter) REST. Traduce HTTP <-> casos de uso;
 * no contiene ninguna regla de negocio.
 */
@RestController
@RequestMapping("/api/v1/credit-applications")
@Tag(name = "Solicitudes de Credito", description = "Registro, evaluacion de riesgo y amortizacion")
public class CreditApplicationController {

    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;
    private final EvaluarSolicitudUseCase evaluarSolicitudUseCase;
    private final ConsultarEvaluacionUseCase consultarEvaluacionUseCase;
    private final ConsultarAmortizacionUseCase consultarAmortizacionUseCase;

    public CreditApplicationController(RegistrarSolicitudUseCase registrarSolicitudUseCase,
                                        EvaluarSolicitudUseCase evaluarSolicitudUseCase,
                                        ConsultarEvaluacionUseCase consultarEvaluacionUseCase,
                                        ConsultarAmortizacionUseCase consultarAmortizacionUseCase) {
        this.registrarSolicitudUseCase = registrarSolicitudUseCase;
        this.evaluarSolicitudUseCase = evaluarSolicitudUseCase;
        this.consultarEvaluacionUseCase = consultarEvaluacionUseCase;
        this.consultarAmortizacionUseCase = consultarAmortizacionUseCase;
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva solicitud de credito")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud creada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion o reglas financieras basicas)")
    })
    public ResponseEntity<SolicitudCreditoResponse> registrar(@Valid @RequestBody SolicitudCreditoRequest request) {
        SolicitudCreditoResponse response = registrarSolicitudUseCase.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/evaluate")
    @Operation(summary = "Ejecuta el motor de scoring sobre la solicitud y genera el dictamen")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluacion ejecutada", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(name = "APROBADO", value = """
                                    {
                                      "idSolicitud": 101,
                                      "scoreObtenido": 780,
                                      "dtiCalculado": 28.50,
                                      "cuotaEstimada": 452301.12,
                                      "tasaInteresEA": 0.12,
                                      "dictamen": "APROBADO",
                                      "motivos": [],
                                      "detallePuntajePorRegla": {"DTI_RATIO": 300, "SCORE_BURO": 300, "HISTORIAL_CREDITICIO": 150},
                                      "versionMotorReglas": "scoring-engine-v1.0.0",
                                      "fechaEvaluacion": "2026-09-27T10:15:30"
                                    }
                                    """),
                            @ExampleObject(name = "RECHAZADO", value = """
                                    {
                                      "idSolicitud": 102,
                                      "scoreObtenido": 350,
                                      "dtiCalculado": 61.20,
                                      "cuotaEstimada": 980000.00,
                                      "tasaInteresEA": 0.24,
                                      "dictamen": "RECHAZADO",
                                      "motivos": [
                                        {"codigo": "DTI_EXCEDIDO", "descripcion": "El DTI calculado (61.20%) supera el umbral maximo permitido de 45%.", "tipo": "RECHAZO"},
                                        {"codigo": "MORA_ACTIVA_DETECTADA", "descripcion": "El buro de credito reporta 2 mora(s) activa(s).", "tipo": "RECHAZO"}
                                      ],
                                      "detallePuntajePorRegla": {"DTI_RATIO": 0, "SCORE_BURO": 50},
                                      "versionMotorReglas": "scoring-engine-v1.0.0",
                                      "fechaEvaluacion": "2026-09-27T10:20:11"
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "422", description = "La solicitud no puede procesarse (datos inconsistentes)")
    })
    public ResponseEntity<EvaluacionResponse> evaluar(@PathVariable Long id) {
        return ResponseEntity.ok(evaluarSolicitudUseCase.evaluar(id));
    }

    @GetMapping("/{id}/evaluation")
    @Operation(summary = "Consulta el dictamen de riesgo, score obtenido y desglose de motivos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluacion encontrada"),
            @ApiResponse(responseCode = "404", description = "Solicitud o evaluacion no encontrada")
    })
    public ResponseEntity<EvaluacionResponse> consultarEvaluacion(@PathVariable Long id) {
        return ResponseEntity.ok(consultarEvaluacionUseCase.consultar(id));
    }

    @GetMapping("/{id}/amortization")
    @Operation(summary = "Genera la tabla de amortizacion proyectada mes a mes (solo solicitudes APROBADAS)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tabla de amortizacion generada"),
            @ApiResponse(responseCode = "400", description = "La solicitud no esta en estado APROBADO")
    })
    public ResponseEntity<AmortizacionResponse> consultarAmortizacion(@PathVariable Long id) {
        return ResponseEntity.ok(consultarAmortizacionUseCase.consultar(id));
    }
}
