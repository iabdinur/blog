-- Reset seeded content to a clean professional baseline.
-- This migration is intended for environments that already ran V2 seed data.
-- It removes mock/demo content, then inserts a single admin user and core topic tags.

BEGIN;

-- Wipe content and related derived data while preserving schema.
-- Order includes FK-dependent tables; CASCADE handles remaining references.
TRUNCATE TABLE
    post_tags,
    comments,
    posts,
    authors,
    newsletter_subscriptions,
    sent_emails,
    verification_codes,
    users,
    tags
    RESTART IDENTITY CASCADE;

-- Seed primary admin/author login account.
-- Password hash corresponds to your chosen initial password.
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

-- Seed canonical topic tags for initial publishing taxonomy.
-- ON CONFLICT ensures reruns (or partial manual inserts) remain safe.
INSERT INTO tags (name, slug, description, posts_count, created_at, updated_at)
VALUES
    ('Artificial Intelligence', 'ai', 'Artificial Intelligence and Machine Learning articles', 1, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('DevOps', 'devops', 'DevOps workflows and practices', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('SWE Interview Preparation', 'interview-prep', 'Interview preparation guides and resources', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('System Design', 'system-design', 'System design fundamentals and patterns', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('UIUC Master of Computer Science', 'uiuc-mcs', 'UIUC Master of Computer Science reflections and course reviews', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('Software Engineering', 'software-engineering', 'Software engineering principles, practices, and methodologies', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00')
ON CONFLICT (slug) DO NOTHING;

COMMIT;