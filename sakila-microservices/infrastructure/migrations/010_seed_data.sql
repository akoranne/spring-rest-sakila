-- ============================================================
-- 010_seed_data.sql
-- Representative seed data for all schemas
-- Provides a minimal dataset for development and testing
-- ============================================================

BEGIN;

-- ============================================================
-- auth_schema seed data
-- Password hash is bcrypt of 'password123'
-- ============================================================
INSERT INTO auth_schema.authority (email, password, authority) VALUES
    ('admin@sakila.com',   '$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', ARRAY['ROLE_READ','ROLE_WRITE','ROLE_MANAGE','ROLE_ADMIN']),
    ('manager@sakila.com', '$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', ARRAY['ROLE_READ','ROLE_WRITE','ROLE_MANAGE']),
    ('reader@sakila.com',  '$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', ARRAY['ROLE_READ']);

-- ============================================================
-- catalog_schema seed data
-- ============================================================
INSERT INTO catalog_schema.language (name) VALUES
    ('English'),
    ('French');

INSERT INTO catalog_schema.film (title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features) VALUES
    ('ACADEMY DINOSAUR', 'A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies', 2006, 1, 6, 0.99, 86, 20.99, 'PG', ARRAY['Deleted Scenes','Behind the Scenes']),
    ('ACE GOLDFINGER', 'A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China', 2006, 1, 3, 4.99, 48, 12.99, 'G', ARRAY['Trailers','Deleted Scenes']),
    ('ADAPTATION HOLES', 'A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory', 2006, 1, 7, 2.99, 50, 18.99, 'NC-17', ARRAY['Trailers','Deleted Scenes']),
    ('AFFAIR PREJUDICE', 'A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank', 2006, 1, 5, 2.99, 117, 26.99, 'G', ARRAY['Commentaries','Behind the Scenes']);

INSERT INTO catalog_schema.actor (first_name, last_name) VALUES
    ('PENELOPE', 'GUINESS'),
    ('NICK', 'WAHLBERG'),
    ('ED', 'CHASE'),
    ('JENNIFER', 'DAVIS');

-- film_actor associations
INSERT INTO catalog_schema.film_actor (actor_id, film_id) VALUES
    (1, 1),  -- PENELOPE GUINESS in ACADEMY DINOSAUR
    (2, 1),  -- NICK WAHLBERG in ACADEMY DINOSAUR
    (1, 2),  -- PENELOPE GUINESS in ACE GOLDFINGER
    (3, 3),  -- ED CHASE in ADAPTATION HOLES
    (4, 4);  -- JENNIFER DAVIS in AFFAIR PREJUDICE

INSERT INTO catalog_schema.category (name) VALUES
    ('Action'),
    ('Comedy'),
    ('Drama');

-- film_category associations
INSERT INTO catalog_schema.film_category (film_id, category_id) VALUES
    (1, 3),  -- ACADEMY DINOSAUR → Drama
    (2, 1),  -- ACE GOLDFINGER → Action
    (3, 1),  -- ADAPTATION HOLES → Action
    (4, 2);  -- AFFAIR PREJUDICE → Comedy

-- film_text entries with tsvector
INSERT INTO catalog_schema.film_text (film_id, title, description, fulltext) VALUES
    (1, 'ACADEMY DINOSAUR', 'A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies',
        to_tsvector('english', 'ACADEMY DINOSAUR A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies')),
    (2, 'ACE GOLDFINGER', 'A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China',
        to_tsvector('english', 'ACE GOLDFINGER A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China')),
    (3, 'ADAPTATION HOLES', 'A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory',
        to_tsvector('english', 'ADAPTATION HOLES A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory')),
    (4, 'AFFAIR PREJUDICE', 'A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank',
        to_tsvector('english', 'AFFAIR PREJUDICE A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank'));

-- ============================================================
-- location_schema seed data
-- ============================================================
INSERT INTO location_schema.country (country) VALUES
    ('United States'),
    ('Canada');

INSERT INTO location_schema.city (city, country_id) VALUES
    ('San Francisco', 1),
    ('New York', 1),
    ('Toronto', 2);

INSERT INTO location_schema.address (address, address2, district, city_id, postal_code, phone) VALUES
    ('123 Main St', NULL, 'California', 1, '94102', '555-0101'),
    ('456 Oak Ave', 'Suite 200', 'New York', 2, '10001', '555-0102'),
    ('789 Maple Dr', NULL, 'Ontario', 3, 'M5V 2T6', '555-0103'),
    ('321 Pine Rd', NULL, 'California', 1, '94103', '555-0104');

-- ============================================================
-- customer_schema seed data
-- ============================================================
INSERT INTO customer_schema.customer (store_id, first_name, last_name, address_id, active, authority_id) VALUES
    (1, 'MARY', 'SMITH', 1, TRUE, 2),
    (1, 'PATRICIA', 'JOHNSON', 2, TRUE, 2),
    (2, 'LINDA', 'WILLIAMS', 3, TRUE, 3),
    (2, 'BARBARA', 'JONES', 4, FALSE, 3);

-- ============================================================
-- store_schema seed data
-- Uses SET CONSTRAINTS ALL DEFERRED to handle circular
-- dependency between store and staff tables
-- ============================================================
SET CONSTRAINTS ALL DEFERRED;

-- Insert stores first with NULL manager
INSERT INTO store_schema.store (manager_staff_id, address_id) VALUES
    (NULL, 1),
    (NULL, 2);

-- Insert staff
INSERT INTO store_schema.staff (first_name, last_name, address_id, store_id, active, username, authority_id) VALUES
    ('Mike', 'Hillyer', 3, 1, TRUE, 'Mike', 1),
    ('Jon', 'Stephens', 4, 2, TRUE, 'Jon', 2),
    ('Sarah', 'Connor', 1, 1, TRUE, 'Sarah', 2);

-- Update stores with manager references
UPDATE store_schema.store SET manager_staff_id = 1 WHERE store_id = 1;
UPDATE store_schema.store SET manager_staff_id = 2 WHERE store_id = 2;

-- Inventory items
INSERT INTO store_schema.inventory (film_id, store_id) VALUES
    (1, 1),  -- ACADEMY DINOSAUR at store 1
    (1, 1),  -- second copy at store 1
    (2, 1),  -- ACE GOLDFINGER at store 1
    (3, 2),  -- ADAPTATION HOLES at store 2
    (4, 2);  -- AFFAIR PREJUDICE at store 2

-- ============================================================
-- rental_schema seed data
-- Mix of returned and unreturned rentals
-- ============================================================
INSERT INTO rental_schema.rental (rental_date, inventory_id, customer_id, return_date, staff_id) VALUES
    ('2024-01-15 10:00:00', 1, 1, '2024-01-20 14:00:00', 1),  -- returned
    ('2024-01-16 11:00:00', 2, 2, '2024-01-22 09:00:00', 1),  -- returned
    ('2024-02-01 09:00:00', 3, 1, NULL, 2),                     -- NOT returned (held by customer 1)
    ('2024-02-10 15:00:00', 4, 3, '2024-02-15 10:00:00', 2),  -- returned
    ('2024-02-20 12:00:00', 5, 4, NULL, 1);                     -- NOT returned (held by customer 4)

-- ============================================================
-- payment_schema seed data
-- ============================================================
INSERT INTO payment_schema.payment (customer_id, staff_id, rental_id, amount, payment_date) VALUES
    (1, 1, 1, 0.99, '2024-01-15 10:05:00'),
    (2, 1, 2, 0.99, '2024-01-16 11:05:00'),
    (1, 2, 3, 4.99, '2024-02-01 09:05:00'),
    (3, 2, 4, 2.99, '2024-02-10 15:05:00'),
    (4, 1, 5, 2.99, '2024-02-20 12:05:00');

COMMIT;
