#!/bin/bash

# Database Reset Script
# This script drops and recreates the database for a fresh start

DB_NAME="blog"
DB_USER="${DB_USER:-postgres}"

echo "⚠️  WARNING: This will DROP the database '$DB_NAME' if it exists!"
read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Aborted."
    exit 1
fi

echo "Dropping database '$DB_NAME'..."
psql -U "$DB_USER" -d postgres -c "DROP DATABASE IF EXISTS $DB_NAME;" || {
    echo "Error: Could not drop database. Make sure PostgreSQL is running and you have proper permissions."
    exit 1
}

echo "Creating fresh database '$DB_NAME'..."
psql -U "$DB_USER" -d postgres -c "CREATE DATABASE $DB_NAME;" || {
    echo "Error: Could not create database."
    exit 1
}

echo "✅ Database '$DB_NAME' has been reset!"
echo ""
echo "Next steps:"
echo "1. Start the Spring Boot application - migrations will run automatically"
echo "2. Create an admin account:"
echo "   psql -U $DB_USER -d $DB_NAME -c \"INSERT INTO accounts (username, password) VALUES ('admin', 'password123');\""

