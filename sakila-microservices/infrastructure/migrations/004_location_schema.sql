-- ============================================================
-- 004_location_schema.sql
-- Location Service schema migration
-- Converts: country, city, address tables from MySQL Sakila
-- ============================================================

-- Create location schema
CREATE SCHEMA IF NOT EXISTS location_schema;

-- country table
CREATE TABLE location_schema.country (
    country_id  SERIAL PRIMARY KEY,
    country     VARCHAR(50) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- city table (intra-schema FK to country preserved)
CREATE TABLE location_schema.city (
    city_id     SERIAL PRIMARY KEY,
    city        VARCHAR(50) NOT NULL,
    country_id  INTEGER NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_city_country FOREIGN KEY (country_id)
        REFERENCES location_schema.country (country_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE INDEX idx_city_country_id ON location_schema.city (country_id);

-- address table (intra-schema FK to city preserved)
-- MySQL GEOMETRY column omitted (not needed for microservice)
CREATE TABLE location_schema.address (
    address_id  SERIAL PRIMARY KEY,
    address     VARCHAR(50) NOT NULL,
    address2    VARCHAR(50),
    district    VARCHAR(20) NOT NULL,
    city_id     INTEGER NOT NULL,
    postal_code VARCHAR(10),
    phone       VARCHAR(20) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_city FOREIGN KEY (city_id)
        REFERENCES location_schema.city (city_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE INDEX idx_address_city_id ON location_schema.address (city_id);

-- last_update triggers for all location tables
CREATE TRIGGER set_last_update_country
    BEFORE UPDATE ON location_schema.country
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_city
    BEFORE UPDATE ON location_schema.city
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_address
    BEFORE UPDATE ON location_schema.address
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();
