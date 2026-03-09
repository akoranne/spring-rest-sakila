-- ============================================================
-- 007_store_schema.sql
-- Store Service schema migration
-- Converts: store, inventory, staff tables from MySQL Sakila
-- ============================================================

-- Create store schema
CREATE SCHEMA IF NOT EXISTS store_schema;

-- store table
-- address_id is cross-schema ref → plain integer, NO FK constraint
-- manager_staff_id is intra-schema FK to staff, but uses DEFERRABLE INITIALLY DEFERRED
-- to handle circular insert dependency (store→staff, staff→store)
CREATE TABLE store_schema.store (
    store_id         SERIAL PRIMARY KEY,
    manager_staff_id INTEGER UNIQUE,
    address_id       INTEGER,
    last_update      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_store_address_id ON store_schema.store (address_id);

-- inventory table
-- film_id is cross-schema ref → plain integer, NO FK constraint
-- store_id is intra-schema FK to store (preserved)
CREATE TABLE store_schema.inventory (
    inventory_id SERIAL PRIMARY KEY,
    film_id      INTEGER NOT NULL,
    store_id     INTEGER NOT NULL,
    last_update  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_store FOREIGN KEY (store_id)
        REFERENCES store_schema.store (store_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE INDEX idx_inventory_film_id ON store_schema.inventory (film_id);
CREATE INDEX idx_inventory_store_id_film_id ON store_schema.inventory (store_id, film_id);

-- staff table
-- address_id, authority_id are cross-schema refs → plain integers, NO FK constraints
-- store_id is intra-schema FK to store (preserved)
-- MySQL BLOB picture column omitted (not needed for microservice API)
CREATE TABLE store_schema.staff (
    staff_id     SERIAL PRIMARY KEY,
    first_name   VARCHAR(45) NOT NULL,
    last_name    VARCHAR(45) NOT NULL,
    address_id   INTEGER,
    store_id     INTEGER NOT NULL,
    active       BOOLEAN DEFAULT TRUE,
    username     VARCHAR(16) NOT NULL,
    authority_id INTEGER,
    last_update  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_store FOREIGN KEY (store_id)
        REFERENCES store_schema.store (store_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE INDEX idx_staff_store_id ON store_schema.staff (store_id);
CREATE INDEX idx_staff_address_id ON store_schema.staff (address_id);
CREATE INDEX idx_staff_authority_id ON store_schema.staff (authority_id);

-- Deferred FK: store.manager_staff_id → staff.staff_id
-- Uses DEFERRABLE INITIALLY DEFERRED to allow circular inserts
-- (insert store first with NULL manager, then insert staff, then update store)
ALTER TABLE store_schema.store
    ADD CONSTRAINT fk_store_manager_staff
    FOREIGN KEY (manager_staff_id)
    REFERENCES store_schema.staff (staff_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    DEFERRABLE INITIALLY DEFERRED;

-- last_update triggers for all store tables
CREATE TRIGGER set_last_update_store
    BEFORE UPDATE ON store_schema.store
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_inventory
    BEFORE UPDATE ON store_schema.inventory
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_staff
    BEFORE UPDATE ON store_schema.staff
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();
