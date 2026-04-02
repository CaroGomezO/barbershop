-- Roles
CREATE TABLE roles (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- Usuarios
CREATE TABLE users (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    names             VARCHAR(100) NOT NULL,
    last_names        VARCHAR(100) NOT NULL,
    document_number   VARCHAR(20)  NOT NULL UNIQUE,
    birth_date        DATE         NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    phone             VARCHAR(15)  NOT NULL,
    password_hash     VARCHAR(255) NOT NULL,
	must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Usuarios - Roles (Un barbero puede ser también un cliente)
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id INT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Administradores
CREATE TABLE administrators (
    id      SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

-- Clientes 
CREATE TABLE clients (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

-- Servicios
CREATE TABLE services (
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    description      TEXT,
    price            DECIMAL(10,2) NOT NULL,
    duration_minutes INT NOT NULL
);

-- Barberos
CREATE TABLE barbers (
    id                   SERIAL PRIMARY KEY,
    user_id              BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    address              VARCHAR(150) NOT NULL,
    hire_date            DATE NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                             CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at           TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Barberos - Servicios (Un barbero puede tener uno o más servicios asociados)
CREATE TABLE barber_services (
    barber_id  INT REFERENCES barbers(id) ON DELETE CASCADE,
    service_id INT REFERENCES services(id) ON DELETE CASCADE,
    PRIMARY KEY (barber_id, service_id)
);

-- Bloques de disponibilidad
CREATE TABLE availability_blocks (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    barber_id  INT    NOT NULL REFERENCES barbers(id) ON DELETE CASCADE,
    block_date DATE   NOT NULL,
    start_time TIME   NOT NULL,
    end_time   TIME   NOT NULL,
    CONSTRAINT uq_block UNIQUE (barber_id, block_date, start_time),
    CONSTRAINT chk_time CHECK (end_time > start_time)
);

-- Citas
CREATE TABLE appointments (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id             BIGINT NOT NULL REFERENCES clients(id),
    barber_id             INT    NOT NULL REFERENCES barbers(id),
    availability_block_id BIGINT NOT NULL UNIQUE REFERENCES availability_blocks(id),
    appointment_date      DATE   NOT NULL,
    appointment_time      TIME   NOT NULL,
    total_price           DECIMAL(10,2) NOT NULL,
    total_duration        INT NOT NULL,
    status                VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED'
                              CHECK (status IN ('CONFIRMED', 'CANCELLED', 'MODIFIED')),
    created_at            TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Citas - Servicios (Una cita puede tener uno o más servicios)
CREATE TABLE appointment_services (
    appointment_id BIGINT REFERENCES appointments(id) ON DELETE CASCADE,
    service_id INT REFERENCES services(id),
    PRIMARY KEY (appointment_id, service_id)
);

-- Índices
CREATE INDEX idx_availability_barber_date
    ON availability_blocks (barber_id, block_date);

CREATE INDEX idx_appointments_client
    ON appointments (client_id);

CREATE INDEX idx_appointments_barber_date
    ON appointments (barber_id, appointment_date);


INSERT INTO roles (name) VALUES 
('ADMINISTRADOR'),
('BARBERO'),
('CLIENTE')
ON CONFLICT (name) DO NOTHING;

INSERT INTO services (name, description, price, duration_minutes) VALUES
('Corte de Cabello Masculino', 'Corte moderno o clásico con asesoría de imagen y acabado con pomada', 15000, 30),
('Corte con Lavado y Peinado', 'Corte de cabello más lavado profundo con masaje capilar y peinado', 20000, 45),
('Corte de Cabello Infantil', 'Corte para niños menores de 12 años con paciencia y estilo', 13000, 30),
('Rapado Uniforme', 'Corte con una sola guía en toda la cabeza (Buzz cut)', 10000, 20),
('Arreglo de Barba Tradicional', 'Perfilado con navaja, toalla caliente y aplicación de bálsamo hidratante', 12000, 25),
('Afeitado Completo', 'Afeitado al ras con espuma premium y técnica de toalla caliente', 15000, 30),
('Perfilado de Cejas', 'Limpieza y diseño de cejas con navaja o pinza', 5000, 15),
('Pigmentación de Barba', 'Aplicación de pigmento semipermanente para dar densidad a la barba', 18000, 30),
('Limpieza Facial Express', 'Exfoliación rápida y remoción de impurezas superficiales', 15000, 20),
('Mascarilla de Carbón Activado', 'Mascarilla negra para eliminación profunda de puntos negros', 12000, 15),
('Tratamiento de Ojeras', 'Parches de colágeno y masaje vibratorio en la zona ocular', 10000, 15),
('Exfoliación + Mascarilla Hidratante', 'Combo de limpieza profunda y nutrición de la piel', 22000, 35),
('Tratamiento Anticaída', 'Aplicación de tónico fortalecedor con masaje estimulante', 25000, 20),
('Camuflaje de Canas', 'Tinte rápido para disimular canas en cabello de forma natural', 30000, 40),
('Alisado Keratina Flequillo/Superior', 'Tratamiento para controlar el frizz en la parte superior', 40000, 60);

-- La contraseña en texto plano es: Admin123
INSERT INTO users (names, last_names, document_number, birth_date, email, phone, password_hash, must_change_password)
VALUES (
    'Admin', 
    'Barbería', 
    '123456789', 
    '1990-01-01', 
    'admin@barberia.com', 
    '3001234567', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjTYkRNS7iK', 
    FALSE
);

INSERT INTO user_roles (user_id, role_id)
VALUES (
    (SELECT id FROM users WHERE email = 'admin@barberia.com'),
    (SELECT id FROM roles WHERE name = 'ADMINISTRADOR')
);

INSERT INTO administrators (user_id)
SELECT id FROM users WHERE email = 'admin@barberia.com'
ON CONFLICT (user_id) DO NOTHING;