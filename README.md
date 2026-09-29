# Motor de Scoring y Evaluacion de Creditos

Backend en Spring Boot 3 / Java 17 que implementa el motor de originacion y
scoring de creditos descrito en el enunciado, con arquitectura hexagonal y
los patrones Strategy (`RiskRule`) y Specification (`DictamenSpecification`).

## Como ejecutarlo

Requiere Java 17+, Maven 3.9+ y una base de datos PostgreSQL.

```bash
# 1. Crear la base de datos (o usar Docker)
docker run --name credit-scoring-db -e POSTGRES_DB=credit_scoring \
  -e POSTGRES_USER=credit_scoring -e POSTGRES_PASSWORD=credit_scoring \
  -p 5432:5432 -d postgres:16

# 2. Compilar y correr pruebas
mvn clean verify

# 3. Levantar la aplicacion (crea/actualiza el esquema automaticamente con ddl-auto=update)
mvn spring-boot:run
```

La API queda en `http://localhost:8080/api/v1/credit-applications` y la
documentacion interactiva OpenAPI/Swagger en `http://localhost:8080/swagger-ui.html`.

Variables de entorno soportadas: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.

Para produccion se recomienda usar `db/schema.sql` (fiel al modelo de datos
del enunciado, con `JSONB`, `CHECK` constraints, etc.) con
`spring.jpa.hibernate.ddl-auto=validate` en vez de `update`.

> Nota: este entregable se genero en un entorno sin acceso a internet, por lo
> que no fue posible ejecutar `mvn clean verify` para confirmar la
> compilacion contra los artefactos reales de Maven Central. El codigo fue
> revisado manualmente con cuidado, pero se recomienda correr la build
> completa como primer paso al recibir el proyecto.

## Arquitectura

```
domain/            <- Nucleo puro (POJOs), CERO dependencias de frameworks
  model/            Cliente, SolicitudCredito, BuroCreditoResultado, ResultadoEvaluacion, ...
  rules/            Interfaz RiskRule (Strategy) + 8 implementaciones concretas
  specification/    Interfaz DictamenSpecification (Specification) + 3 implementaciones + DictamenResolver
  service/          ScoringEngine, DtiCalculator, CapacidadPagoCalculator,
                     AmortizacionFrancesaCalculator (sistema frances), TasaInteresAssigner
  exception/        ApplicantUnderageException, InvalidFinancialDataException, ...

application/        <- Casos de uso (puertos in/out) y DTOs. Orquesta el dominio,
  port/in/             sin logica de negocio propia.
  port/out/
  service/
  dto/

infrastructure/     <- Adaptadores (Spring, JPA, REST, OpenAPI)
  rest/                Controller + @RestControllerAdvice (RFC 7807)
  persistence/         Entidades JPA, repositorios Spring Data, adaptadores de los puertos
  buro/                BuroCreditoMockAdapter (simulacion Datacredito/TransUnion-style)
  config/              Wiring de Spring: aqui, y SOLO aqui, se decide que reglas
                       estan activas y con que umbrales (scoring.* en application.yml)
```

### Por que es un motor "desacoplado" y no un bloque de if-else

`ScoringEngine.evaluar(...)` (ver `domain/service/ScoringEngine.java`) itera
una `List<RiskRule>` y solo suma puntos / recolecta motivos de rechazo. No
conoce DTI, mora, edad, ni ninguna regla concreta. Cada politica vive en su
propia clase bajo `domain/rules/impl/`:

| Regla                          | Tipo de resultado                          |
|---------------------------------|---------------------------------------------|
| `EdadMinimaRiskRule`            | Causal de rechazo si edad < minima          |
| `MoraActivaRiskRule`            | Causal de rechazo si hay mora activa        |
| `CapacidadPagoResidualRiskRule` | Causal de rechazo si el residual < umbral   |
| `DtiRiskRule`                   | Puntaje (300/150/0) o causal si DTI > maximo|
| `ScoreBuroRiskRule`             | Puntaje segun franja de score (150-950)     |
| `HistorialCrediticioRiskRule`   | Puntaje segun calificacion A/B/C            |
| `EstabilidadLaboralRiskRule`    | Puntaje segun tipo de contrato + antiguedad |
| `NivelIngresosRiskRule`         | Puntaje segun nivel absoluto de ingresos    |

Activar/desactivar una regla o cambiar sus umbrales es **solo** cuestion de
tocar `application.yml` (prefijo `scoring.*`) o `ScoringEngineConfig`; el
`ScoringEngine` nunca cambia.

El dictamen final se resuelve con `DictamenResolver`, que evalua tres
`DictamenSpecification` en orden de prioridad (`Rechazado` > `Aprobado` >
`RevisionManual`), en vez de anidar condicionales en el servicio de
aplicacion.

## Decisiones y supuestos explicitos

El enunciado deja algunos puntos abiertos; estas son las decisiones tomadas
(todas facilmente ajustables via configuracion):

1. **Score interno vs. score de buro**: el campo `score_obtenido` de
   `resultados_evaluacion` es el **score interno del motor** (suma de las 8
   `RiskRule`, rango aproximado 0-1000), no el score crudo del buro
   (150-950). El corte de aprobacion (`score >= 700` y `DTI <= 40%`) aplica
   sobre este score interno. Configurable en `scoring.dictamen.*`.
2. **Umbrales de DTI**: se usan los cortes explicitos de la formula base
   (35% / 45%) para la matriz de puntos. El enunciado tambien menciona 40%/50%
   como umbrales generales de "riesgo alto"; `DtiRiskRule` recibe sus
   umbrales por parametro, por lo que ajustarlos a 40/50 es un cambio de
   configuracion, no de codigo.
3. **Tasa de evaluacion vs. tasa preferencial**: para proyectar la cuota y
   el DTI *antes* de conocer el dictamen se usa una tasa conservadora fija
   (`TasaInteresAssigner.TASA_EVALUACION_EA`, 24% EA). Si la solicitud queda
   `APROBADO`, se reasigna una tasa preferencial segun la calificacion del
   buro (A: 12% EA, B: 18% EA, C: 22% EA) y se recalcula la cuota final con
   esa tasa. Esto evita que una tasa optimista subestime el riesgo antes de
   aprobar.
4. **Cupo aprobado**: se asume que el cupo otorgado es igual al monto
   solicitado cuando el dictamen es `APROBADO` (no se modela un ajuste de
   monto). Extenderlo a un cupo reducido segun capacidad de pago es un
   cambio localizado en `EvaluarSolicitudService`.
5. **Tabla de amortizacion**: no se persiste como entidad propia (el modelo
   de datos del enunciado no la incluye). Se recalcula de forma determinista
   en `GET .../amortization` a partir del monto/plazo de la solicitud y la
   tasa preferencial ya guardada en `resultados_evaluacion`; por eso ese
   endpoint devuelve 400 si el dictamen no es `APROBADO`.
6. **`actividad_economica`**: el requerimiento funcional la pide como dato
   de entrada, pero no aparece en el diagrama entidad-relacion. Se agrego
   como columna adicional en `clientes` (nullable) para no perder el dato.
7. **Inmutabilidad del historico**: `ResultadoEvaluacionRepositoryAdapter.guardar()`
   siempre inserta un registro nuevo; nunca hace `UPDATE` sobre un resultado
   existente, en linea con el requerimiento de trazabilidad/auditoria. Si
   una solicitud se re-evalua, quedan varios registros en
   `resultados_evaluacion` con distinta `fecha_evaluacion`; el endpoint de
   consulta retorna el mas reciente en la implementacion actual
   (`findByIdSolicitud`) — para un historial completo bastaria con exponer
   un `findAllByIdSolicitud` adicional.
8. **`versionMotorReglas`**: se registra como una constante de codigo
   (`EvaluarSolicitudService.VERSION_MOTOR_REGLAS`). En un escenario real se
   recomienda derivarla del artefacto/build (ej. manifest de Maven) para que
   cambie automaticamente con cada release del motor.

## Pruebas

`mvn test` ejecuta la suite JUnit 5 + Mockito, que incluye:

- **Reglas individuales** (`domain/rules/impl/*Test`): `@ParameterizedTest`
  con los bordes exactos de cada corte (DTI en 35%/45%, score de buro en
  400/600/700, edad en 18, capacidad residual en el umbral de subsistencia).
- **`ScoringEngineTest`**: el motor central probado con `RiskRule` mockeadas
  (Mockito), demostrando que es agnostico a cualquier regla concreta.
- **`DictamenResolverTest`**: matriz de decision completa (`APROBADO` /
  `RECHAZADO` / `REVISION_MANUAL`) con los bordes de score (700) y DTI (40%).
- **`EvaluarSolicitudServiceTest`**: pruebas de integracion de la capa de
  aplicacion con reglas de dominio reales y puertos de salida mockeados,
  cubriendo un perfil solido (`APROBADO`), mora activa (`RECHAZADO`),
  capacidad de pago insuficiente (`RECHAZADO`) y un perfil de zona gris
  (`REVISION_MANUAL`).
- **`RegistrarSolicitudServiceTest`**: borde exacto de la edad minima legal
  (17 vs. 18 anios) y validacion basica de datos financieros.
- **`AmortizacionFrancesaCalculatorTest`**: formula del sistema frances
  contra valores de referencia, mas invariantes de la tabla completa (saldo
  final en cero, suma de capital = monto solicitado).

## Endpoints

| Metodo | Endpoint                                           | Descripcion                                  |
|--------|-----------------------------------------------------|-----------------------------------------------|
| POST   | `/api/v1/credit-applications`                       | Registra una solicitud                        |
| POST   | `/api/v1/credit-applications/{id}/evaluate`         | Ejecuta el motor de scoring y genera el dictamen |
| GET    | `/api/v1/credit-applications/{id}/evaluation`       | Consulta el dictamen ya generado              |
| GET    | `/api/v1/credit-applications/{id}/amortization`     | Tabla de amortizacion (solo si `APROBADO`)    |

Todos los errores de negocio responden en formato RFC 7807
(`application/problem+json`) via `GlobalExceptionHandler`.
