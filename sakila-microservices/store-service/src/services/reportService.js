const db = require('../db');

const salesByCategory = async () => {
  const results = await db.raw(`
    SELECT c.name AS category, SUM(p.amount) AS total_sales
    FROM catalog_schema.category c
    JOIN catalog_schema.film_category fc ON c.category_id = fc.category_id
    JOIN store_schema.inventory i ON fc.film_id = i.film_id
    JOIN rental_schema.rental r ON i.inventory_id = r.inventory_id
    JOIN payment_schema.payment p ON r.rental_id = p.rental_id
    GROUP BY c.name
    ORDER BY total_sales DESC
  `);
  return results.rows;
};

const salesByStore = async () => {
  const results = await db.raw(`
    SELECT s.store_id, SUM(p.amount) AS total_sales
    FROM store_schema.store s
    JOIN store_schema.inventory i ON s.store_id = i.store_id
    JOIN rental_schema.rental r ON i.inventory_id = r.inventory_id
    JOIN payment_schema.payment p ON r.rental_id = p.rental_id
    GROUP BY s.store_id
    ORDER BY total_sales DESC
  `);
  return results.rows;
};

module.exports = { salesByCategory, salesByStore };
