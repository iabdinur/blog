# Database Reset Instructions

To start from a fresh database, follow these steps:

## 1. Drop and Recreate Database

```bash
# Connect to PostgreSQL
psql -U postgres

# Drop the database (if it exists)
DROP DATABASE IF EXISTS blog;

# Create a fresh database
CREATE DATABASE blog;

# Exit psql
\q
```

## 2. Run Migrations

The migrations will run automatically when you start the Spring Boot application. The migrations are:

1. **V1__Initial_schema.sql** - Creates core tables:
   - `authors`
   - `tags`
   - `posts`
   - `post_tags` (junction table)
   - `comments`

2. **V2__Seed_mock_data.sql** - Seeds initial data:
   - One author (Ibrahim Abdinur)
   - Five tags/series
   - Sample posts with content

3. **V3__Create_accounts_table.sql** - Creates admin authentication:
   - `accounts` table for admin login

## 3. Create Admin Account

After migrations run, create an admin account:

```sql
psql -U postgres -d blog

INSERT INTO accounts (username, password, created_at, updated_at)
VALUES ('admin', 'password123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

**Note:** In production, use password hashing (BCrypt). This is just for development.

## 4. Verify Tables

```sql
-- List all tables
\dt

-- Check table structure
\d authors
\d tags
\d posts
\d post_tags
\d comments
\d accounts
```

## Migration Order

Migrations run in alphabetical order by filename:
- V1__Initial_schema.sql
- V2__Seed_mock_data.sql
- V3__Create_accounts_table.sql

## Troubleshooting

If you encounter issues:

1. **Flyway checksum mismatch**: Drop and recreate the database
2. **Sequence errors**: BIGSERIAL automatically creates sequences, no manual creation needed
3. **Foreign key errors**: Ensure migrations run in order (V1 before V2)

