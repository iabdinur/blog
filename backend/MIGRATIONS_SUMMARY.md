# Database Migrations Summary

## Migration Files (in execution order)

### V1__Initial_schema.sql
Creates the core blog schema:
- **authors** - Blog authors/writers with profile information
- **tags** - Series/categories for posts
- **posts** - Blog posts with content, metadata, and publishing status
- **post_tags** - Many-to-many junction table linking posts to tags
- **comments** - Post comments with support for nested replies

**Indexes created:**
- `idx_posts_author_id`, `idx_posts_slug`, `idx_posts_published_at`, `idx_posts_is_published`
- `idx_post_tags_post_id`, `idx_post_tags_tag_id`
- `idx_comments_post_id`, `idx_comments_author_id`, `idx_comments_parent_id`
- `idx_tags_slug`, `idx_authors_username`

### V2__Seed_mock_data.sql
Seeds initial development data:
- 1 author (Ibrahim Abdinur)
- 5 tags/series (AI, DevOps, Interview Prep, System Design, UIUC MCS)
- Sample blog posts with full content

**Note:** This migration contains mock data. In production, you may want to skip or modify this.

### V3__Create_accounts_table.sql
Creates admin authentication:
- **accounts** - Admin user accounts for login
- Index: `idx_accounts_username`

**Note:** BIGSERIAL automatically creates sequences. No manual sequence creation needed.

## Final Database Schema

### Tables (6 total):
1. `authors` - Blog authors
2. `tags` - Post categories/series
3. `posts` - Blog posts
4. `post_tags` - Post-Tag relationships
5. `comments` - Post comments
6. `accounts` - Admin users

### Sequences (auto-created by BIGSERIAL):
- `authors_id_seq`
- `tags_id_seq`
- `posts_id_seq`
- `comments_id_seq`
- `accounts_id_seq`

### Flyway Metadata:
- `flyway_schema_history` - Tracks migration execution

## Starting Fresh

### Option 1: Manual Reset
```bash
# Drop database
psql -U postgres -d postgres -c "DROP DATABASE IF EXISTS blog;"

# Create fresh database
psql -U postgres -d postgres -c "CREATE DATABASE blog;"

# Start Spring Boot - migrations run automatically
cd backend
mvn spring-boot:run
```

### Option 2: Using Reset Script
```bash
cd backend
./scripts/reset-database.sh
```

### After Migrations Run:

1. **Create Admin Account:**
```sql
psql -U postgres -d blog

INSERT INTO accounts (username, password, created_at, updated_at)
VALUES ('admin', 'password123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

2. **Verify Tables:**
```sql
\dt  -- List all tables
\d authors  -- Check table structure
```

## Migration Configuration

**application.yaml:**
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0
    validate-on-migrate: true
  jpa:
    hibernate:
      ddl-auto: none  # Flyway manages schema
```

## Important Notes

1. **Sequences**: BIGSERIAL automatically creates sequences. Don't create them manually.
2. **Order Matters**: Migrations run in alphabetical order by filename prefix (V1, V2, V3...)
3. **Foreign Keys**: V1 must run before V2 (which references authors and tags)
4. **Flyway History**: The `flyway_schema_history` table tracks all executed migrations
5. **Validation**: `validate-on-migrate: true` ensures migrations haven't been modified after execution

## Troubleshooting

### Checksum Mismatch
If you modify a migration after it's been executed:
```sql
-- Option 1: Drop and recreate database (recommended for development)
DROP DATABASE blog;
CREATE DATABASE blog;

-- Option 2: Update checksum manually (not recommended)
UPDATE flyway_schema_history 
SET checksum = <new_checksum> 
WHERE version = '<version>';
```

### Sequence Errors
BIGSERIAL handles sequences automatically. If you see sequence errors:
- Check that you're not manually creating sequences
- Ensure JPA entity sequence names match PostgreSQL auto-generated names

### Foreign Key Errors
Ensure migrations run in order:
- V1 (creates tables) must run before V2 (inserts data)
- V1 must run before V3 (accounts can be created independently, but V1 should be first)

