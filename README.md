# Blog Platform

A full-stack blog platform combining features from Hashnode and Substack, built with modern technologies and designed to scale to 10,000+ readers.

## 🏗️ Architecture Overview

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend Layer                        │
│  React 18 + TypeScript + Vite + Chakra UI                   │
│  - Client-Side Rendering                                    │
│  - State Management (Zustand)                               │
│  - API Integration (Axios + React Query)                    │
│  - Markdown Rendering with Syntax Highlighting              │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/REST API
┌──────────────────────▼──────────────────────────────────────┐
│                      API Gateway Layer                      │
│  Spring Boot 3.4.2 + Java 21                               │
│  - RESTful API (v1)                                        │
│  - JWT Authentication                                       │
│  - CORS Configuration                                       │
│  - Global Exception Handling                               │
│  - Email Templates (Thymeleaf)                              │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┬──────────────┐
        │              │              │              │
┌───────▼──────┐ ┌─────▼─────┐ ┌─────▼─────┐ ┌─────▼─────┐
│  PostgreSQL  │ │   AWS S3   │ │  AWS SES  │ │   Email   │
│  (Primary)   │ │  (Storage) │ │  (Email)  │ │ Templates │
│              │ │            │ │           │ │           │
│  - Users     │ │  - Profile │ │  - Verify │ │  - Verify │
│  - Authors   │ │    Images  │ │    Codes  │ │    Email  │
│  - Posts     │ │            │ │  - Post    │ │  - Post   │
│  - Tags      │ │            │ │    Notify │ │    Notify │
│  - Comments  │ │            │ │           │ │           │
│  - Newsletter│ │            │ │           │ │           │
│  - Reactions │ │            │ │           │ │           │
└──────────────┘ └────────────┘ └───────────┘ └───────────┘
```

### Core Components

#### Frontend (React + TypeScript)
- **Framework**: React 18.3.1 with TypeScript 5.8.3
- **Build Tool**: Vite 7.0.4
- **UI Library**: Chakra UI 2.10.9
- **State Management**: Zustand 5.0.6
- **Data Fetching**: TanStack React Query 5.83.0
- **Routing**: React Router DOM 7.6.3
- **Forms**: React Hook Form 7.60.0 + Zod 4.0.5
- **HTTP Client**: Axios 1.10.0
- **Markdown**: React Markdown with syntax highlighting
- **Icons**: React Icons 5.5.0

#### Backend (Spring Boot + Java)
- **Framework**: Spring Boot 3.4.2
- **Language**: Java 21
- **Database**: PostgreSQL 17.4
- **ORM**: Spring JDBC (Custom RowMappers)
- **Migrations**: Flyway
- **Security**: Spring Security + JWT
- **Validation**: Spring Boot Validation
- **Email**: AWS SES + Thymeleaf Templates
- **Storage**: AWS S3 (Profile Images)
- **Testing**: JUnit 5 + Mockito + Testcontainers

## ✨ Features

### User Features
- **Authentication**: Email-based verification code system
- **User Profiles**: Customizable profiles with image uploads
- **Reading List**: Save posts for later reading
- **Comments**: Nested comment system with edit/delete
- **Reactions**: Like posts and comments
- **Newsletter**: Subscribe/unsubscribe with email preferences
- **Search**: Full-text search across posts
- **Dark Mode**: Theme toggle support

### Author Features
- **Post Management**: Create, edit, delete, and publish posts
- **Draft System**: Save posts as drafts
- **Content Images**: Add images within post content using placeholders
- **Tag Management**: Create and manage tags/series
- **Author Profile**: Customize author bio, avatar, and social links
- **Post Scheduling**: Schedule posts for future publication
- **Analytics**: View post views, likes, and comments

### Content Features
- **Markdown Support**: Rich markdown content with syntax highlighting
- **Content Images**: Dynamic image placement within articles
- **Table of Contents**: Auto-generated from headings
- **Reading Progress**: Visual reading progress indicator
- **Related Posts**: Show related articles based on tags
- **Post Series**: Organize posts into series/tags
- **Archive**: Browse all posts with filtering

## 📁 Project Structure

```
blog/
├── frontend/                 # React frontend application
│   ├── src/
│   │   ├── api/            # API client and endpoints
│   │   │   ├── admin.ts    # Admin/author endpoints
│   │   │   ├── authors.ts  # Author endpoints
│   │   │   ├── client.ts   # Axios client configuration
│   │   │   ├── comments.ts # Comment endpoints
│   │   │   ├── newsletter.ts # Newsletter endpoints
│   │   │   ├── posts.ts    # Post endpoints
│   │   │   ├── reactions.ts # Reaction endpoints
│   │   │   ├── search.ts   # Search endpoints
│   │   │   └── tags.ts     # Tag endpoints
│   │   ├── components/     # React components
│   │   │   ├── blog/       # Blog-specific components
│   │   │   │   ├── PostCard.tsx
│   │   │   │   ├── PostDetail.tsx
│   │   │   │   ├── PostComments.tsx
│   │   │   │   ├── PostReactions.tsx
│   │   │   │   ├── PostTags.tsx
│   │   │   │   ├── PostShare.tsx
│   │   │   │   ├── PostMeta.tsx
│   │   │   │   ├── PostList.tsx
│   │   │   │   ├── PostAuthor.tsx
│   │   │   │   ├── ReadingProgress.tsx
│   │   │   │   └── TableOfContents.tsx
│   │   │   ├── forms/      # Form components
│   │   │   │   ├── ContactForm.tsx
│   │   │   │   └── SearchForm.tsx
│   │   │   ├── layout/     # Layout components
│   │   │   │   ├── Layout.tsx
│   │   │   │   ├── Navbar.tsx
│   │   │   │   ├── Footer.tsx
│   │   │   │   ├── Sidebar.tsx
│   │   │   │   └── ThemeToggleButton.tsx
│   │   │   ├── newsletter/ # Newsletter components
│   │   │   │   ├── NewsletterCard.tsx
│   │   │   │   ├── SubscribeForm.tsx
│   │   │   │   ├── SubscriptionStatus.tsx
│   │   │   │   └── EmailPreferences.tsx
│   │   │   └── ui/         # Reusable UI components
│   │   │       ├── Avatar.tsx
│   │   │       ├── Badge.tsx
│   │   │       ├── Button.tsx
│   │   │       ├── Card.tsx
│   │   │       ├── CodeBlock.tsx
│   │   │       ├── LoginRegisterModal.tsx
│   │   │       ├── MarkdownRenderer.tsx
│   │   │       ├── NewsletterPopup.tsx
│   │   │       └── SearchPopup.tsx
│   │   ├── hooks/          # Custom React hooks
│   │   │   └── useScrollProgress.ts
│   │   ├── lib/            # Utility libraries
│   │   │   ├── markdown.ts
│   │   │   └── syntaxHighlight.ts
│   │   ├── pages/          # Page components
│   │   │   ├── Home.tsx
│   │   │   ├── Blog.tsx (Archive)
│   │   │   ├── Post.tsx
│   │   │   ├── Author.tsx
│   │   │   ├── Tag.tsx
│   │   │   ├── Series.tsx
│   │   │   ├── Search.tsx
│   │   │   ├── Newsletter.tsx
│   │   │   ├── About.tsx
│   │   │   ├── Profile.tsx
│   │   │   ├── NotFound.tsx
│   │   │   └── author/     # Author admin pages
│   │   │       ├── Login.tsx
│   │   │       ├── AuthorLayout.tsx
│   │   │       ├── PostsList.tsx
│   │   │       ├── PostForm.tsx
│   │   │       ├── DraftsList.tsx
│   │   │       ├── TagsList.tsx
│   │   │       ├── TagForm.tsx
│   │   │       ├── AuthorsList.tsx
│   │   │       └── AuthorForm.tsx
│   │   ├── store/          # Zustand stores
│   │   │   ├── useAuthStore.ts
│   │   │   ├── useNewsletterStore.ts
│   │   │   ├── useReadingListStore.ts
│   │   │   └── useUIStore.ts
│   │   ├── types/          # TypeScript types
│   │   │   ├── author.ts
│   │   │   ├── comment.ts
│   │   │   ├── newsletter.ts
│   │   │   ├── post.ts
│   │   │   └── tag.ts
│   │   ├── utils/          # Utility functions
│   │   │   ├── auth.ts
│   │   │   ├── codeHighlight.ts
│   │   │   ├── date.ts
│   │   │   ├── markdown.ts
│   │   │   ├── slugify.ts
│   │   │   └── text.ts
│   │   ├── compositions/   # Compositions
│   │   │   └── lib/
│   │   │       └── color-palettes.ts
│   │   └── theme/          # Theme configuration
│   │       └── index.ts
│   ├── public/             # Static assets
│   ├── Dockerfile
│   ├── Dockerrun.aws.json
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.ts
│
├── backend/                # Spring Boot backend API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/iabdinur/
│   │   │   │   ├── config/      # Configuration classes
│   │   │   │   │   ├── CorsConfig.java
│   │   │   │   │   ├── DataAccessConfig.java
│   │   │   │   │   ├── RateLimitingConfig.java
│   │   │   │   │   ├── S3Config.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── controller/ # REST controllers
│   │   │   │   │   ├── ApiInfoController.java
│   │   │   │   │   ├── AuthenticationController.java
│   │   │   │   │   ├── AuthorController.java
│   │   │   │   │   ├── CommentController.java
│   │   │   │   │   ├── NewsletterController.java
│   │   │   │   │   ├── PostController.java
│   │   │   │   │   ├── SearchController.java
│   │   │   │   │   ├── TagController.java
│   │   │   │   │   └── UserController.java
│   │   │   │   ├── dao/         # Data Access Objects
│   │   │   │   │   ├── AuthorDao.java
│   │   │   │   │   ├── CommentDao.java
│   │   │   │   │   ├── NewsletterSubscriptionDao.java
│   │   │   │   │   ├── PostDao.java
│   │   │   │   │   ├── SentEmailDao.java
│   │   │   │   │   ├── TagDao.java
│   │   │   │   │   ├── UserDao.java
│   │   │   │   │   └── VerificationCodeDao.java
│   │   │   │   ├── dto/         # Data Transfer Objects (23 files)
│   │   │   │   ├── exception/   # Exception handlers
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   ├── UnauthorizedException.java
│   │   │   │   │   └── ValidationException.java
│   │   │   │   ├── mapper/      # DTO mappers
│   │   │   │   │   └── UserDTOMapper.java
│   │   │   │   ├── model/        # Domain models
│   │   │   │   │   ├── Author.java
│   │   │   │   │   ├── Comment.java
│   │   │   │   │   ├── NewsletterSubscription.java
│   │   │   │   │   ├── Post.java
│   │   │   │   │   ├── SentEmail.java
│   │   │   │   │   ├── Tag.java
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── UserType.java
│   │   │   │   │   └── VerificationCode.java
│   │   │   │   ├── repository/  # JDBC repositories
│   │   │   │   │   ├── AuthorJDBCDataAccessService.java
│   │   │   │   │   ├── CommentJDBCDataAccessService.java
│   │   │   │   │   ├── NewsletterSubscriptionJDBCDataAccessService.java
│   │   │   │   │   ├── PostJDBCDataAccessService.java
│   │   │   │   │   ├── SentEmailJDBCDataAccessService.java
│   │   │   │   │   ├── TagJDBCDataAccessService.java
│   │   │   │   │   ├── UserJDBCDataAccessService.java
│   │   │   │   │   └── VerificationCodeJDBCDataAccessService.java
│   │   │   │   ├── rowmapper/   # ResultSet mappers
│   │   │   │   │   ├── AuthorRowMapper.java
│   │   │   │   │   ├── CommentRowMapper.java
│   │   │   │   │   ├── PostRowMapper.java
│   │   │   │   │   ├── SentEmailRowMapper.java
│   │   │   │   │   ├── TagRowMapper.java
│   │   │   │   │   ├── UserRowMapper.java
│   │   │   │   │   └── VerificationCodeRowMapper.java
│   │   │   │   ├── service/     # Business logic
│   │   │   │   │   ├── AuthenticationService.java
│   │   │   │   │   ├── AuthorService.java
│   │   │   │   │   ├── CommentService.java
│   │   │   │   │   ├── EmailService.java
│   │   │   │   │   ├── NewsletterService.java
│   │   │   │   │   ├── PostService.java
│   │   │   │   │   ├── ScheduledPostService.java
│   │   │   │   │   ├── SearchService.java
│   │   │   │   │   ├── SesEmailService.java
│   │   │   │   │   ├── S3Service.java
│   │   │   │   │   ├── TagService.java
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   └── UserUserDetailsService.java
│   │   │   │   ├── s3/          # S3 utilities
│   │   │   │   │   └── S3Buckets.java
│   │   │   │   └── util/        # Utilities
│   │   │   │       └── JWTUtil.java
│   │   │   └── resources/
│   │   │       ├── db/migration/ # Flyway migrations
│   │   │       │   ├── V1__Initial_schema.sql
│   │   │       │   ├── V2__Seed_mock_data.sql
│   │   │       │   └── V3__Reset_seed_to_professional_baseline.sql
│   │   │       ├── templates/    # Email templates
│   │   │       │   └── email/
│   │   │       │       ├── verification.html
│   │   │       │       └── post-notification.html
│   │   │       ├── application.yaml
│   │   │       └── application-prod.yaml
│   │   └── test/            # Test classes
│   │       ├── java/com/iabdinur/
│   │       │   ├── journey/     # Integration tests
│   │       │   ├── repository/  # Repository tests
│   │       │   ├── rowmapper/   # RowMapper tests
│   │       │   └── service/     # Service tests
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
│
├── .github/workflows/      # CI/CD pipelines
│   ├── backend-ci.yml
│   ├── backend-cd.yml
│   └── frontend-react-cd.yml
├── Dockerrun.aws.json      # AWS Elastic Beanstalk config
├── docker-compose.yml      # Local development
└── README.md
```

## 🗄️ Database Schema

### Core Tables

#### Users
- User authentication and profile management
- Email verification support
- Profile image storage (S3 key reference)
- User types: READER (REA) and AUTHOR (AUT)

#### Authors
- Author profiles with bio, avatar, social links
- Tracks post counts and author metadata
- Linked to User accounts

#### Posts
- Blog posts with markdown content
- Supports drafts, scheduling, and publishing
- Tracks views, likes, comments count
- Content images with placeholder support (`{{content_image}}`)
- Cover images and excerpts

#### Tags (Series)
- Categorization system for posts
- Supports series/collections
- Alphabetically sorted

#### Comments
- Nested comment system
- Supports replies and reactions
- Tracks likes and author information

#### Newsletter Subscriptions
- Email subscription management
- Email preferences (post notifications)
- Unsubscribe tokens

#### Verification Codes
- Email verification code storage
- Rate limiting and expiration
- Attempt tracking

#### Sent Emails
- Email delivery tracking
- Prevents duplicate sends

## 🔌 API Endpoints

### Authentication (`/api/v1/auth`)
- `POST /login` - User login with email/password (JWT)

### Users (`/api/v1/users`)
- `POST /send-code` - Send verification code to email
- `POST /verify-code` - Verify code and auto-register/login
- `POST /` - Register new user (admin/programmatic)
- `GET /{email}` - Get user by email
- `PUT /{email}` - Update user (name, email)
- `PUT /{email}/password` - Change password
- `POST /{email}/profile-image` - Upload profile image (S3)
- `GET /{email}/profile-image` - Get profile image
- `DELETE /{email}/profile-image` - Delete profile image

### Posts (`/api/v1/posts`)
- `GET /posts` - List posts (pagination, filtering, sorting, exclude)
  - Query params: `page`, `limit`, `tag`, `author`, `sort` (latest/top/discussions), `exclude`
- `GET /posts/{slug}` - Get post by slug
- `GET /posts/{slug}/admin` - Get post for editing (admin)
- `GET /posts/drafts` - Get draft posts (admin)
- `POST /posts` - Create new post (admin)
- `PUT /posts/{slug}` - Update post (admin)
- `DELETE /posts/{slug}` - Delete post (admin)
- `POST /posts/{slug}/publish` - Publish draft (admin)
- `POST /posts/{slug}/views` - Increment view count
- `POST /posts/{slug}/like` - Like post
- `DELETE /posts/{slug}/like` - Unlike post

### Authors (`/api/v1/authors`)
- `GET /authors` - List all authors
- `GET /authors/{idOrUsername}` - Get author by ID or username
- `POST /authors` - Create author (admin)
- `PUT /authors/{username}` - Update author (admin)
- `DELETE /authors/{username}` - Delete author (admin)

### Tags (`/api/v1/tags`)
- `GET /tags` - List all tags (alphabetically sorted)
- `GET /tags/{slug}` - Get tag by slug
- `POST /tags` - Create tag (admin)
- `PUT /tags/{slug}` - Update tag (admin)
- `DELETE /tags/{slug}` - Delete tag (admin)

### Comments (`/api/v1/comments`)
- `GET /comments?postSlug={slug}` - Get comments for post
- `POST /comments` - Create comment
- `PUT /comments/{commentId}` - Update comment (author only)
- `DELETE /comments/{commentId}` - Delete comment (author only)
- `POST /comments/{commentId}/like` - Like comment

### Newsletter (`/api/v1/newsletter`)
- `POST /subscribe` - Subscribe to newsletter
- `POST /unsubscribe` - Unsubscribe from newsletter
- `GET /subscription/{email}` - Get subscription status
- `PUT /subscription/{email}` - Update email preferences

### Search (`/api/v1/search`)
- `GET /search?q={query}` - Full-text search across posts

### API Info (`/api/v1`)
- `GET /health` - Health check endpoint
- `GET /` - API information

## 🚀 Getting Started

### Prerequisites

- **Node.js** 23+ and npm
- **Java** 21+
- **Maven** 3.8+
- **PostgreSQL** 17.4+
- **Docker** (optional, for containerized development)
- **AWS Account** (for S3 and SES in production)

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend will be available at `http://localhost:5173`

**Environment Variables** (create `frontend/.env` file):
```env
VITE_API_BASE_URL=http://localhost:8080
```

### Backend Setup

1. **Start PostgreSQL**:
```bash
# Using Docker Compose
cd backend
docker-compose up -d

# Or manually
createdb blog
```

2. **Configure Database** (update `application.yaml` or use environment variables):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/blog
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
```

3. **Configure AWS Services** (for production):
```yaml
aws:
  s3:
    bucket-name: ${AWS_S3_BUCKET_NAME}
    region: ${AWS_REGION:us-east-1}
  ses:
    region: ${AWS_REGION:us-east-1}
  credentials:
    access-key-id: ${AWS_ACCESS_KEY_ID}
    secret-access-key: ${AWS_SECRET_ACCESS_KEY}
```

**Note**: For local development, S3 and SES are optional. The app will work without them (profile images won't upload).

4. **Run Migrations** (automatic on startup):
   - Flyway will automatically run migrations from `src/main/resources/db/migration/`
   - V1: Creates all tables
   - V2: Legacy mock seed data (kept for migration history compatibility)
   - V3: Resets to professional baseline (clears content, seeds admin user and topic tags)

5. **Start Backend**:
```bash
cd backend
./mvnw spring-boot:run
```

Backend API will be available at `http://localhost:8080`

### Docker Development

```bash
# Start all services (frontend, backend, database)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## 🧪 Testing

### Backend Tests

```bash
cd backend

# Run all tests
./mvnw test

# Run integration tests
./mvnw verify

# Run with coverage
./mvnw test jacoco:report
```

**Test Structure**:
- Unit tests: `*Test.java`
- Integration tests: `*IT.java` or `*IntegrationTest.java`
- Uses Testcontainers for database testing
- Tests cover: Services, Repositories, RowMappers, Controllers

### Frontend Tests

```bash
cd frontend
npm test
```

## 🔐 Security

### Authentication Flow

1. **Email Verification Flow** (Primary):
   - User requests verification code via `POST /api/v1/users/send-code`
   - Code is generated (6 digits), hashed, and stored with expiration (10 minutes)
   - Code sent via AWS SES with Thymeleaf email template
   - User submits code via `POST /api/v1/users/verify-code`
   - If valid, user is auto-registered (if new) and JWT token is issued
   - Token includes roles: `ROLE_USER` and optionally `ROLE_AUTHOR`

2. **Password Login Flow** (Alternative):
   - User logs in via `POST /api/v1/auth/login` with email/password
   - JWT token is issued upon successful authentication

3. **Token Usage**:
   - Token is included in `Authorization: Bearer {token}` header
   - Token contains user email and roles
   - Backend validates token on protected endpoints

### Security Features

- **JWT-based authentication** with role-based access control
- **Password hashing** with BCrypt (10 rounds)
- **CORS configuration** for frontend domain
- **SQL injection prevention** (parameterized queries)
- **XSS protection** (input sanitization)
- **Global exception handling** with proper error messages
- **Rate limiting** on verification codes (3 codes per hour)
- **Code expiration** (10 minutes)
- **Attempt tracking** (max 5 attempts per code)
- **Email verification** required for registration

## 📧 Email System

### Email Templates

Email templates are built using **Thymeleaf** and styled to match the frontend:

1. **Verification Email** (`verification.html`):
   - Sent when user requests verification code
   - Includes 6-digit code and expiration time
   - Branded with blog colors and logo

2. **Post Notification Email** (`post-notification.html`):
   - Sent to newsletter subscribers when new post is published
   - Includes post title, excerpt, and link
   - Includes unsubscribe link

### Email Service

- **AWS SES** integration for sending emails
- **Thymeleaf** template engine for HTML emails
- **Email tracking** via `sent_emails` table
- **Duplicate prevention** to avoid sending same email twice

## 🖼️ Image Management

### Profile Images

- **Storage**: AWS S3 bucket
- **Upload**: `POST /api/v1/users/{email}/profile-image`
- **Retrieve**: `GET /api/v1/users/{email}/profile-image`
- **Delete**: `DELETE /api/v1/users/{email}/profile-image`
- **Format**: JPEG, stored with S3 key in database

### Content Images

- **Placeholder System**: Use `{{content_image}}` in markdown content
- **Backend Replacement**: Automatically replaced with markdown image syntax
- **URL Storage**: Content image URL stored in `posts.content_image` column
- **Display**: Rendered at placeholder location in markdown

## 📦 Deployment

### AWS Elastic Beanstalk Deployment

The application is configured for deployment on AWS Elastic Beanstalk using Docker.

#### Backend Deployment

**Docker Build** (using Jib):
```bash
cd backend
./mvnw clean package
# Jib automatically builds and pushes to Docker Hub
```

**Dockerrun.aws.json** (root level):
```json
{
  "AWSEBDockerrunVersion": 2,
  "containerDefinitions": [
    {
      "name": "blog-api",
      "image": "iabdinur/blog-api:latest",
      "essential": true,
      "memory": 512,
      "portMappings": [
        {
          "hostPort": 80,
          "containerPort": 8080
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ]
    }
  ]
}
```

**Production Environment Variables** (set in Elastic Beanstalk):
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://your-rds-endpoint:5432/blog
SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
AWS_REGION=us-east-1
AWS_S3_BUCKET_NAME=${S3_BUCKET_NAME}
SPRING_PROFILES_ACTIVE=prod
```

#### Frontend Deployment

**Docker Build**:
```bash
cd frontend
docker build --build-arg api_base_url=https://api.yourdomain.com -t blog-frontend:latest .
```

**Dockerrun.aws.json** (frontend directory):
```json
{
  "AWSEBDockerrunVersion": 2,
  "containerDefinitions": [
    {
      "name": "blog-frontend",
      "image": "iabdinur/blog-frontend:latest",
      "essential": true,
      "memory": 256,
      "portMappings": [
        {
          "hostPort": 80,
          "containerPort": 80
        }
      ]
    }
  ]
}
```

**Production Environment Variables**:
- `API_BASE_URL`: Set as build argument during Docker build

### CI/CD Pipeline

#### GitHub Actions Workflows

**Backend CI** (`.github/workflows/backend-ci.yml`):
- Runs on pull requests
- Executes unit and integration tests
- Uses PostgreSQL service container
- Validates code quality

**Backend CD** (`.github/workflows/backend-cd.yml`):
- Triggers on push to `main` branch
- Builds Docker image with Maven Jib plugin
- Pushes to Docker Hub (`iabdinur/blog-api:latest`)
- Deploys to AWS Elastic Beanstalk
- Updates `Dockerrun.aws.json` with latest image

**Frontend CD** (`.github/workflows/frontend-react-cd.yml`):
- Triggers on push to `main` branch
- Builds React application with production API URL
- Creates Docker image
- Pushes to Docker Hub (`iabdinur/blog-frontend:latest`)
- Deploys to AWS Elastic Beanstalk

#### Required GitHub Secrets

- `DOCKERHUB_USERNAME`: Docker Hub username
- `DOCKERHUB_ACCESS_TOKEN`: Docker Hub access token
- `AWS_ACCESS_KEY_ID`: AWS access key
- `AWS_SECRET_ACCESS_KEY`: AWS secret key
- `EB_APPLICATION_NAME`: Elastic Beanstalk application name
- `EB_ENVIRONMENT_NAME`: Elastic Beanstalk environment name
- `EB_REGION`: AWS region
- `API_BASE_URL`: Production API base URL (for frontend build)

## 🏗️ Technical Architecture Details

### Backend Architecture

#### Layered Architecture
```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
DAO Layer (Data Access Interface)
    ↓
Repository Layer (JDBC Implementation)
    ↓
Database (PostgreSQL)
```

#### Key Design Patterns
- **Repository Pattern**: Abstract data access
- **DAO Pattern**: Data Access Object interfaces
- **DTO Pattern**: Separate API contracts from domain models
- **RowMapper Pattern**: Map ResultSet to domain objects
- **Service Layer**: Encapsulate business logic
- **Dependency Injection**: Spring IoC container

#### Database Access Strategy
- **JDBC Template**: Direct SQL queries for performance
- **Custom RowMappers**: Type-safe result mapping
- **Connection Pooling**: HikariCP for efficient connections
- **Transaction Management**: `@Transactional` annotations
- **Flyway Migrations**: Version-controlled schema changes

### Frontend Architecture

#### Component Structure
- **Pages**: Route-level components
- **Components**: Reusable UI components
- **Hooks**: Custom React hooks for logic reuse
- **Store**: Global state management (Zustand)
- **API**: Centralized API client with interceptors

#### State Management
- **Zustand**: Global state (auth, UI, reading list, newsletter)
- **React Query**: Server state and caching
- **Local State**: Component-level useState/useReducer

#### Routing
- **React Router DOM**: Client-side routing
- **Protected Routes**: Author admin pages require authentication
- **Public Routes**: Blog pages accessible to all

## 📊 Performance Optimizations

### Backend
- Connection pooling (HikariCP)
- Database indexing on frequently queried columns
- Pagination for large datasets
- Efficient SQL queries with proper joins
- Query result caching (future: Redis)

### Frontend
- Code splitting with React.lazy()
- Image optimization
- API response caching (React Query)
- Debounced search inputs
- Optimistic updates for likes/comments

## 🔄 Content Image System

### How It Works

1. **Author adds content image URL** in post form
2. **Author places placeholder** `{{content_image}}` in markdown content where image should appear
3. **Backend replaces placeholder** with markdown image syntax: `![Content Image](url)`
4. **Frontend renders** markdown with image at correct location

### Example

**Markdown Content**:
```markdown
# Introduction

This is the beginning of the article.

{{content_image}}

Now we continue with more content...
```

**After Processing**:
```markdown
# Introduction

This is the beginning of the article.

![Content Image](https://example.com/image.jpg)

Now we continue with more content...
```

## 📈 Scalability Considerations

### Current Capacity (Designed for 10k readers)
- **API Throughput**: 200 req/sec
- **Email Processing**: 100 emails/sec
- **Database**: PostgreSQL with connection pooling
- **Storage**: AWS S3 for images
- **Email**: AWS SES for transactional emails

### Future Scaling Path
1. **10k-50k**: Add read replicas, Redis cluster for caching
2. **50k-100k**: Implement message queue (SQS/Kafka) for async tasks
3. **100k+**: Microservices architecture, Kubernetes, CDN for static assets

## 🛠️ Development Tools

- **Backend**: IntelliJ IDEA / VS Code with Java extensions
- **Frontend**: VS Code with React/TypeScript extensions
- **Database**: pgAdmin / DBeaver / psql
- **API Testing**: Postman / Thunder Client / curl
- **Version Control**: Git + GitHub
- **Container Management**: Docker + Docker Compose

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Built with Spring Boot 3.4.2, React 18, TypeScript, PostgreSQL, AWS S3, and AWS SES**
