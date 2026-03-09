#!/bin/bash
# ============================================================
# init-schemas.sh
# Creates per-service database users with schema-scoped privileges.
# Each user gets USAGE + full DML on its own schema only.
# Run as the PostgreSQL superuser (e.g. via docker-entrypoint-initdb.d).
# ============================================================

set -euo pipefail

# Use the POSTGRES_DB env var if set, otherwise default to "sakila"
DB="${POSTGRES_DB:-sakila}"

# Default password for service users (override via env vars in production)
DEFAULT_PASSWORD="${SERVICE_USER_PASSWORD:-changeme}"

# Schema-to-user mapping
declare -A SCHEMA_MAP=(
  [auth_schema]=auth_user
  [catalog_schema]=catalog_user
  [customer_schema]=customer_user
  [location_schema]=location_user
  [payment_schema]=payment_user
  [rental_schema]=rental_user
  [store_schema]=store_user
)

echo "=== Creating per-service database users ==="

for schema in "${!SCHEMA_MAP[@]}"; do
  user="${SCHEMA_MAP[$schema]}"
  echo "Creating user '${user}' for schema '${schema}'..."

  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$DB" <<-EOSQL
    -- Create user if it does not already exist
    DO \$\$
    BEGIN
      IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${user}') THEN
        CREATE ROLE ${user} LOGIN PASSWORD '${DEFAULT_PASSWORD}';
      END IF;
    END
    \$\$;

    -- Revoke default public schema access
    REVOKE ALL ON SCHEMA public FROM ${user};

    -- Grant USAGE on the service's own schema
    GRANT USAGE ON SCHEMA ${schema} TO ${user};

    -- Grant DML privileges on all existing tables in the schema
    GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA ${schema} TO ${user};

    -- Grant USAGE on all sequences (needed for SERIAL columns / nextval)
    GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA ${schema} TO ${user};

    -- Ensure future tables/sequences in this schema also get the same grants
    ALTER DEFAULT PRIVILEGES IN SCHEMA ${schema}
      GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ${user};
    ALTER DEFAULT PRIVILEGES IN SCHEMA ${schema}
      GRANT USAGE, SELECT ON SEQUENCES TO ${user};
EOSQL

  echo "User '${user}' configured for schema '${schema}'."
done

echo "=== All service users created successfully ==="
