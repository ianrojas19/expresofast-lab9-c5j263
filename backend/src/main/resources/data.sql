-- Empresa logística
INSERT INTO empresa_logistica (nombre, cedula_juridica, telefono, fecha_registro) VALUES
('Logistica Express', '3-101-123456', '2555-1000', CURRENT_TIMESTAMP);

-- Vehículos
INSERT INTO vehiculo (placa, capacidad_kg, estado, empresa_id) VALUES
('ABC-123', 5000.00, 'DISPONIBLE', 1),
('XYZ-987', 3000.00, 'DISPONIBLE', 1),
('DEF-456', 4000.00, 'EN_RUTA', 1);

-- Conductores
INSERT INTO conductor (nombre, apellidos, licencia, telefono, activo) VALUES
('Juan', 'Perez', 'B3-111', '8888-1111', TRUE),
('Maria', 'Gomez', 'B3-222', '8888-2222', TRUE),
('Carlos', 'Ramirez', 'B3-333', '8888-3333', TRUE);

-- Usuarios
INSERT INTO usuario (username, password_hash, nombre_completo, email, activo) VALUES
('admin', '$2a$10$JpSI45z6lS/K5gR2pTrgNu9LRlhokKGkwzKgsSTAM.R.97RLDsvdW', 'Carlos Alvarado', 'admin@expresofast.cr', TRUE),
('operador1', '$2a$10$JpSI45z6lS/K5gR2pTrgNu9LRlhokKGkwzKgsSTAM.R.97RLDsvdW', 'Maria Solis', 'operador1@expresofast.cr', TRUE),
('conductor1', '$2a$10$JpSI45z6lS/K5gR2pTrgNu9LRlhokKGkwzKgsSTAM.R.97RLDsvdW', 'Juan Perez', 'conductor1@expresofast.cr', TRUE);

-- Roles
INSERT INTO rol (nombre_rol) VALUES ('ROLE_ADMIN'), ('ROLE_OPERADOR'), ('ROLE_CONDUCTOR');

-- Asignación usuario-rol
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (1, 1);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (2, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (3, 3);


INSERT INTO envio (codigo_rastreo, direccion_destino, peso_kg, costo, estado_envio, vehiculo_id, conductor_id, fecha_creacion, fecha_modificacion) VALUES
('EXP-0001', 'San José Centro, Avenida 2', 5.50, 2500.00, 'PENDIENTE', 1, 1, DATEADD('DAY', -15, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0002', 'Cartago, Barrio Asís', 12.00, 7500.00, 'EN_TRANSITO', 2, 2, DATEADD('DAY', -14, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0003', 'Heredia, San Francisco', 8.75, 4500.00, 'ENTREGADO', 1, 1, DATEADD('DAY', -13, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0004', 'Alajuela, El Coyol', 25.00, 9000.00, 'CANCELADO', 2, 2, DATEADD('DAY', -12, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0005', 'Puntarenas, Esparza', 3.20, 2500.00, 'PENDIENTE', 1, 3, DATEADD('DAY', -11, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0006', 'Limón, Siquirres', 45.00, 12000.00, 'EN_TRANSITO', 3, 1, DATEADD('DAY', -10, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0007', 'Guanacaste, Liberia', 18.30, 8500.00, 'ENTREGADO', 1, 2, DATEADD('DAY', -9, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0008', 'San José, Escazú', 7.00, 3500.00, 'PENDIENTE', 2, 3, DATEADD('DAY', -8, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0009', 'Cartago, Turrialba', 60.00, 15000.00, 'EN_TRANSITO', 3, 1, DATEADD('DAY', -7, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0010', 'Heredia, Barva', 2.50, 2500.00, 'ENTREGADO', 1, 2, DATEADD('DAY', -6, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0011', 'San José, Desamparados', 15.00, 7500.00, 'CANCELADO', 2, 3, DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0012', 'Alajuela, Grecia', 9.80, 4000.00, 'PENDIENTE', 1, 1, DATEADD('DAY', -4, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0013', 'Puntarenas, Quepos', 33.00, 10500.00, 'EN_TRANSITO', 3, 2, DATEADD('DAY', -3, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0014', 'Limón, Guápiles', 22.00, 8000.00, 'ENTREGADO', 2, 3, DATEADD('DAY', -2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0015', 'Guanacaste, Nicoya', 11.50, 6000.00, 'PENDIENTE', 1, 1, DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0016', 'San José, Moravia', 4.00, 2500.00, 'CANCELADO', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EXP-0017', 'Cartago, Paraíso', 28.00, 9500.00, 'EN_TRANSITO', 3, 3, DATEADD('HOUR', -12, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('EXP-0018', 'Heredia, Santo Domingo', 6.25, 3000.00, 'ENTREGADO', 1, 1, DATEADD('HOUR', -6, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);
