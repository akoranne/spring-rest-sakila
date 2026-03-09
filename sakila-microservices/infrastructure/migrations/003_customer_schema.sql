-- ============================================================
-- 003_customer_schema.sql
-- Customer Service schema migration
-- Converts: customer table from MySQL Sakila
-- ============================================================

-- Create customer schema
CREATE SCHEMA IF NOT EXISTS customer_schema;

-- customer table
-- store_id, address_id, authority_id are cross-schema refs → plain integers, NO FK constraints
-- MySQL DATETIME → TIMESTAMP
CREATE TABLE customer_schema.customer (
    customer_id  SERIAL PRIMARY KEY,
    store_id     INTEGER,
    first_name   VARCHAR(45) NOT NULL,
    last_name    VARCHAR(45) NOT NULL,
    address_id   INTEGER,
    active       BOOLEAN DEFAULT TRUE,
    authority_id INTEGER,
    create_date  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customer_store_id ON customer_schema.customer (store_id);
CREATE INDEX idx_customer_address_id ON customer_schema.customer (address_id);
CREATE INDEX idx_customer_authority_id ON customer_schema.customer (authority_id);
CREATE INDEX idx_customer_last_name ON customer_schema.customer (last_name);

-- last_update trigger
CREATE TRIGGER set_last_update
    BEFORE UPDATE ON customer_schema.customer
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();
