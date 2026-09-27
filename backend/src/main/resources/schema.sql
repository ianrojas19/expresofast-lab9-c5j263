-- Tabla: empresa_logistica
CREATE TABLE IF NOT EXISTS empresa_logistica (
    empresa_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    cedula_juridica VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL
);

-- Tabla: vehiculo
CREATE TABLE IF NOT EXISTS vehiculo (
    vehiculo_id INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(15) NOT NULL UNIQUE,
    capacidad_kg DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('DISPONIBLE', 'EN_RUTA', 'MANTENIMIENTO')),
    empresa_id INT,
    FOREIGN KEY (empresa_id) REFERENCES empresa_logistica(empresa_id)
);

-- Tabla: conductor
CREATE TABLE IF NOT EXISTS conductor (
    conductor_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    licencia VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla: envio
CREATE TABLE IF NOT EXISTS envio (
    envio_id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_rastreo VARCHAR(30) NOT NULL UNIQUE,
    direccion_destino VARCHAR(200) NOT NULL,
    peso_kg DECIMAL(10,2) NOT NULL,
    costo DECIMAL(10,2) NOT NULL,
    estado_envio VARCHAR(20) NOT NULL CHECK (estado_envio IN ('PENDIENTE', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO')),
    vehiculo_id INT,
    conductor_id INT,
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    FOREIGN KEY (vehiculo_id) REFERENCES vehiculo(vehiculo_id),
    FOREIGN KEY (conductor_id) REFERENCES conductor(conductor_id)
);

-- Tabla: usuario
CREATE TABLE IF NOT EXISTS usuario (
    usuario_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL
);

-- Tabla: rol
CREATE TABLE IF NOT EXISTS rol (
    rol_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(30) NOT NULL UNIQUE CHECK (nombre_rol IN ('ROLE_ADMIN', 'ROLE_OPERADOR', 'ROLE_CONDUCTOR'))
);

-- Tabla: usuario_rol
CREATE TABLE IF NOT EXISTS usuario_rol (
    usuario_id INT,
    rol_id INT,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(usuario_id),
    FOREIGN KEY (rol_id) REFERENCES rol(rol_id)
);

-- Tabla: bitacora_envio
CREATE TABLE IF NOT EXISTS bitacora_envio (
    bitacora_id INT AUTO_INCREMENT PRIMARY KEY,
    envio_id INT NOT NULL,
    estado_anterior VARCHAR(20) NOT NULL,
    estado_nuevo VARCHAR(20) NOT NULL,
    fecha_cambio TIMESTAMP NOT NULL,
    usuario_id INT NOT NULL,
    observaciones VARCHAR(250),
    FOREIGN KEY (envio_id) REFERENCES envio(envio_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(usuario_id)
);




CREATE ALIAS IF NOT EXISTS SP_OBTENER_ENVIOS_POR_ESTADO FOR "cr.ac.ucr.paraiso.ie.c5j263.expresofast.procedure.StoredProcedures.obtenerEnviosPorEstado";


CREATE ALIAS IF NOT EXISTS SP_RESUMEN_METRICAS_ENVIOS FOR "cr.ac.ucr.paraiso.ie.c5j263.expresofast.procedure.StoredProcedures.resumenMetricasEnvios";
