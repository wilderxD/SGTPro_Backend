SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE trabajo_ot;
TRUNCATE TABLE requerimientos_insumo;
TRUNCATE TABLE ordenes_trabajo;
DELETE FROM categorias_reporte;
DELETE FROM vehiculos;
DELETE FROM usuarios;
DELETE FROM roles;
DELETE FROM catalogo_insumos;

SET FOREIGN_KEY_CHECKS = 1;

-- Categorias de Reporte
INSERT INTO categorias_reporte (id_categoria, nombre, descripcion) VALUES
(1, 'Motor', 'Problemas con el motor, ruidos, sobrecalentamiento'),
(2, 'Transmisión', 'Problemas con la caja de cambios o embrague'),
(3, 'Sistema Eléctrico', 'Fallas en luces, alternador, batería, arranque'),
(4, 'Dirección', 'Juego en la dirección, vibraciones'),
(5, 'Frenos', 'Frenos desgastados, ruido al frenar, pedal blando'),
(6, 'Suspensión', 'Amortiguadores, resortes, ruidos en suspensión'),
(7, 'Chasis', 'Daños estructurales, corrosión, soldadura'),
(8, 'Refrigeración', 'Sistema de enfriamiento, radiador, mangueras'),
(9, 'Aire Acondicionado', 'Falta de enfriamiento, fugas, compresor');

-- Roles
INSERT INTO roles (id_rol, nombre, descripcion) VALUES
(1, 'ROLE_JEFE_TALLER', 'Jefe de taller - acceso total'),
(2, 'ROLE_JEFE_DIRECTO', 'Jefe directo - acceso total'),
(3, 'ROLE_MECANICO', 'Mecanico - consultas y solicitar insumos'),
(4, 'ROLE_LOGISTICA', 'Logistica - gestion de insumos y despacho');

-- Usuarios (password: 123456, hash BCrypt)
INSERT INTO usuarios (id_usuario, id_rol, nombre_completo, correo, password_hash) VALUES
(1, 1, 'Carlos López', 'admin@test.com', '$2a$10$4nPk/g81euJjqAFMoPIBkuOtu9I.WM4knB6rJ4Ll0HZa6BYODMskK'),
(2, 2, 'María García', 'jefe@test.com', '$2a$10$4nPk/g81euJjqAFMoPIBkuOtu9I.WM4knB6rJ4Ll0HZa6BYODMskK'),
(3, 3, 'Pedro Ramírez', 'mecanico@test.com', '$2a$10$4nPk/g81euJjqAFMoPIBkuOtu9I.WM4knB6rJ4Ll0HZa6BYODMskK'),
(4, 4, 'Ana Torres', 'logistica@test.com', '$2a$10$4nPk/g81euJjqAFMoPIBkuOtu9I.WM4knB6rJ4Ll0HZa6BYODMskK'),
(5, 3, 'Luis Mendoza', 'luis@test.com', '$2a$10$4nPk/g81euJjqAFMoPIBkuOtu9I.WM4knB6rJ4Ll0HZa6BYODMskK');

-- Vehiculos
INSERT INTO vehiculos (placa, marca, modelo, kilometraje_actual, proximo_mantenimiento_km) VALUES
('ABC123', 'Toyota', 'Hilux 2023', 45230, 50000),
('DEF456', 'Nissan', 'Sentra 2022', 28150, 30000),
('GHI789', 'Hyundai', 'Tucson 2024', 12400, 20000),
('JKL012', 'Mazda', 'CX-5 2023', 33500, 40000),
('MNO345', 'Volkswagen', 'Amarok 2023', 56780, 60000),
('PQR678', 'Kia', 'Sportage 2024', 8900, 15000);

-- Catalogo Insumos
INSERT INTO catalogo_insumos (id_insumo, codigo_interno, nombre, unidad_medida, costo_unitario, stock) VALUES
(1, 'ACE-001', 'Aceite 20W50', 'Litro', 35.50, 50),
(2, 'FIL-001', 'Filtro de Aceite', 'Unidad', 28.00, 30),
(3, 'FIL-002', 'Filtro de Aire', 'Unidad', 45.00, 25),
(4, 'FREN-001', 'Pastillas de Freno', 'Juego', 120.00, 15),
(5, 'BUI-001', 'Bujías NGK', 'Unidad', 18.50, 40),
(6, 'COR-001', 'Correa de Distribución', 'Unidad', 95.00, 10),
(7, 'LUB-001', 'Grasa Litio', 'Kg', 22.00, 20),
(8, 'REF-001', 'Refrigerante', 'Galón', 42.00, 15),
(9, 'SUS-001', 'Amortiguador Delantero', 'Unidad', 250.00, 8),
(10, 'NEU-001', 'Neumático 205/55R16', 'Unidad', 380.00, 12);

-- Ordenes de Trabajo
INSERT INTO ordenes_trabajo (id_ot, id_jefe_taller, id_mecanico, placa, fecha_internamiento, fecha_salida, diagnostico_mecanico, fallas_reparadas, kilometraje, costo_total, estado) VALUES
(1, 1, 3, 'ABC123', '2026-06-15 08:30:00', NULL, 'Revisión general y cambio de aceite', '', 45230, 125.50, 'EN_REPARACION'),
(2, 1, 5, 'DEF456', '2026-06-14 10:00:00', '2026-06-16 16:00:00', 'Frenos traseros desgastados, revisar sistema', 'Pastillas de freno reemplazadas', 28150, 340.00, 'FINALIZADO'),
(3, 2, 3, 'GHI789', '2026-06-16 07:45:00', NULL, 'Ruido en motor al acelerar, revisar bujías', '', 12400, 0, 'EN_REVISION'),
(4, 1, 5, 'JKL012', '2026-06-13 14:20:00', '2026-06-15 18:00:00', 'Mantenimiento programado 30,000 km', 'Cambio de aceite, filtros, rotación de neumáticos', 33500, 890.00, 'FINALIZADO'),
(5, 2, 3, 'MNO345', '2026-06-12 09:00:00', NULL, 'Cliente solicita revisión de aire acondicionado', '', 56780, 0, 'CANCELADO'),
(6, 1, 5, 'PQR678', '2026-06-17 11:15:00', NULL, 'Suspensión delantera con ruidos', '', 8900, 0, 'EN_REVISION');

-- Requerimientos
INSERT INTO requerimientos_insumo (id_requerimiento, id_ot, id_insumo, solicitado_por, cantidad_solicitada, cantidad_entregada, subtotal) VALUES
(1, 1, 1, 3, 4, 4, 142.00),
(2, 1, 2, 3, 1, 1, 28.00),
(3, 2, 4, 3, 1, 1, 120.00),
(4, 3, 5, 3, 4, 0, 74.00),
(5, 4, 1, 3, 5, 5, 177.50),
(6, 4, 2, 3, 1, 1, 28.00),
(7, 4, 3, 3, 1, 1, 45.00),
(8, 6, 8, 3, 2, 0, 84.00);
