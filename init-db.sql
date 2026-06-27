CREATE TABLE IF NOT EXISTS roles (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    id_rol INT NOT NULL,
    nombre_completo VARCHAR(150) NOT NULL,
    correo VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

CREATE TABLE IF NOT EXISTS vehiculos (
    placa VARCHAR(7) PRIMARY KEY,
    marca VARCHAR(255),
    modelo VARCHAR(255),
    kilometraje_actual INT NOT NULL,
    proximo_mantenimiento_km INT DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS catalogo_insumos (
    id_insumo INT PRIMARY KEY AUTO_INCREMENT,
    codigo_interno VARCHAR(50) UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    unidad_medida VARCHAR(20) NOT NULL,
    costo_unitario DECIMAL(10,2) NOT NULL,
    stock DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ordenes_trabajo (
    id_ot INT PRIMARY KEY AUTO_INCREMENT,
    id_jefe_taller INT,
    id_mecanico INT,
    placa VARCHAR(7) NOT NULL,
    fecha_internamiento DATETIME,
    fecha_salida DATETIME,
    diagnostico_mecanico TEXT,
    fallas_reparadas TEXT,
    kilometraje INT DEFAULT NULL,
    costo_total DECIMAL(10,2) DEFAULT 0,
    estado VARCHAR(50) DEFAULT 'EN_REVISION',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_jefe_taller) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_mecanico) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (placa) REFERENCES vehiculos(placa)
);

CREATE TABLE IF NOT EXISTS categorias_reporte (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

INSERT IGNORE INTO categorias_reporte (id_categoria, nombre, descripcion) VALUES
(1, 'Motor', 'Problemas con el motor, ruidos, sobrecalentamiento'),
(2, 'Transmisión', 'Problemas con la caja de cambios o embrague'),
(3, 'Sistema Eléctrico', 'Fallas en luces, alternador, batería, arranque'),
(4, 'Dirección', 'Juego en la dirección, vibraciones'),
(5, 'Frenos', 'Frenos desgastados, ruido al frenar, pedal blando'),
(6, 'Suspensión', 'Amortiguadores, resortes, ruidos en suspensión'),
(7, 'Chasis', 'Daños estructurales, corrosión, soldadura'),
(8, 'Refrigeración', 'Sistema de enfriamiento, radiador, mangueras'),
(9, 'Aire Acondicionado', 'Falta de enfriamiento, fugas, compresor');

CREATE TABLE IF NOT EXISTS trabajo_ot (
    id_trabajo INT PRIMARY KEY AUTO_INCREMENT,
    id_ot INT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    completado BOOLEAN DEFAULT FALSE,
    observaciones TEXT,
    FOREIGN KEY (id_ot) REFERENCES ordenes_trabajo(id_ot)
);

CREATE TABLE IF NOT EXISTS requerimientos_insumo (
    id_requerimiento INT PRIMARY KEY AUTO_INCREMENT,
    id_ot INT NOT NULL,
    id_insumo INT NOT NULL,
    solicitado_por INT NOT NULL,
    cantidad_solicitada DECIMAL(8,2) NOT NULL,
    cantidad_entregada DECIMAL(8,2) DEFAULT 0,
    subtotal DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (id_ot) REFERENCES ordenes_trabajo(id_ot),
    FOREIGN KEY (id_insumo) REFERENCES catalogo_insumos(id_insumo),
    FOREIGN KEY (solicitado_por) REFERENCES usuarios(id_usuario)
);

-- Datos semilla
INSERT IGNORE INTO roles (id_rol, nombre, descripcion) VALUES
(1, 'ROLE_JEFE_TALLER', 'Jefe de taller - acceso total'),
(2, 'ROLE_JEFE_DIRECTO', 'Jefe directo - acceso total'),
(3, 'ROLE_MECANICO', 'Mecanico - consultas y solicitar insumos'),
(4, 'ROLE_LOGISTICA', 'Logistica - gestion de insumos y despacho');
