-- =====================================================================
-- 03. Datos de ejemplo para probar la API
-- Se puede ejecutar varias veces: no duplica registros.
-- =====================================================================
INSERT INTO soporte.clientes (nombre, email, telefono, empresa) VALUES
    ('María Quispe', 'mquispe@andina.pe', '987654321', 'Distribuidora Andina SAC'),
    ('Jorge Torres', 'jtorres@gmail.com', '956123478', NULL)
ON CONFLICT (email) DO NOTHING;

INSERT INTO soporte.tecnicos (nombre, email, telefono, especialidad, activo) VALUES
    ('Luis Ramírez', 'lramirez@soporte.pe', '912345678', 'REDES', TRUE),
    ('Ana Salazar', 'asalazar@soporte.pe', '923456789', 'HARDWARE', TRUE)
ON CONFLICT (email) DO NOTHING;

-- Las solicitudes solo se cargan si la tabla está vacía
INSERT INTO soporte.solicitudes
    (titulo, descripcion, prioridad, estado, costo, cliente_id, tecnico_id, fecha_creacion, fecha_actualizacion)
SELECT d.titulo, d.descripcion, d.prioridad, d.estado, d.costo, c.id, t.id, NOW(), NOW()
FROM (VALUES
    (1, 'La impresora del área contable no imprime',
     'Muestra el error de papel atascado aunque la bandeja está vacía.',
     'MEDIA', 'ABIERTA', NULL::NUMERIC, 'mquispe@andina.pe', NULL),
    (2, 'Sin conexión a internet en la oficina',
     'Ninguna computadora del segundo piso tiene internet desde las 9:00.',
     'CRITICA', 'EN_PROCESO', 150.00, 'mquispe@andina.pe', 'lramirez@soporte.pe'),
    (3, 'La laptop se apaga sola',
     'Se apaga a los 10 minutos de uso y el ventilador hace ruido.',
     'ALTA', 'ABIERTA', NULL::NUMERIC, 'jtorres@gmail.com', NULL)
) AS d (orden, titulo, descripcion, prioridad, estado, costo, email_cliente, email_tecnico)
JOIN soporte.clientes c ON c.email = d.email_cliente
LEFT JOIN soporte.tecnicos t ON t.email = d.email_tecnico
WHERE NOT EXISTS (SELECT 1 FROM soporte.solicitudes)
ORDER BY d.orden;
