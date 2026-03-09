-- ============================================================
-- 005_payment_schema.sql
-- Payment Service schema migration
-- Converts: payment table from MySQL Sakila
-- ============================================================

-- Create payment schema
CREATE SCHEMA IF NOT EXISTS payment_schema;

-- payment table
-- customer_id, staff_id, rental_id are cross-schema refs → plain integers, NO FK constraints
-- MySQL DATETIME → TIMESTAMP
CREATE TABLE payment_schema.payment (
    payment_id   SERIAL PRIMARY KEY,
    customer_id  INTEGER NOT NULL,
    staff_id     INTEGER NOT NULL,
    rental_id    INTEGER,
    amount       NUMERIC(5,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    last_update  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_customer_id ON payment_schema.payment (customer_id);
CREATE INDEX idx_payment_staff_id ON payment_schema.payment (staff_id);
CREATE INDEX idx_payment_rental_id ON payment_schema.payment (rental_id);

-- last_update trigger
CREATE TRIGGER set_last_update
    BEFORE UPDATE ON payment_schema.payment
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();
