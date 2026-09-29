package com.creditscoring.infrastructure.buro;

import com.creditscoring.application.port.out.BuroCreditoPort;
import com.creditscoring.domain.model.BuroCreditoResultado;
import com.creditscoring.domain.model.CalificacionBuro;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Adaptador de salida MOCK que simula la consulta a una central de riesgo
 * estilo Datacredito/TransUnion. Es determinista por numero de documento
 * (misma cedula siempre retorna el mismo historial simulado), lo cual
 * facilita pruebas reproducibles, y respeta el rango de score 150-950
 * exigido por el requerimiento funcional.
 *
 * Reemplazar por una integracion real solo requiere una nueva implementacion
 * de BuroCreditoPort; ninguna otra capa se ve afectada (puerto de salida).
 */
@Component
public class BuroCreditoMockAdapter implements BuroCreditoPort {

    @Override
    public BuroCreditoResultado consultar(String numeroDocumento) {
        long seed = numeroDocumento == null ? 0L : numeroDocumento.hashCode();
        Random random = new Random(seed);

        int score = BuroCreditoResultado.SCORE_MIN
                + random.nextInt(BuroCreditoResultado.SCORE_MAX - BuroCreditoResultado.SCORE_MIN + 1);

        CalificacionBuro calificacion;
        if (score >= 750) {
            calificacion = CalificacionBuro.A;
        } else if (score >= 550) {
            calificacion = CalificacionBuro.B;
        } else {
            calificacion = CalificacionBuro.C;
        }

        // 80% de los perfiles simulados no tienen moras activas; el resto tiene entre 1 y 3.
        int numeroMorasActivas = random.nextInt(100) < 80 ? 0 : 1 + random.nextInt(3);

        return new BuroCreditoResultado(calificacion, score, numeroMorasActivas, LocalDateTime.now());
    }
}
