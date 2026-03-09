-- ============================================================
-- 006_rental_schema.sql
-- Rental Service schema migration
-- Converts: rental table from MySQL Sakila
-- ============================================================

-- Create rental schema
CREATE SCHEMA IF NOT EXISTS rental_schema;

-- rental table
-- inventory_id, customer_id, staff_id are cross-schema refs → plain integers, NO FK constraints
-- MySQL DATETIME → TIMESTAMP
-- MySQL INT AUTO_INCREMENT → SERIAL
CREATE TABLE rental_schema.rental (
    rental_id    SERIAL PRIMARY KEY,
    rental_date  TIMESTAMP NOT NULL,
    inventory_id INTEGER NOT NULL,
    customer_id  INTEGER NOT NULL,
    return_date  TIMESTAMP,
    staff_id     INTEGER NOT NULL,
    last_update  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_rental_date_inventory_customer UNIQUE (rental_date, inventory_id, customer_id)
);

CREATE INDEX idx_rental_inventory_id ON rental_schema.rental (inventory_id);
CREATE INDEX idx_rental_customer_id ON rental_schema.rental (customer_id);
CREATE INDEX idx_rental_staff_id ON rental_schema.rental (staff_id);

-- last_update trigger
CREATE TRIGGER set_last_update
    BEFORE UPDATE ON rental_schema.rental
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();
