package com.creditscoring.domain.specification;

import com.creditscoring.domain.model.Dictamen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Valida la matriz de decision completa del dictamen final, incluyendo los
 * casos de borde explicitamente pedidos: clientes justo en el limite del
 * puntaje de corte (700) y del DTI maximo de aprobacion (40%).
 */
class DictamenResolverTest {

    // score corte aprobacion=700, dti maximo aprobacion=40%, score minimo absoluto=400 (valores por defecto del producto)
    private final DictamenResolver resolver = new DictamenResolver(
            new RechazadoSpecification(400),
            new AprobadoSpecification(700, BigDecimal.valueOf(40)),
            new RevisionManualSpecification());

    @ParameterizedTest(name = "score={0}, dti={1}%, causalRechazo={2} => {3}")
    @DisplayName("Matriz de decision: APROBADO / RECHAZADO / REVISION_MANUAL")
    @CsvSource({
            // score, dti, tieneCausalRechazo, dictamenEsperado
            "800, 20, false, APROBADO",
            "700, 40, false, APROBADO",     // borde exacto de ambos cortes: aprobado
            "700, 40.01, false, REVISION_MANUAL", // score ok pero DTI se pasa por una centesima
            "699, 30, false, REVISION_MANUAL",    // un punto por debajo del corte de score
            "750, 20, true, RECHAZADO",     // causal dura vence incluso con score/DTI excelentes
            "399, 10, false, RECHAZADO",    // por debajo del score minimo absoluto
            "400, 10, false, REVISION_MANUAL", // borde exacto del minimo absoluto: ya no es rechazo automatico
            "500, 38, false, REVISION_MANUAL",
            "0, 100, true, RECHAZADO"
    })
    void resuelveDictamenSegunMatriz(int score, String dti, boolean tieneCausalRechazo, String dictamenEsperado) {
        EvaluacionContext context = new EvaluacionContext(score, new BigDecimal(dti), tieneCausalRechazo);

        Dictamen resultado = resolver.resolver(context);

        assertThat(resultado).isEqualTo(Dictamen.valueOf(dictamenEsperado));
    }

    @ParameterizedTest(name = "causalRechazo tiene siempre prioridad, incluso con score={0}")
    @DisplayName("Una causal de rechazo tiene veto absoluto sin importar cuan alto sea el score")
    @CsvSource({"700", "800", "950", "1000"})
    void causalDeRechazoTienePrioridadAbsoluta(int scoreAlto) {
        EvaluacionContext context = new EvaluacionContext(scoreAlto, BigDecimal.valueOf(10), true);
        assertThat(resolver.resolver(context)).isEqualTo(Dictamen.RECHAZADO);
    }
}
