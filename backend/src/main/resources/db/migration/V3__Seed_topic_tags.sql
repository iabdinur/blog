-- Insert Tags (Series)
INSERT INTO tags (name, slug, description, posts_count, created_at, updated_at)
VALUES
    ('Artificial Intelligence', 'ai', 'Artificial Intelligence and Machine Learning articles', 1, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('DevOps', 'devops', 'DevOps workflows and practices', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('SWE Interview Preparation', 'interview-prep', 'Interview preparation guides and resources', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('System Design', 'system-design', 'System design fundamentals and patterns', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('UIUC Master of Computer Science', 'uiuc-mcs', 'UIUC Master of Computer Science reflections and course reviews', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('Software Engineering', 'software-engineering', 'Software engineering principles, practices, and methodologies', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00')
ON CONFLICT (slug) DO NOTHING;