-- ============================================================
-- 009_views.sql
-- Views migration
-- Converts MySQL views to PostgreSQL equivalents
-- ============================================================

-- ============================================================
-- catalog_schema.film_list view
-- Self-contained within catalog_schema (no cross-schema access needed)
-- Produces a denormalized film listing with category and actors
-- ============================================================
CREATE OR REPLACE VIEW catalog_schema.film_list AS
SELECT
    f.film_id    AS fid,
    f.title      AS title,
    f.description AS description,
    c.name       AS category,
    f.rental_rate AS price,
    f.length     AS length,
    f.rating     AS rating,
    STRING_AGG(
        a.first_name || ' ' || a.last_name, ', '
        ORDER BY a.last_name, a.first_name
    ) AS actors
FROM catalog_schema.film f
LEFT JOIN catalog_schema.film_category fc ON f.film_id = fc.film_id
LEFT JOIN catalog_schema.category c ON fc.category_id = c.category_id
LEFT JOIN catalog_schema.film_actor fa ON f.film_id = fa.film_id
LEFT JOIN catalog_schema.actor a ON fa.actor_id = a.actor_id
GROUP BY f.film_id, f.title, f.description, c.name, f.rental_rate, f.length, f.rating;

-- ============================================================
-- store_schema.staff_list view
-- Requires cross-schema read access to location_schema
-- (address, city, country tables).
-- The db-init script must grant store_user SELECT on
-- location_schema.address, location_schema.city, location_schema.country.
-- ============================================================
CREATE OR REPLACE VIEW store_schema.staff_list AS
SELECT
    s.staff_id   AS id,
    s.first_name || ' ' || s.last_name AS name,
    a.address    AS address,
    a.postal_code AS "zip code",
    a.phone      AS phone,
    ci.city      AS city,
    co.country   AS country,
    s.store_id   AS sid
FROM store_schema.staff s
JOIN location_schema.address a ON s.address_id = a.address_id
JOIN location_schema.city ci ON a.city_id = ci.city_id
JOIN location_schema.country co ON ci.country_id = co.country_id;
