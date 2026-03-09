-- ============================================================
-- 008_functions.sql
-- Stored procedures and functions migration
-- Converts MySQL stored procedures/functions to PostgreSQL
-- ============================================================
-- NOTE: Functions in store_schema and customer_schema require
-- cross-schema read access to rental_schema and payment_schema.
-- The db-init script (init-schemas.sh) must grant:
--   - store_user: SELECT on rental_schema.rental
--   - customer_user: SELECT on rental_schema.rental, payment_schema.payment
-- ============================================================

-- ============================================================
-- store_schema functions
-- ============================================================

-- inventory_in_stock(p_inventory_id)
-- Returns TRUE if the inventory item has no unreturned rentals.
-- Requires cross-schema read access to rental_schema.rental.
CREATE OR REPLACE FUNCTION store_schema.inventory_in_stock(p_inventory_id INTEGER)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $func$
DECLARE
    v_rentals INTEGER;
    v_out     INTEGER;
BEGIN
    -- Count total rentals for this inventory item
    SELECT COUNT(*)
      INTO v_rentals
      FROM rental_schema.rental
     WHERE inventory_id = p_inventory_id;

    IF v_rentals = 0 THEN
        RETURN TRUE;
    END IF;

    -- Count unreturned rentals
    SELECT COUNT(*)
      INTO v_out
      FROM rental_schema.rental
     WHERE inventory_id = p_inventory_id
       AND return_date IS NULL;

    RETURN v_out = 0;
END;
$func$;

-- inventory_held_by_customer(p_inventory_id)
-- Returns the customer_id of the customer currently holding the item
-- (has an unreturned rental), or NULL if not held.
-- Requires cross-schema read access to rental_schema.rental.
CREATE OR REPLACE FUNCTION store_schema.inventory_held_by_customer(p_inventory_id INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
AS $func$
DECLARE
    v_customer_id INTEGER;
BEGIN
    SELECT customer_id
      INTO v_customer_id
      FROM rental_schema.rental
     WHERE inventory_id = p_inventory_id
       AND return_date IS NULL
     ORDER BY rental_date DESC
     LIMIT 1;

    RETURN v_customer_id;
END;
$func$;

-- film_in_stock(p_film_id, p_store_id)
-- Returns a set of inventory_ids that are currently in stock
-- for a given film at a given store.
CREATE OR REPLACE FUNCTION store_schema.film_in_stock(
    p_film_id  INTEGER,
    p_store_id INTEGER,
    OUT p_film_count INTEGER
)
RETURNS SETOF INTEGER
LANGUAGE plpgsql
AS $func$
BEGIN
    SELECT COUNT(*)
      INTO p_film_count
      FROM store_schema.inventory
     WHERE film_id = p_film_id
       AND store_id = p_store_id
       AND store_schema.inventory_in_stock(inventory_id);

    RETURN QUERY
        SELECT inventory_id
          FROM store_schema.inventory
         WHERE film_id = p_film_id
           AND store_id = p_store_id
           AND store_schema.inventory_in_stock(inventory_id);
END;
$func$;

-- film_not_in_stock(p_film_id, p_store_id)
-- Returns a set of inventory_ids that are NOT currently in stock
-- for a given film at a given store.
CREATE OR REPLACE FUNCTION store_schema.film_not_in_stock(
    p_film_id  INTEGER,
    p_store_id INTEGER,
    OUT p_film_count INTEGER
)
RETURNS SETOF INTEGER
LANGUAGE plpgsql
AS $func$
BEGIN
    SELECT COUNT(*)
      INTO p_film_count
      FROM store_schema.inventory
     WHERE film_id = p_film_id
       AND store_id = p_store_id
       AND NOT store_schema.inventory_in_stock(inventory_id);

    RETURN QUERY
        SELECT inventory_id
          FROM store_schema.inventory
         WHERE film_id = p_film_id
           AND store_id = p_store_id
           AND NOT store_schema.inventory_in_stock(inventory_id);
END;
$func$;

-- ============================================================
-- customer_schema functions
-- ============================================================

-- get_customer_balance(p_customer_id, p_effective_date)
-- Calculates customer balance as:
--   rental_rate for each rental before effective_date
--   + replacement_cost for overdue/unreturned rentals
--   - total payments before effective_date
-- Requires cross-schema read access to rental_schema.rental,
-- payment_schema.payment, store_schema.inventory, and catalog_schema.film.
CREATE OR REPLACE FUNCTION customer_schema.get_customer_balance(
    p_customer_id    INTEGER,
    p_effective_date TIMESTAMP
)
RETURNS NUMERIC(5,2)
LANGUAGE plpgsql
AS $func$
DECLARE
    v_rental_fees  NUMERIC(5,2);
    v_overpay_fees NUMERIC(5,2);
    v_payments     NUMERIC(5,2);
BEGIN
    -- Sum rental fees: rental_rate for each rental before effective_date
    SELECT COALESCE(SUM(f.rental_rate), 0)
      INTO v_rental_fees
      FROM rental_schema.rental r
      JOIN store_schema.inventory i ON r.inventory_id = i.inventory_id
      JOIN catalog_schema.film f ON i.film_id = f.film_id
     WHERE r.customer_id = p_customer_id
       AND r.rental_date <= p_effective_date;

    -- Sum replacement costs for overdue/unreturned rentals
    SELECT COALESCE(SUM(
        CASE
            WHEN r.return_date IS NULL
                 OR r.return_date > (r.rental_date + (f.rental_duration || ' days')::INTERVAL)
            THEN f.replacement_cost
            ELSE 0
        END
    ), 0)
      INTO v_overpay_fees
      FROM rental_schema.rental r
      JOIN store_schema.inventory i ON r.inventory_id = i.inventory_id
      JOIN catalog_schema.film f ON i.film_id = f.film_id
     WHERE r.customer_id = p_customer_id
       AND r.rental_date <= p_effective_date;

    -- Sum payments made before effective_date
    SELECT COALESCE(SUM(p.amount), 0)
      INTO v_payments
      FROM payment_schema.payment p
     WHERE p.customer_id = p_customer_id
       AND p.payment_date <= p_effective_date;

    RETURN v_rental_fees + v_overpay_fees - v_payments;
END;
$func$;
