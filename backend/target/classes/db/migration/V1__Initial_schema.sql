-- Create authors table
CREATE TABLE authors
(
    id             BIGSERIAL PRIMARY KEY,
    name           TEXT NOT NULL,
    username       TEXT NOT NULL,
    email          TEXT NOT NULL,
    bio            TEXT,
    avatar         TEXT,
    cover_image    TEXT,
    location       TEXT,
    website        TEXT,
    twitter        TEXT,
    github         TEXT,
    linkedin       TEXT,
    followers_count INTEGER NOT NULL DEFAULT 0,
    following_count INTEGER NOT NULL DEFAULT 0,
    posts_count    INTEGER NOT NULL DEFAULT 0,
    joined_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT authors_username_unique UNIQUE (username),
    CONSTRAINT authors_email_unique UNIQUE (email)
);

-- Create tags (series) table
CREATE TABLE tags
(
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    slug        TEXT NOT NULL,
    description TEXT,
    posts_count INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tags_name_unique UNIQUE (name),
    CONSTRAINT tags_slug_unique UNIQUE (slug)
);

-- Create posts table
CREATE TABLE posts
(
    id              BIGSERIAL PRIMARY KEY,
    title           TEXT NOT NULL,
    slug            TEXT NOT NULL,
    content         TEXT NOT NULL,
    excerpt         TEXT,
    cover_image     TEXT,
    author_id       BIGINT NOT NULL,
    published_at    TIMESTAMP WITHOUT TIME ZONE,
    is_published    BOOLEAN NOT NULL DEFAULT false,
    views           BIGINT NOT NULL DEFAULT 0,
    likes           BIGINT NOT NULL DEFAULT 0,
    comments_count  INTEGER NOT NULL DEFAULT 0,
    reading_time    INTEGER,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT posts_slug_unique UNIQUE (slug),
    CONSTRAINT posts_author_id_fkey FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

-- Create post_tags junction table
CREATE TABLE post_tags
(
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT post_tags_post_id_fkey FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT post_tags_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Create comments table
CREATE TABLE comments
(
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    author_id  BIGINT NOT NULL,
    content    TEXT NOT NULL,
    parent_id  BIGINT,
    likes      INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT comments_post_id_fkey FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT comments_author_id_fkey FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE,
    CONSTRAINT comments_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
);

-- Create users table (authentication)
-- Note: BIGSERIAL automatically creates the sequence users_id_seq
CREATE TABLE users
(
    id              BIGSERIAL PRIMARY KEY,
    name            TEXT NOT NULL,
    email           TEXT NOT NULL,
    password        TEXT NOT NULL,
    profile_image_id TEXT,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_email_unique UNIQUE (email),
    CONSTRAINT profile_image_id_unique UNIQUE (profile_image_id)
);

-- Create verification_codes table
CREATE TABLE verification_codes
(
    id              BIGSERIAL PRIMARY KEY,
    email           TEXT NOT NULL,
    hashed_code     TEXT NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    attempts        INTEGER NOT NULL DEFAULT 0,
    is_used         BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create indexes for better query performance
CREATE INDEX idx_posts_author_id ON posts(author_id);
CREATE INDEX idx_posts_slug ON posts(slug);
CREATE INDEX idx_posts_published_at ON posts(published_at);
CREATE INDEX idx_posts_is_published ON posts(is_published);
CREATE INDEX idx_post_tags_post_id ON post_tags(post_id);
CREATE INDEX idx_post_tags_tag_id ON post_tags(tag_id);
CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);
CREATE INDEX idx_tags_slug ON tags(slug);
CREATE INDEX idx_authors_username ON authors(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_verification_codes_email ON verification_codes(email);
CREATE INDEX idx_verification_codes_expires_at ON verification_codes(expires_at);
CREATE INDEX idx_verification_codes_email_created ON verification_codes(email, created_at);
