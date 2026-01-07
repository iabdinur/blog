# Blog Platform

A full-stack blog platform combining features from Hashnode and Substack.

## Tech Stack

### Frontend
- React 18.3.1
- TypeScript 5.8.3
- Vite 7.0.4
- Chakra UI 2.10.9
- Framer Motion 10.18.0
- React Router DOM 7.6.3
- Zustand 5.0.6
- TanStack React Query 5.83.0
- React Hook Form 7.60.0
- Zod 4.0.5
- Axios 1.10.0

### Backend (Phase 2)
- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL 15+
- ModelMapper 3.1.1
- Spring Boot Validation
- Maven/Gradle

## Getting Started

### Frontend Development

```bash
cd frontend
npm install
npm run dev
```

### Backend Development (Phase 2)

```bash
cd backend
mvn spring-boot:run
# or
./gradlew bootRun
```

## Project Structure

- `frontend/` - Frontend React application
- `backend/` - Backend API (Phase 2)
- `shared/` - Shared types and utilities

## Phase 1

Currently focusing on frontend-only development. All builds and configurations are in the `frontend/` package.

## Phase 2

Backend API development with the following features:
- RESTful API with Spring Boot
- PostgreSQL database with JPA/Hibernate
- Entity relationships (Posts, Authors, Tags, Comments)
- Pagination and filtering support
- Full-text search functionality
- Sorting (Latest, Top, Discussions)
- View and like tracking
- Comment system with nested replies

