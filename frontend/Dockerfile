# Multi-stage build for React application
FROM node:20-alpine AS build

# Accept build argument for API base URL
ARG api_base_url

WORKDIR /app

# Copy package files
COPY package*.json .

# Install dependencies
RUN npm i --silent

# Copy source code
COPY . .

# Create .env file with API base URL (embedded in build at compile time)
RUN echo "VITE_API_BASE_URL=${api_base_url}" > .env

# Build application
RUN npm run build

# Production stage with Nginx
FROM nginx:alpine

# Copy built files
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
