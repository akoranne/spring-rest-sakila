-- ============================================================
-- 001_auth_schema.sql
-- Auth Service schema migration
-- Converts: authority table from MySQL Sakila
-- ============================================================

-- Shared trigger function used by all schemas
CREATE OR REPLACE FUNCTION update_last_update_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_update = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create auth schema
CREATE SCHEMA IF NOT EXISTS auth_schema;

-- authority table
-- MySQL SET('ROLE_READ','ROLE_WRITE','ROLE_MANAGE','ROLE_ADMIN') → TEXT[]
-- MySQL SMALLINT UNSIGNED AUTO_INCREMENT → SERIAL
CREATE TABLE auth_schema.authority (
    authority_id SERIAL PRIMARY KEY,
    email        VARCHAR(50) NOT NULL UNIQUE,
    password     VARCHAR(60) NOT NULL,
    authority    TEXT[],
    last_update  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- last_update trigger
CREATE TRIGGER set_last_update
    BEFORE UPDATE ON auth_schema.authority
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();
