-- =====================================================================
-- 01. Crear la base de datos (se ejecuta UNA sola vez)
-- Conectado como postgres a la base "postgres", por ejemplo desde
-- pgAdmin (Query Tool) o con:  psql -U postgres -f 01_crear_base_de_datos.sql
-- =====================================================================
CREATE DATABASE soporte_db
    WITH ENCODING = 'UTF8'
    TEMPLATE = template0;

COMMENT ON DATABASE soporte_db IS 'API REST de solicitudes de soporte técnico';
