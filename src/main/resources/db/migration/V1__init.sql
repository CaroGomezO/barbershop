-- Roles
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES 
('ADMINISTRADOR'),
('BARBERO'),
('CLIENTE');

-- Usuarios
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    hash_password TEXT NOT NULL,
    role_id INT NOT NULL,
    is_password_temporary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Clientes
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    names VARCHAR(100) NOT NULL,
    last_names VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,

    CONSTRAINT fk_client_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Empleados
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    document_number VARCHAR(20) UNIQUE NOT NULL,
    names VARCHAR(100) NOT NULL,
    last_names VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_employee_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Servicios
CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0)
);

-- Servicios por empleado
CREATE TABLE employee_services (
    employee_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,

    PRIMARY KEY (employee_id, service_id),

    CONSTRAINT fk_es_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_es_service FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Disponibilidad de empleados
CREATE TABLE availability (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    CONSTRAINT fk_av_employee FOREIGN KEY (employee_id) REFERENCES employees(id),

    CONSTRAINT chk_time_range CHECK (start_time < end_time)
);

-- Citas
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_app_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_app_employee FOREIGN KEY (employee_id) REFERENCES employees(id),

    CONSTRAINT chk_app_time CHECK (start_time < end_time)
);

-- Servicios por cita
CREATE TABLE appointment_services (
    appointment_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    price_charged NUMERIC(10,2) NOT NULL,
    duration_minutes INT NOT NULL,

    PRIMARY KEY (appointment_id, service_id),

    CONSTRAINT fk_as_app FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    CONSTRAINT fk_as_service FOREIGN KEY (service_id) REFERENCES services(id)
);

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