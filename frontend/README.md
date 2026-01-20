# Blog Frontend

React + TypeScript frontend application for the blog platform, built with Vite, Chakra UI, and modern React patterns.

## 🚀 Quick Start

### Prerequisites

- **Node.js** 20+
- **npm** or **yarn**

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

Frontend will be available at `http://localhost:5173`

### Environment Variables

Create a `.env` file in the `frontend/` directory:

```env
VITE_API_BASE_URL=http://localhost:8080
```

**Note**: The API base URL should NOT include `/api/v1` - it's automatically appended.

## 📦 Dependencies

### Core Dependencies

- **React** 18.3.1 - UI library
- **TypeScript** 5.8.3 - Type safety
- **Vite** 7.0.4 - Build tool and dev server
- **Chakra UI** 2.10.9 - Component library
- **React Router DOM** 7.6.3 - Client-side routing
- **TanStack React Query** 5.83.0 - Server state management
- **Zustand** 5.0.6 - Global state management
- **Axios** 1.10.0 - HTTP client
- **React Markdown** 9.0.1 - Markdown rendering
- **React Hook Form** 7.60.0 - Form handling
- **Zod** 4.0.5 - Schema validation

### Development Dependencies

- **TypeScript** - Type checking
- **ESLint** - Code linting
- **Vite React Plugin** - Vite integration

## 🏗️ Project Structure

```
frontend/
├── src/
│   ├── api/              # API client and endpoints
│   │   ├── admin.ts      # Admin/author endpoints
│   │   ├── authors.ts    # Author endpoints
│   │   ├── client.ts     # Axios client configuration
│   │   ├── comments.ts   # Comment endpoints
│   │   ├── newsletter.ts # Newsletter endpoints
│   │   ├── posts.ts      # Post endpoints
│   │   ├── reactions.ts  # Reaction endpoints
│   │   ├── search.ts     # Search endpoints
│   │   └── tags.ts       # Tag endpoints
│   ├── components/       # React components
│   │   ├── blog/        # Blog-specific components
│   │   ├── forms/       # Form components
│   │   ├── layout/      # Layout components
│   │   ├── newsletter/  # Newsletter components
│   │   └── ui/          # Reusable UI components
│   ├── hooks/           # Custom React hooks
│   ├── lib/             # Utility libraries
│   ├── pages/           # Page components
│   │   └── author/      # Author admin pages
│   ├── store/           # Zustand stores
│   ├── types/           # TypeScript types
│   ├── utils/           # Utility functions
│   ├── compositions/    # Compositions
│   ├── theme/           # Theme configuration
│   ├── App.tsx          # Main app component
│   └── main.tsx         # Entry point
├── public/              # Static assets
├── Dockerfile           # Docker configuration
├── Dockerrun.aws.json   # AWS Elastic Beanstalk config
├── nginx.conf           # Nginx configuration
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 🎨 Features

### User Features

- **Authentication**: Email verification and password login
- **User Profile**: View and edit profile, upload profile image
- **Reading List**: Save posts for later reading
- **Comments**: View, create, edit, and delete comments
- **Reactions**: Like posts and comments
- **Newsletter**: Subscribe/unsubscribe with email preferences
- **Search**: Full-text search across posts
- **Dark Mode**: Toggle between light and dark themes

### Author Features

- **Post Management**: Create, edit, delete, and publish posts
- **Draft System**: Save posts as drafts
- **Content Images**: Add images within post content using placeholders
- **Tag Management**: Create and manage tags/series
- **Author Profile**: Customize author bio, avatar, and social links
- **Post Scheduling**: Schedule posts for future publication

### Content Features

- **Markdown Support**: Rich markdown content with syntax highlighting
- **Content Images**: Dynamic image placement within articles
- **Table of Contents**: Auto-generated from headings
- **Reading Progress**: Visual reading progress indicator
- **Related Posts**: Show related articles based on tags
- **Post Series**: Organize posts into series/tags
- **Archive**: Browse all posts with filtering

## 🛣️ Routing

### Public Routes

- `/` - Home page with featured post and latest posts
- `/archive` - Archive page with all posts
- `/post/:slug` - Individual post page
- `/author` - Author profile page
- `/series/:slug` - Series/tag page
- `/search` - Search results page
- `/newsletter` - Newsletter subscription page
- `/about` - About page
- `/profile` - User profile settings (requires auth)

### Author Admin Routes

- `/login` - Author login page
- `/authors/posts` - List all posts
- `/authors/posts/new` - Create new post
- `/authors/posts/edit/:slug` - Edit post
- `/authors/drafts` - List draft posts
- `/authors/tags` - List all tags
- `/authors/tags/new` - Create new tag
- `/authors/tags/edit/:slug` - Edit tag
- `/authors/list` - List all authors (admin)

## 🎯 State Management

### Zustand Stores

- **useAuthStore**: Authentication state (user, token, login/logout)
- **useNewsletterStore**: Newsletter subscription state
- **useReadingListStore**: Reading list (saved posts)
- **useUIStore**: UI state (modals, search popup, etc.)

### React Query

- Server state management and caching
- Automatic refetching and background updates
- Optimistic updates for mutations

## 🎨 Styling

### Chakra UI

- Component-based design system
- Dark mode support
- Responsive design
- Custom theme configuration

### Theme

Theme configuration in `src/theme/index.ts`:
- Brand colors
- Typography
- Component styles
- Color palettes

## 📡 API Integration

### API Client

Centralized API client in `src/api/client.ts`:
- Axios instance with base URL configuration
- Request/response interceptors
- Automatic token injection
- Error handling

### API Endpoints

All API calls are organized by feature:
- `posts.ts` - Post-related endpoints
- `comments.ts` - Comment endpoints
- `authors.ts` - Author endpoints
- `tags.ts` - Tag endpoints
- `newsletter.ts` - Newsletter endpoints
- `admin.ts` - Admin/author endpoints
- `search.ts` - Search endpoints
- `reactions.ts` - Reaction endpoints

## 🧩 Components

### Layout Components

- **Layout**: Main layout wrapper with navbar and footer
- **Navbar**: Navigation bar with links, search, theme toggle
- **Footer**: Footer with links and copyright
- **Sidebar**: Sidebar with popular tags
- **ThemeToggleButton**: Dark/light mode toggle

### Blog Components

- **PostCard**: Post preview card (vertical/horizontal variants)
- **PostList**: List of post cards
- **PostDetail**: Full post view with content, comments, reactions
- **PostComments**: Comment section with nested comments
- **PostReactions**: Like button and count
- **PostTags**: Tag badges
- **PostShare**: Social sharing buttons
- **PostMeta**: Post metadata (date, author, reading time)
- **PostAuthor**: Author card
- **ReadingProgress**: Reading progress indicator
- **TableOfContents**: Table of contents from headings

### UI Components

- **Avatar**: Custom avatar component with theme support
- **Badge**: Badge component
- **Button**: Button component
- **Card**: Card component
- **CodeBlock**: Syntax-highlighted code block
- **LoginRegisterModal**: Login/register modal
- **MarkdownRenderer**: Markdown renderer with syntax highlighting
- **NewsletterPopup**: Newsletter subscription popup
- **SearchPopup**: Search popup with results

### Form Components

- **ContactForm**: Contact form
- **SearchForm**: Search form

## 🧪 Development

### Available Scripts

```bash
# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

### Code Style

- Use TypeScript for type safety
- Follow React best practices
- Use functional components and hooks
- Keep components focused and reusable
- Use Chakra UI components when possible

### Adding New Features

1. Create types in `src/types/`
2. Add API endpoints in `src/api/`
3. Create components in appropriate directory
4. Add routes in `src/App.tsx`
5. Update stores if needed

## 🐳 Docker

### Build Docker Image

```bash
# Build with API base URL
docker build --build-arg api_base_url=https://api.yourdomain.com -t blog-frontend:latest .
```

### Dockerfile

Multi-stage build:
1. **Build stage**: Installs dependencies and builds React app
2. **Production stage**: Serves static files with Nginx

### Nginx Configuration

Nginx config in `nginx.conf`:
- Serves static files
- Handles client-side routing
- Health check endpoint

## 🚀 Deployment

### Production Build

```bash
npm run build
```

Output directory: `dist/`

### Environment Variables

For production, set `VITE_API_BASE_URL` as build argument:
```bash
docker build --build-arg api_base_url=https://api.yourdomain.com .
```

### AWS Elastic Beanstalk

1. Build Docker image with production API URL
2. Push to Docker Hub
3. Deploy via GitHub Actions workflow
4. Uses `Dockerrun.aws.json` for configuration

### Static Hosting (Alternative)

Can also be deployed to:
- **Vercel**: Connect GitHub repo, set build command and output directory
- **Netlify**: Connect GitHub repo, set build settings
- **AWS S3 + CloudFront**: Upload `dist/` folder to S3 bucket

## 🎨 Customization

### Theme

Edit `src/theme/index.ts` to customize:
- Colors
- Fonts
- Component styles
- Breakpoints

### Components

All components are customizable:
- Modify existing components in `src/components/`
- Create new components following existing patterns
- Use Chakra UI theme tokens for consistency

## 🔍 Troubleshooting

### Common Issues

1. **API Connection Failed**:
   - Check `VITE_API_BASE_URL` is set correctly
   - Verify backend is running
   - Check CORS configuration on backend

2. **Build Errors**:
   - Clear `node_modules` and reinstall: `rm -rf node_modules && npm install`
   - Check TypeScript errors: `npm run build`
   - Verify all dependencies are installed

3. **Routing Issues**:
   - Ensure all routes are defined in `App.tsx`
   - Check route parameters match component expectations
   - Verify React Router version compatibility

4. **State Not Updating**:
   - Check store updates are correct
   - Verify React Query cache keys
   - Check component re-render conditions

## 📚 Resources

- [React Documentation](https://react.dev/)
- [TypeScript Documentation](https://www.typescriptlang.org/)
- [Vite Documentation](https://vitejs.dev/)
- [Chakra UI Documentation](https://chakra-ui.com/)
- [React Query Documentation](https://tanstack.com/query/latest)
- [React Router Documentation](https://reactrouter.com/)

---

**Built with React 18, TypeScript, Vite, and Chakra UI**
