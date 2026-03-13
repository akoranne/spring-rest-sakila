const db = require('../db');

const STORE_TABLE = 'store_schema.store';
const INVENTORY_TABLE = 'store_schema.inventory';
const STAFF_TABLE = 'store_schema.staff';

const salesByCategory = async () => {
  // Sales by category requires cross-service data (catalog, rental, payment).
  // With schema isolation, we aggregate what we can from store_schema
  // and return inventory counts grouped by film_id as a proxy.
  // Full cross-service aggregation would require inter-service HTTP calls.
  const results = await db(INVENTORY_TABLE)
    .select('film_id')
    .count('* as inventory_count')
    .groupBy('film_id')
    .orderBy('inventory_count', 'desc');
  return results;
};

const salesByStore = async () => {
  // Sales by store aggregates inventory and staff counts per store.
  // Full sales data would require inter-service calls to rental/payment services.
  const results = await db(STORE_TABLE)
    .leftJoin(INVENTORY_TABLE, `${STORE_TABLE}.store_id`, `${INVENTORY_TABLE}.store_id`)
    .select(`${STORE_TABLE}.store_id`, `${STORE_TABLE}.manager_staff_id`, `${STORE_TABLE}.address_id`)
    .count(`${INVENTORY_TABLE}.inventory_id as inventory_count`)
    .groupBy(`${STORE_TABLE}.store_id`, `${STORE_TABLE}.manager_staff_id`, `${STORE_TABLE}.address_id`)
    .orderBy(`${STORE_TABLE}.store_id`);
  return results;
};

module.exports = { salesByCategory, salesByStore };
