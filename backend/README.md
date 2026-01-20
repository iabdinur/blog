# Blog Backend API

Spring Boot REST API for the blog platform, providing endpoints for posts, authors, comments, users, newsletter, and search functionality.

## 🚀 Quick Start

### Prerequisites

- **Java** 21+
- **Maven** 3.8+
- **PostgreSQL** 17.4+
- **Docker** (optional, for containerized development)

### Local Development Setup

1. **Start PostgreSQL**:
```bash
# Using Docker Compose
docker-compose up -d

# Or manually
createdb blog
```

2. **Configure Database**:
Update `src/main/resources/application.yaml` or set environment variables:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/blog
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
```

3. **Run Migrations**:
Flyway will automatically run migrations on startup from `src/main/resources/db/migration/`:
- `V1__Initial_schema.sql` - Creates all database tables
- `V2__Seed_mock_data.sql` - Seeds initial data

4. **Start Application**:
```bash
./mvnw spring-boot:run
```

API will be available at `http://localhost:8080`

## 📦 Dependencies

### Core Dependencies
- **Spring Boot** 3.4.2
- **Spring Web** - REST API
- **Spring Security** - Authentication & Authorization
- **Spring JDBC** - Database access
- **PostgreSQL Driver** - Database connectivity
- **Flyway** 9.22.2 - Database migrations
- **JWT** (JJWT) 0.12.6 - Token-based authentication
- **Thymeleaf** - Email templates
- **AWS SDK** 2.41.10 - S3 and SES integration

### Testing Dependencies
- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework
- **Testcontainers** 1.21.4 - Integration testing with Docker
- **Java Faker** 1.0.2 - Test data generation

## 🏗️ Architecture

### Package Structure

```
com.iabdinur/
├── config/          # Configuration classes
│   ├── CorsConfig.java
│   ├── DataAccessConfig.java
│   ├── RateLimitingConfig.java
│   ├── S3Config.java
│   └── SecurityConfig.java
├── controller/      # REST controllers
│   ├── ApiInfoController.java
│   ├── AuthenticationController.java
│   ├── AuthorController.java
│   ├── CommentController.java
│   ├── NewsletterController.java
│   ├── PostController.java
│   ├── SearchController.java
│   ├── TagController.java
│   └── UserController.java
├── dao/            # Data Access Object interfaces
├── dto/            # Data Transfer Objects (23 files)
├── exception/      # Exception handlers
├── mapper/         # DTO mappers
├── model/          # Domain models
├── repository/     # JDBC repository implementations
├── rowmapper/      # ResultSet mappers
├── service/        # Business logic services
├── s3/             # S3 utilities
└── util/           # Utility classes
```

### Layered Architecture

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

## 🔌 API Endpoints

### Base URL
`http://localhost:8080/api/v1`

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
- `GET /posts` - List posts (pagination, filtering, sorting)
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

## 🗄️ Database

### Database Schema

The application uses PostgreSQL with the following main tables:
- `users` - User accounts and authentication
- `authors` - Author profiles
- `posts` - Blog posts
- `tags` - Post tags/series
- `comments` - Post comments
- `newsletter_subscriptions` - Newsletter subscribers
- `verification_codes` - Email verification codes
- `sent_emails` - Email delivery tracking
- `post_tags` - Many-to-many relationship between posts and tags

### Migrations

Database migrations are managed by Flyway:
- Migrations are located in `src/main/resources/db/migration/`
- Migrations run automatically on application startup
- Migration files follow naming: `V{version}__{description}.sql`

## 🔐 Security

### Authentication

1. **Email Verification Flow** (Primary):
   - User requests code via `POST /api/v1/users/send-code`
   - 6-digit code generated, hashed, and stored (10 min expiration)
   - Code sent via AWS SES
   - User verifies via `POST /api/v1/users/verify-code`
   - JWT token issued upon successful verification

2. **Password Login Flow**:
   - User logs in via `POST /api/v1/auth/login`
   - JWT token issued upon successful authentication

### JWT Tokens

- Tokens contain user email and roles (`ROLE_USER`, `ROLE_AUTHOR`)
- Tokens are included in `Authorization: Bearer {token}` header
- Token validation handled by Spring Security

### Security Features

- **Password Hashing**: BCrypt (10 rounds)
- **SQL Injection Prevention**: Parameterized queries
- **CORS Configuration**: Configured for frontend domain
- **Rate Limiting**: 3 verification codes per hour
- **Code Expiration**: 10 minutes
- **Attempt Tracking**: Max 5 attempts per code

## 📧 Email System

### Email Templates

Email templates use **Thymeleaf** and are located in `src/main/resources/templates/email/`:

1. **verification.html** - Verification code email
2. **post-notification.html** - New post notification email

### AWS SES Integration

- Email sending via AWS SES
- Templates styled to match frontend branding
- Email tracking via `sent_emails` table
- Duplicate prevention

### Configuration

```yaml
app:
  email:
    enabled: ${EMAIL_ENABLED:false}
    from: ${EMAIL_FROM:noreply@iabdinur.com}
```

## 🖼️ Image Management

### Profile Images (AWS S3)

- **Upload**: `POST /api/v1/users/{email}/profile-image`
- **Retrieve**: `GET /api/v1/users/{email}/profile-image`
- **Delete**: `DELETE /api/v1/users/{email}/profile-image`
- Images stored in S3, S3 key stored in database

### Content Images

- Content images use placeholder system: `{{content_image}}`
- Backend replaces placeholder with markdown image syntax
- Image URL stored in `posts.content_image` column

### AWS S3 Configuration

```yaml
aws:
  access-key-id: ${AWS_ACCESS_KEY_ID:}
  secret-access-key: ${AWS_SECRET_ACCESS_KEY:}
  region: ${AWS_REGION:us-east-1}
  s3:
    buckets:
      users: ${S3_BUCKET:users}
```

## ⚙️ Configuration

### Application Properties

Main configuration file: `src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: blog-api
  datasource:
    url: jdbc:postgresql://localhost:5432/blog
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080
```

### Environment Variables

- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `AWS_ACCESS_KEY_ID` - AWS access key
- `AWS_SECRET_ACCESS_KEY` - AWS secret key
- `AWS_REGION` - AWS region (default: us-east-1)
- `S3_BUCKET` - S3 bucket name
- `EMAIL_ENABLED` - Enable email sending (default: false)
- `EMAIL_FROM` - Email sender address

### Production Profile

Production configuration: `src/main/resources/application-prod.yaml`

## 🧪 Testing

### Run Tests

```bash
# Run all tests
./mvnw test

# Run integration tests
./mvnw verify

# Run with coverage
./mvnw test jacoco:report
```

### Test Structure

- **Unit Tests**: `*Test.java` - Test individual components
- **Integration Tests**: `*IT.java` - Test with real database (Testcontainers)
- **Test Data**: Uses Java Faker for generating test data

### Testcontainers

Integration tests use Testcontainers to spin up PostgreSQL containers:
- Automatic container lifecycle management
- Isolated test environments
- No need for external database setup

## 🐳 Docker

### Build Docker Image

```bash
# Build using Dockerfile
docker build -t blog-api:latest .

# Build using Maven Jib (recommended)
./mvnw clean package
# Jib automatically builds and pushes to Docker Hub
```

### Docker Compose

Local development with Docker Compose:
```bash
docker-compose up -d
```

Includes:
- PostgreSQL database
- Automatic migrations
- Health checks

### Dockerfile

Multi-stage build:
1. **Build stage**: Compiles application
2. **Runtime stage**: Runs JAR with minimal JRE

## 📊 Performance

### Optimizations

- **Connection Pooling**: HikariCP
- **Database Indexing**: Indexes on frequently queried columns
- **Pagination**: All list endpoints support pagination
- **Efficient Queries**: Optimized SQL with proper joins
- **Transaction Management**: `@Transactional` annotations

## 🚀 Deployment

### AWS Elastic Beanstalk

1. **Build Docker Image**:
```bash
./mvnw clean package
# Jib builds and pushes to Docker Hub
```

2. **Deploy**:
- GitHub Actions workflow handles deployment
- Updates `Dockerrun.aws.json` with latest image
- Deploys to Elastic Beanstalk environment

### Production Environment Variables

Set in Elastic Beanstalk:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION`
- `S3_BUCKET`
- `SPRING_PROFILES_ACTIVE=prod`

## 📝 Development

### Code Style

- Follow Java naming conventions
- Use Spring Boot best practices
- Document public APIs with Javadoc
- Keep methods focused and single-purpose

### Adding New Features

1. Create domain model in `model/` package
2. Create DTOs in `dto/` package
3. Create DAO interface in `dao/` package
4. Implement repository in `repository/` package
5. Create row mapper in `rowmapper/` package
6. Implement service logic in `service/` package
7. Create controller in `controller/` package
8. Add database migration if needed
9. Write tests

## 🔍 Troubleshooting

### Common Issues

1. **Database Connection Failed**:
   - Check PostgreSQL is running
   - Verify connection string in `application.yaml`
   - Check credentials

2. **Migrations Failed**:
   - Check Flyway logs
   - Verify SQL syntax in migration files
   - Check database permissions

3. **S3 Upload Failed**:
   - Verify AWS credentials are set
   - Check S3 bucket exists and has correct permissions
   - Verify region configuration

4. **Email Not Sending**:
   - Check `EMAIL_ENABLED` is set to `true`
   - Verify AWS SES credentials
   - Check SES sandbox mode (if applicable)

## 📚 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/)

---

**Built with Spring Boot 3.4.2 and Java 21**
