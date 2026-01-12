# Blog Platform

A full-stack blog platform combining features from Hashnode and Substack, built with modern technologies and designed to scale to 10,000+ readers.

## 🏗️ Architecture Overview

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend Layer                        │
│  React 18 + TypeScript + Vite + Chakra UI                   │
│  - Server-Side Rendering (SSR)                              │
│  - Client-Side State Management (Zustand)                    │
│  - API Integration (Axios + React Query)                     │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/REST API
┌──────────────────────▼──────────────────────────────────────┐
│                      API Gateway Layer                      │
│  Spring Boot 3.4.1 + Java 21                               │
│  - RESTful API (v1)                                        │
│  - JWT Authentication                                       │
│  - CORS Configuration                                       │
│  - Global Exception Handling                               │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
┌───────▼──────┐ ┌─────▼─────┐ ┌─────▼─────┐
│  PostgreSQL  │ │   Redis   │ │   Email   │
│  (Primary)   │ │  (Cache)  │ │  Services │
│              │ │            │ │           │
│  - Authors   │ │  - Session │ │  - SES   │
│  - Posts     │ │  - Cache   │ │  - SendGrid│
│  - Tags      │ │            │ │           │
│  - Comments  │ │            │ │           │
│  - Accounts  │ │            │ │           │
└──────────────┘ └────────────┘ └───────────┘
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

#### Backend (Spring Boot + Java)
- **Framework**: Spring Boot 3.4.1
- **Language**: Java 21
- **Database**: PostgreSQL 17.4
- **ORM**: Spring JDBC (Custom RowMappers)
- **Migrations**: Flyway
- **Security**: Spring Security + JWT
- **Validation**: Spring Boot Validation
- **Testing**: JUnit 5 + Mockito + Testcontainers

## 📁 Project Structure

```
blog/
├── frontend/                 # React frontend application
│   ├── src/
│   │   ├── api/            # API client and endpoints
│   │   ├── components/     # React components
│   │   │   ├── blog/       # Blog-specific components
│   │   │   ├── forms/      # Form components
│   │   │   ├── layout/     # Layout components
│   │   │   ├── newsletter/ # Newsletter components
│   │   │   └── ui/         # Reusable UI components
│   │   ├── hooks/          # Custom React hooks
│   │   ├── lib/            # Utility libraries
│   │   ├── pages/          # Page components
│   │   ├── store/          # Zustand stores
│   │   ├── types/          # TypeScript types
│   │   └── utils/          # Utility functions
│   ├── public/             # Static assets
│   ├── package.json
│   └── vite.config.ts
│
├── backend/                # Spring Boot backend API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/iabdinur/
│   │   │   │   ├── config/      # Configuration classes
│   │   │   │   ├── controller/ # REST controllers
│   │   │   │   ├── dao/         # Data Access Objects
│   │   │   │   ├── dto/         # Data Transfer Objects
│   │   │   │   ├── exception/   # Exception handlers
│   │   │   │   ├── mapper/      # DTO mappers
│   │   │   │   ├── model/        # Domain models
│   │   │   │   ├── repository/  # JDBC repositories
│   │   │   │   ├── rowmapper/   # ResultSet mappers
│   │   │   │   ├── service/     # Business logic
│   │   │   │   └── util/        # Utilities
│   │   │   └── resources/
│   │   │       ├── db/migration/ # Flyway migrations
│   │   │       └── application.yaml
│   │   └── test/            # Test classes
│   ├── pom.xml
│   └── docker-compose.yml
│
└── README.md
```

## 🗄️ Database Schema

### Core Tables

#### Authors
- User profiles with social links, bio, avatar
- Tracks followers, following, and post counts

#### Posts
- Blog posts with markdown content
- Supports drafts, scheduling, and publishing
- Tracks views, likes, and comments

#### Tags (Series)
- Categorization system for posts
- Supports series/collections

#### Comments
- Nested comment system
- Supports replies and reactions

#### Accounts
- User authentication and authorization
- Email verification support

#### Post Tags (Junction Table)
- Many-to-many relationship between posts and tags

## 🔌 API Endpoints

### Authentication (`/api/v1/auth`)
- `POST /login` - User login with JWT
- `POST /send-code` - Send verification code
- `POST /verify-code` - Verify code and login

### Posts (`/api/v1/posts`)
- `GET /posts` - List posts with pagination, filtering, sorting
- `GET /posts/{id}` - Get post details
- `POST /posts` - Create new post (admin)
- `PUT /posts/{id}` - Update post (admin)
- `DELETE /posts/{id}` - Delete post (admin)

### Authors (`/api/v1/authors`)
- `GET /authors` - List authors
- `GET /authors/{id}` - Get author details
- `POST /authors` - Create author (admin)
- `PUT /authors/{id}` - Update author (admin)

### Tags (`/api/v1/tags`)
- `GET /tags` - List tags
- `GET /tags/{slug}` - Get tag details
- `POST /tags` - Create tag (admin)
- `PUT /tags/{id}` - Update tag (admin)

### Comments (`/api/v1/comments`)
- `GET /comments/post/{postId}` - Get comments for post
- `POST /comments` - Create comment
- `PUT /comments/{id}` - Update comment
- `DELETE /comments/{id}` - Delete comment

### Search (`/api/v1/search`)
- `GET /search?q={query}` - Full-text search

## 🚀 Getting Started

### Prerequisites

- **Node.js** 18+ and npm
- **Java** 21+
- **Maven** 3.8+
- **PostgreSQL** 17.4+
- **Docker** (optional, for containerized development)

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend will be available at `http://localhost:5173`

**Environment Variables** (create `.env` file):
```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
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
    password: ${DB_PASSWORD}
```

**Note**: Use environment variables for sensitive credentials. Create a `.env` file or set environment variables:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

3. **Run Migrations** (automatic on startup):
   - Flyway will automatically run migrations from `src/main/resources/db/migration/`

4. **Start Backend**:
```bash
cd backend
./mvnw spring-boot:run
```

Backend API will be available at `http://localhost:8080`

### Docker Development

```bash
# Start all services
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

### Frontend Tests

```bash
cd frontend
npm test
```

## 🔐 Security

### Authentication Flow
1. User requests verification code via `/api/v1/accounts/send-code`
2. Code is generated and stored (in-memory, production: Redis)
3. User submits code via `/api/v1/accounts/verify-code`
4. JWT token is issued upon successful verification
5. Token is included in `Authorization: Bearer {token}` header

### Security Features
- JWT-based authentication
- Password hashing with BCrypt
- CORS configuration
- SQL injection prevention (parameterized queries)
- XSS protection
- Global exception handling

## 📦 Deployment

### Backend Deployment

**Docker Build**:
```bash
cd backend
./mvnw clean package
docker build -t blog-api:latest .
```

**Production Environment Variables**:
```env
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
AWS_ACCESS_KEY=${AWS_ACCESS_KEY}
AWS_SECRET_KEY=${AWS_SECRET_KEY}
AWS_REGION=${AWS_REGION}
SENDGRID_API_KEY=${SENDGRID_API_KEY}
```

**Note**: All sensitive values should be stored in environment variables or secrets management (AWS Secrets Manager, GitHub Secrets, etc.)

### Frontend Deployment

**Build**:
```bash
cd frontend
npm run build
```

**Deploy to Vercel/Netlify**:
- Connect GitHub repository
- Set build command: `npm run build`
- Set output directory: `dist`
- Add environment variable: `VITE_API_BASE_URL`

## 🏗️ Technical Architecture Details

### Backend Architecture

#### Layered Architecture
```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
DAO Layer (Data Access)
    ↓
Repository Layer (JDBC)
    ↓
Database (PostgreSQL)
```

#### Key Design Patterns
- **Repository Pattern**: Abstract data access
- **DTO Pattern**: Separate API contracts from domain models
- **RowMapper Pattern**: Map ResultSet to domain objects
- **Service Layer**: Encapsulate business logic

#### Database Access Strategy
- **JDBC Template**: Direct SQL queries for performance
- **Custom RowMappers**: Type-safe result mapping
- **Connection Pooling**: HikariCP for efficient connections
- **Transaction Management**: `@Transactional` annotations

### Frontend Architecture

#### Component Structure
- **Pages**: Route-level components
- **Components**: Reusable UI components
- **Hooks**: Custom React hooks for logic reuse
- **Store**: Global state management (Zustand)
- **API**: Centralized API client with interceptors

#### State Management
- **Zustand**: Global state (auth, UI, reading list)
- **React Query**: Server state and caching
- **Local State**: Component-level useState/useReducer

## 📊 Performance Optimizations

### Backend
- Connection pooling (HikariCP)
- Database indexing on frequently queried columns
- Pagination for large datasets
- Efficient SQL queries with proper joins

### Frontend
- Code splitting with React.lazy()
- Image optimization
- API response caching (React Query)
- Debounced search inputs

## 🔄 CI/CD Pipeline

### GitHub Actions Workflows

**Backend CI** (`.github/workflows/backend-ci.yml`):
- Runs on pull requests
- Executes unit and integration tests
- Uses PostgreSQL service container

**Backend CD** (`.github/workflows/backend-cd.yml`):
- Builds Docker image with Jib
- Pushes to Docker Hub
- Deploys to AWS Elastic Beanstalk
- Updates Dockerrun.aws.json

**Frontend CD** (`.github/workflows/frontend-react-cd.yml`):
- Builds React application
- Creates production Docker image
- Deploys to AWS Elastic Beanstalk

## 📈 Scalability Considerations

### Current Capacity (Designed for 10k readers)
- **API Throughput**: 200 req/sec
- **Email Processing**: 100 emails/sec
- **Database**: PostgreSQL with connection pooling
- **Caching**: Redis for session and content caching

### Future Scaling Path
1. **10k-50k**: Add read replicas, Redis cluster
2. **50k-100k**: Implement message queue (SQS/Kafka)
3. **100k+**: Microservices, Kubernetes, CDN

## 🛠️ Development Tools

- **Backend**: IntelliJ IDEA / VS Code
- **Frontend**: VS Code with React extensions
- **Database**: pgAdmin / DBeaver
- **API Testing**: Postman / Thunder Client
- **Version Control**: Git + GitHub

## 📝 License

[Add your license here]

## 🤝 Contributing

[Add contribution guidelines]

## 📧 Contact

[Add contact information]

---

**Built with ❤️ using Spring Boot, React, and PostgreSQL**
