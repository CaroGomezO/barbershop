-- ============================================================
-- ROLES
-- ============================================================
CREATE TABLE roles (
    id   SERIAL      PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- ============================================================
-- USUARIOS (tabla base para todos los perfiles)
-- Cambios:
--   - password renombrado a password_hash (claridad)
--   - se agrega created_at para auditoría
-- ============================================================
CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    phone         VARCHAR(15)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id       INT          NOT NULL REFERENCES roles(id),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- ADMINISTRADORES
-- ============================================================
CREATE TABLE administrators (
    id      SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- CLIENTES
-- ============================================================
CREATE TABLE clients (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- SERVICIOS
-- ============================================================
CREATE TABLE services (
    id               SERIAL       PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    description      TEXT,
    price            DECIMAL(10,2) NOT NULL,
    duration_minutes INT           NOT NULL
);

-- ============================================================
-- BARBEROS
-- Cambios:
--   - first_login → must_change_password (nombre más claro)
--   - status usa CHECK en lugar de confiar en la app
--   - se agrega created_at para auditoría
--   - id_card más descriptivo que barber_id_card
-- ============================================================
CREATE TABLE barbers (
    id                   SERIAL       PRIMARY KEY,
    user_id              BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    id_card              VARCHAR(20)  NOT NULL UNIQUE,
    address              VARCHAR(150) NOT NULL,
    service_id           INT          NOT NULL REFERENCES services(id),
    status               VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                             CHECK (status IN ('ACTIVE', 'INACTIVE')),
    must_change_password BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- BLOQUES DE DISPONIBILIDAD
-- Cambios:
--   - Se elimina el campo "status" del bloque. Un bloque libre
--     simplemente existe; si tiene una cita asociada, está ocupado.
--     Esto evita inconsistencias entre las dos tablas.
--   - Se agrega UNIQUE (barber_id, block_date, start_time) para
--     evitar duplicados a nivel de BD.
-- ============================================================
CREATE TABLE availability_blocks (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    barber_id  INT    NOT NULL REFERENCES barbers(id) ON DELETE CASCADE,
    block_date DATE   NOT NULL,
    start_time TIME   NOT NULL,
    end_time   TIME   NOT NULL,
    CONSTRAINT uq_block UNIQUE (barber_id, block_date, start_time)
);

-- ============================================================
-- CITAS
-- Cambios:
--   - Se agrega availability_block_id (FK) para vincular
--     directamente el bloque reservado. Esto permite saber
--     qué bloques están ocupados sin campo "status" en el bloque.
--   - Se agrega status con CHECK para el ciclo de vida de la cita.
--   - Se agrega created_at para auditoría.
--   - appointment_date y appointment_time se pueden derivar del
--     bloque, pero se mantienen por consultas rápidas y legibilidad.
-- ============================================================
CREATE TABLE appointments (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id             BIGINT NOT NULL REFERENCES clients(id),
    barber_id             INT    NOT NULL REFERENCES barbers(id),
    service_id            INT    NOT NULL REFERENCES services(id),
    availability_block_id BIGINT NOT NULL UNIQUE REFERENCES availability_blocks(id),
    appointment_date      DATE   NOT NULL,
    appointment_time      TIME   NOT NULL,
    status                VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED'
                              CHECK (status IN ('CONFIRMED', 'CANCELLED', 'MODIFIED')),
    created_at            TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- ÍNDICES para consultas frecuentes
-- ============================================================
CREATE INDEX idx_availability_barber_date
    ON availability_blocks (barber_id, block_date);

CREATE INDEX idx_appointments_client
    ON appointments (client_id);

CREATE INDEX idx_appointments_barber_date
    ON appointments (barber_id, appointment_date);

-- ============================================================
-- DATOS INICIALES
-- ============================================================
INSERT INTO roles (name) VALUES ('Administrador'), ('Barbero'), ('Cliente');

INSERT INTO services (name, description, price, duration_minutes) VALUES
('Corte de cabello',
 'Degradados perfectos, clásicos o tendencia. Incluye lavado, asesoría según tu tipo de rostro y acabado con ceras.',
 15000, 30),
('Arreglo de barba',
 'Ritual de toalla caliente, perfilado con navaja y aceites hidratantes para una barba suave y alineada.',
 12000, 20),
('Corte de cabello + barba',
 'La experiencia completa. Transformación total con simetría entre cabello y barba.',
 25000, 60),
('Perfilado de cejas',
 'Limpieza y definición natural para resaltar la mirada. El detalle que marca la diferencia.',
 5000, 15),
('Corte + lavado',
 'Corte premium seguido de masaje capilar con shampoo refrescante.',
 20000, 45);