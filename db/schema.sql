-- Esquema de referencia para PostgreSQL, fiel al modelo de datos del enunciado.
-- Uso recomendado en produccion: aplicar este script (o migrarlo a Flyway/
-- Liquibase) y configurar spring.jpa.hibernate.ddl-auto=validate.

CREATE TABLE IF NOT EXISTS clientes (
    id                  BIGSERIAL PRIMARY KEY,
    numero_documento    VARCHAR(30)  NOT NULL UNIQUE,
    nombre_completo     VARCHAR(200) NOT NULL,
    fecha_nacimiento    DATE         NOT NULL,
    tipo_contrato       VARCHAR(40)  NOT NULL
        CHECK (tipo_contrato IN ('INDEFINIDO','TERMINO_FIJO','PRESTACION_SERVICIOS','INDEPENDIENTE_SIN_CONTRATO')),
    antiguedad_meses    INTEGER      NOT NULL CHECK (antiguedad_meses >= 0),
    actividad_economica VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS solicitudes_credito (
    id                  BIGSERIAL PRIMARY KEY,
    id_cliente          BIGINT         NOT NULL REFERENCES clientes(id),
    monto_solicitado    NUMERIC(18,2)  NOT NULL CHECK (monto_solicitado > 0),
    plazo_meses         INTEGER        NOT NULL CHECK (plazo_meses > 0),
    ingresos_mensuales  NUMERIC(18,2)  NOT NULL CHECK (ingresos_mensuales > 0),
    egresos_mensuales   NUMERIC(18,2)  NOT NULL CHECK (egresos_mensuales >= 0),
    estado              VARCHAR(20)    NOT NULL CHECK (estado IN ('PENDIENTE','EVALUADA')),
    fecha_solicitud     TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS resultados_evaluacion (
    id                     BIGSERIAL PRIMARY KEY,
    id_solicitud           BIGINT        NOT NULL UNIQUE REFERENCES solicitudes_credito(id),
    score_obtenido         INTEGER       NOT NULL,
    dti_calculado          NUMERIC(5,2)  NOT NULL,
    cuota_estimada         NUMERIC(18,2) NOT NULL,
    tasa_interes_ea        NUMERIC(7,4)  NOT NULL,
    dictamen               VARCHAR(20)   NOT NULL CHECK (dictamen IN ('APROBADO','RECHAZADO','REVISION_MANUAL')),
    detalles_motivos       JSONB         NOT NULL,
    version_motor_reglas   VARCHAR(60)   NOT NULL,
    fecha_evaluacion       TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_solicitudes_id_cliente ON solicitudes_credito(id_cliente);
CREATE INDEX IF NOT EXISTS idx_resultados_id_solicitud ON resultados_evaluacion(id_solicitud);
