package com.creditscoring.infrastructure.persistence.adapter;

import com.creditscoring.application.port.out.ResultadoEvaluacionRepositoryPort;
import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.model.ResultadoEvaluacion;
import com.creditscoring.infrastructure.persistence.entity.ResultadoEvaluacionEntity;
import com.creditscoring.infrastructure.persistence.repository.ResultadoEvaluacionJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Persiste el ResultadoEvaluacion de forma inmutable: guardar() siempre
 * inserta un registro nuevo (nunca actualiza uno existente), garantizando el
 * historico de auditoria exigido por el requerimiento de trazabilidad. Los
 * motivos y el desglose de puntaje por regla se serializan como JSON en la
 * columna detalles_motivos (JSONB en Postgres).
 */
@Component
public class ResultadoEvaluacionRepositoryAdapter implements ResultadoEvaluacionRepositoryPort {

    private final ResultadoEvaluacionJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public ResultadoEvaluacionRepositoryAdapter(ResultadoEvaluacionJpaRepository jpaRepository,
                                                 ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResultadoEvaluacion guardar(ResultadoEvaluacion resultado) {
        ResultadoEvaluacionEntity entity = new ResultadoEvaluacionEntity();
        entity.setIdSolicitud(resultado.getIdSolicitud());
        entity.setScoreObtenido(resultado.getScoreObtenido());
        entity.setDtiCalculado(resultado.getDtiCalculado());
        entity.setCuotaEstimada(resultado.getCuotaEstimada());
        entity.setTasaInteresEA(resultado.getTasaInteresEA());
        entity.setDictamen(resultado.getDictamen());
        entity.setDetallesMotivosJson(serializarDetalle(resultado));
        entity.setVersionMotorReglas(resultado.getVersionMotorReglas());
        entity.setFechaEvaluacion(resultado.getFechaEvaluacion());

        ResultadoEvaluacionEntity guardado = jpaRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public Optional<ResultadoEvaluacion> buscarPorIdSolicitud(Long idSolicitud) {
        return jpaRepository.findByIdSolicitud(idSolicitud).map(this::toDomain);
    }

    private String serializarDetalle(ResultadoEvaluacion resultado) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("motivos", resultado.getMotivos().stream().map(this::motivoAMapa).toList());
            payload.put("detallePuntajePorRegla", resultado.getDetallePuntajePorRegla());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible serializar el detalle de auditoria de la evaluacion", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ResultadoEvaluacion toDomain(ResultadoEvaluacionEntity e) {
        List<MotivoEvaluacion> motivos = List.of();
        Map<String, Integer> detallePuntaje = Map.of();
        try {
            Map<String, Object> payload = objectMapper.readValue(e.getDetallesMotivosJson(), Map.class);
            List<Map<String, String>> motivosRaw = (List<Map<String, String>>) payload.getOrDefault("motivos", List.of());
            motivos = motivosRaw.stream()
                    .map(m -> new MotivoEvaluacion(m.get("codigo"), m.get("descripcion"),
                            MotivoEvaluacion.Tipo.valueOf(m.get("tipo")), m.get("reglaOrigen")))
                    .toList();
            Map<String, Object> detalleRaw = (Map<String, Object>) payload.getOrDefault("detallePuntajePorRegla", Map.of());
            Map<String, Integer> detalle = new LinkedHashMap<>();
            detalleRaw.forEach((k, v) -> detalle.put(k, ((Number) v).intValue()));
            detallePuntaje = detalle;
        } catch (Exception ex) {
            // Datos historicos corruptos o en formato inesperado no deben tumbar la consulta;
            // se retorna el resultado sin el detalle enriquecido.
        }

        return new ResultadoEvaluacion(
                e.getId(), e.getIdSolicitud(), e.getScoreObtenido(), e.getDtiCalculado(), e.getCuotaEstimada(),
                e.getTasaInteresEA(), e.getDictamen(), motivos, detallePuntaje, e.getVersionMotorReglas(),
                e.getFechaEvaluacion());
    }

    private Map<String, String> motivoAMapa(MotivoEvaluacion m) {
        Map<String, String> mapa = new LinkedHashMap<>();
        mapa.put("codigo", m.getCodigo());
        mapa.put("descripcion", m.getDescripcion());
        mapa.put("tipo", m.getTipo().name());
        mapa.put("reglaOrigen", m.getReglaOrigen());
        return mapa;
    }
}
