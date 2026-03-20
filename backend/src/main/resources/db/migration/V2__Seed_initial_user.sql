-- Insert Admin User (Author type)
INSERT INTO users (name, email, password, user_type, created_at, updated_at)
VALUES (
    'Ibrahim Abdinur',
    'iabdinur@icloud.com',
    '$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6',
    'AUT',
    '2024-01-01 00:00:00',
    '2024-01-01 00:00:00'
)
ON CONFLICT (email) DO NOTHING;
