-- ============================================================
-- 002_catalog_schema.sql
-- Catalog Service schema migration
-- Converts: language, film, actor, film_actor, film_category,
--           film_text, category tables from MySQL Sakila
-- ============================================================

-- Create catalog schema
CREATE SCHEMA IF NOT EXISTS catalog_schema;

-- Custom ENUM type for film rating
-- MySQL ENUM('G','PG','PG-13','R','NC-17') → PostgreSQL ENUM type
CREATE TYPE catalog_schema.film_rating AS ENUM ('G', 'PG', 'PG-13', 'R', 'NC-17');

-- language table
CREATE TABLE catalog_schema.language (
    language_id SERIAL PRIMARY KEY,
    name        VARCHAR(20) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- film table
-- MySQL SET('Trailers','Commentaries','Deleted Scenes','Behind the Scenes') → TEXT[]
-- MySQL YEAR → INTEGER
-- MySQL DECIMAL → NUMERIC
CREATE TABLE catalog_schema.film (
    film_id              SERIAL PRIMARY KEY,
    title                VARCHAR(128) NOT NULL,
    description          TEXT,
    release_year         INTEGER,
    language_id          INTEGER NOT NULL,
    original_language_id INTEGER,
    rental_duration      SMALLINT NOT NULL DEFAULT 3,
    rental_rate          NUMERIC(4,2) NOT NULL DEFAULT 4.99,
    length               SMALLINT,
    replacement_cost     NUMERIC(5,2) NOT NULL DEFAULT 19.99,
    rating               catalog_schema.film_rating DEFAULT 'G',
    special_features     TEXT[],
    last_update          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_film_language FOREIGN KEY (language_id)
        REFERENCES catalog_schema.language (language_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_film_language_original FOREIGN KEY (original_language_id)
        REFERENCES catalog_schema.language (language_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE INDEX idx_film_title ON catalog_schema.film (title);
CREATE INDEX idx_film_language_id ON catalog_schema.film (language_id);
CREATE INDEX idx_film_original_language_id ON catalog_schema.film (original_language_id);

-- actor table
CREATE TABLE catalog_schema.actor (
    actor_id    SERIAL PRIMARY KEY,
    first_name  VARCHAR(45) NOT NULL,
    last_name   VARCHAR(45) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_actor_last_name ON catalog_schema.actor (last_name);

-- film_actor junction table (intra-schema FKs preserved)
CREATE TABLE catalog_schema.film_actor (
    actor_id    INTEGER NOT NULL,
    film_id     INTEGER NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (actor_id, film_id),
    CONSTRAINT fk_film_actor_actor FOREIGN KEY (actor_id)
        REFERENCES catalog_schema.actor (actor_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_film_actor_film FOREIGN KEY (film_id)
        REFERENCES catalog_schema.film (film_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE INDEX idx_film_actor_film_id ON catalog_schema.film_actor (film_id);

-- category table
CREATE TABLE catalog_schema.category (
    category_id SERIAL PRIMARY KEY,
    name        VARCHAR(25) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- film_category junction table (intra-schema FKs preserved)
CREATE TABLE catalog_schema.film_category (
    film_id     INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (film_id, category_id),
    CONSTRAINT fk_film_category_film FOREIGN KEY (film_id)
        REFERENCES catalog_schema.film (film_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_film_category_category FOREIGN KEY (category_id)
        REFERENCES catalog_schema.category (category_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- film_text table with full-text search
-- MySQL FULLTEXT INDEX → PostgreSQL tsvector + GIN index
CREATE TABLE catalog_schema.film_text (
    film_text_id SERIAL PRIMARY KEY,
    film_id      INTEGER NOT NULL,
    title        VARCHAR(128) NOT NULL,
    description  TEXT,
    fulltext     tsvector
);

CREATE INDEX idx_film_text_fulltext ON catalog_schema.film_text USING GIN (fulltext);

-- last_update triggers for all catalog tables
CREATE TRIGGER set_last_update_language
    BEFORE UPDATE ON catalog_schema.language
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_film
    BEFORE UPDATE ON catalog_schema.film
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_actor
    BEFORE UPDATE ON catalog_schema.actor
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_film_actor
    BEFORE UPDATE ON catalog_schema.film_actor
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_category
    BEFORE UPDATE ON catalog_schema.category
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();

CREATE TRIGGER set_last_update_film_category
    BEFORE UPDATE ON catalog_schema.film_category
    FOR EACH ROW
    EXECUTE FUNCTION update_last_update_column();
