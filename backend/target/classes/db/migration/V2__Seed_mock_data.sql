-- Seed mock data for development

-- Insert Admin User
-- Password: password (BCrypt hash)
INSERT INTO users (name, email, password, created_at, updated_at)
VALUES (
    'Ibrahim Abdinur',
    'iabdinur@icloud.com',
    '$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6',
    '2024-01-01 00:00:00',
    '2024-01-01 00:00:00'
)
ON CONFLICT (email) DO NOTHING;

-- Insert Author
INSERT INTO authors (name, username, email, bio, avatar, website, twitter, github, linkedin, followers_count, following_count, posts_count, joined_at, created_at, updated_at)
VALUES (
    'Ibrahim Abdinur',
    'iabdinur',
    'iabdinur@icloud.com',
    'Full-stack developer passionate about web technologies, AI, and developer tools.',
    'https://api.dicebear.com/7.x/avataaars/svg?seed=ibrahim',
    'https://iabdinur.com',
    'iabdinur',
    'iabdinur',
    'https://www.linkedin.com/in/ibrahim-abdinur/',
    0,
    0,
    5,
    '2024-01-01 00:00:00',
    '2024-01-01 00:00:00',
    '2024-01-01 00:00:00'
);

-- Insert Tags (Series)
INSERT INTO tags (name, slug, description, posts_count, created_at, updated_at)
VALUES
    ('Artificial Intelligence', 'ai', 'Artificial Intelligence and Machine Learning articles', 1, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('DevOps', 'devops', 'DevOps workflows and practices', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('SWE Interview Preparation', 'interview-prep', 'Interview preparation guides and resources', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('System Design', 'system-design', 'System design fundamentals and patterns', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('UIUC Master of Computer Science', 'uiuc-mcs', 'UIUC Master of Computer Science reflections and course reviews', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('Software Engineering', 'software-engineering', 'Software engineering principles, practices, and methodologies', 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- Insert Posts
-- Note: We'll reference the author by getting the ID from the authors table
-- and tags by their slugs, then we'll link them in post_tags

-- Post 1: Welcome to My Weekly Newsletter
INSERT INTO posts (title, slug, content, excerpt, cover_image, author_id, published_at, is_published, views, likes, comments_count, reading_time, created_at, updated_at)
VALUES (
    'Welcome to My Weekly Newsletter',
    'welcome-to-my-weekly-newsletter',
    '# Welcome to My Weekly Newsletter

## Hello and Welcome!

I''m thrilled to welcome you to our weekly newsletter! This is your go-to source for the latest insights, tutorials, and news in the world of web development, artificial intelligence, and technology.

## What to Expect

Each week, you''ll receive:

- **In-depth tutorials** on modern web technologies
- **Industry insights** and trends analysis
- **AI developments** and practical applications
- **Developer tool recommendations**
- **Community highlights** and success stories

## Our Mission

Our goal is to help developers stay current with rapidly evolving technologies while providing practical, actionable content that you can apply to your projects immediately.

## Topics We''ll Cover

- **Frontend Development**: React, TypeScript, Next.js, and modern CSS
- **Backend Technologies**: Node.js, databases, API design
- **AI & Machine Learning**: Practical applications for developers
- **Developer Tools**: Productivity boosters and workflow optimization
- **Career Growth**: Learning strategies and professional development

## Join the Community

This isn''t just a one-way conversation! I encourage you to:

- **Reply with questions** or topic suggestions
- **Share your experiences** and projects
- **Connect with other readers** in our community
- **Provide feedback** to help shape future content

## Getting Started

To make the most of this newsletter:

1. **Bookmark this page** for future reference
2. **Share with colleagues** who might benefit
3. **Set aside time** each week to read and implement
4. **Keep a learning journal** to track your progress

## About Me

I''m Ibrahim Osman Abdinur, a full-stack developer passionate about creating educational content that helps developers level up their skills. With experience across the stack, I''m excited to share what I''ve learned and continue learning alongside you.

## Let''s Connect

Feel free to reach out anytime:
- **LinkedIn**: https://www.linkedin.com/in/ibrahim-abdinur/
- **GitHub**: https://github.com/iabdinur
- **Website**: https://iabdinur.com

## What''s Next?

In our next edition, we''ll dive into **Building Modern Web Applications with React and TypeScript** - a comprehensive guide to best practices and patterns that will make your applications more scalable and maintainable.

## Thank You!

Thank you for joining me on this journey. I''m excited to share knowledge, learn together, and build amazing things!

Happy coding! 🚀

*Ibrahim Osman Abdinur*',
    'Join us on this journey as we explore the latest in web development, AI, and technology. Get ready for weekly insights, tutorials, and industry news.',
    'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800',
    (SELECT id FROM authors WHERE username = 'iabdinur'),
    '2026-01-06 00:00:00',
    true,
    0,
    0,
    0,
    5,
    '2026-01-06 00:00:00',
    '2026-01-06 00:00:00'
);

-- Post 2: Building Modern Web Applications with React and TypeScript
INSERT INTO posts (title, slug, content, excerpt, cover_image, author_id, published_at, is_published, views, likes, comments_count, reading_time, created_at, updated_at)
VALUES (
    'Building Modern Web Applications with React and TypeScript',
    'building-modern-web-apps-react-typescript',
    '# Building Modern Web Applications with React and TypeScript

React and TypeScript have become the go-to combination for building modern web applications. In this article, we''ll explore why this combination is so powerful and how to leverage it effectively.

## Why React + TypeScript?

TypeScript adds static typing to JavaScript, which helps catch errors early and improves code quality. When combined with React, you get:

- **Type Safety**: Catch errors at compile time
- **Better IDE Support**: Autocomplete and IntelliSense
- **Improved Refactoring**: Confidently rename and restructure code
- **Enhanced Documentation**: Types serve as inline documentation

## Getting Started

Let''s start with a simple component:

```typescript
interface ButtonProps {
  label: string
  onClick: () => void
  variant?: ''primary'' | ''secondary''
}

const Button: React.FC<ButtonProps> = ({ label, onClick, variant = ''primary'' }) => {
  return (
    <button 
      className={`btn btn-$${variant}`}
      onClick={onClick}
    >
      {label}
    </button>
  )
}
```

## Best Practices

1. **Use TypeScript strict mode** - Enable all strict type checking options
2. **Define explicit types** - Don''t rely on type inference for props
3. **Use generics** - Create reusable, type-safe components
4. **Leverage utility types** - Use Pick, Omit, Partial, etc.

## Conclusion

React and TypeScript together provide a robust foundation for building scalable applications. Start incorporating TypeScript into your React projects today!',
    'Learn how to build scalable and maintainable web applications using React 18 and TypeScript. This comprehensive guide covers best practices, patterns, and real-world examples.',
    'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800',
    (SELECT id FROM authors WHERE username = 'iabdinur'),
    '2026-01-03 10:00:00',
    true,
    1234,
    42,
    8,
    8,
    '2026-01-03 10:00:00',
    '2026-01-03 10:00:00'
);

-- Post 3: Understanding React Hooks
INSERT INTO posts (title, slug, content, excerpt, cover_image, author_id, published_at, is_published, views, likes, comments_count, reading_time, created_at, updated_at)
VALUES (
    'Understanding React Hooks: A Deep Dive',
    'understanding-react-hooks-deep-dive',
    '# Understanding React Hooks: A Deep Dive

React Hooks revolutionized the way we write React components. Let''s explore how to use them effectively.

## The Basics: useState and useEffect

The most commonly used hooks are `useState` and `useEffect`:

```typescript
import { useState, useEffect } from ''react''

function Counter() {
  const [count, setCount] = useState(0)
  
  useEffect(() => {
    document.title = `Count: $${count}`
  }, [count])
  
  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>
        Increment
      </button>
    </div>
  )
}
```

## Custom Hooks

Create reusable logic with custom hooks:

```typescript
function useLocalStorage<T>(key: string, initialValue: T) {
  const [value, setValue] = useState<T>(() => {
    const item = localStorage.getItem(key)
    return item ? JSON.parse(item) : initialValue
  })
  
  useEffect(() => {
    localStorage.setItem(key, JSON.stringify(value))
  }, [key, value])
  
  return [value, setValue] as const
}
```

## Advanced Patterns

- **useReducer**: For complex state logic
- **useContext**: For global state management
- **useMemo & useCallback**: For performance optimization
- **useRef**: For accessing DOM elements and persisting values

## Conclusion

Hooks make React code more readable and maintainable. Practice these patterns to become a React expert!',
    'Master React Hooks with this comprehensive guide. From useState to custom hooks, learn everything you need to know to write modern React code.',
    'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800',
    (SELECT id FROM authors WHERE username = 'iabdinur'),
    '2026-01-02 14:30:00',
    true,
    2156,
    28,
    15,
    12,
    '2026-01-02 14:30:00',
    '2026-01-02 14:30:00'
);

-- Post 4: TypeScript Advanced Types
INSERT INTO posts (title, slug, content, excerpt, cover_image, author_id, published_at, is_published, views, likes, comments_count, reading_time, created_at, updated_at)
VALUES (
    'TypeScript Advanced Types You Should Know',
    'typescript-advanced-types',
    '# TypeScript Advanced Types You Should Know

TypeScript offers powerful type system features that go beyond basic types. Let''s explore some advanced concepts.

## Utility Types

TypeScript provides several built-in utility types:

```typescript
// Partial - Make all properties optional
type User = { name: string; email: string; age: number }
type PartialUser = Partial<User>

// Pick - Select specific properties
type UserPreview = Pick<User, ''name'' | ''email''>

// Omit - Exclude specific properties
type UserWithoutAge = Omit<User, ''age''>

// Record - Create object type with specific keys
type Roles = ''admin'' | ''user'' | ''guest''
type Permissions = Record<Roles, string[]>
```

## Conditional Types

Create types that depend on conditions:

```typescript
type IsString<T> = T extends string ? true : false

type A = IsString<string> // true
type B = IsString<number> // false
```

## Mapped Types

Transform existing types:

```typescript
type Readonly<T> = {
  readonly [P in keyof T]: T[P]
}

type Nullable<T> = {
  [P in keyof T]: T[P] | null
}
```

## Template Literal Types

```typescript
type Color = ''red'' | ''blue'' | ''green''
type Shade = ''light'' | ''dark''
type ColorShade = `$${Shade}-$${Color}` // ''light-red'' | ''dark-red'' | ...
```

## Conclusion

These advanced TypeScript features enable you to write more expressive and type-safe code. Experiment with them in your projects!',
    'Discover powerful TypeScript features that will take your code to the next level. Utility types, conditional types, and more.',
    'https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=800',
    (SELECT id FROM authors WHERE username = 'iabdinur'),
    '2026-01-01 09:00:00',
    true,
    987,
    56,
    3,
    10,
    '2026-01-01 09:00:00',
    '2026-01-01 09:00:00'
);

-- Post 5: Getting Started with AI Development
INSERT INTO posts (title, slug, content, excerpt, cover_image, author_id, published_at, is_published, views, likes, comments_count, reading_time, created_at, updated_at)
VALUES (
    'Getting Started with AI Development: A Practical Guide',
    'getting-started-ai-development',
    '# Getting Started with AI Development: A Practical Guide

Artificial Intelligence is transforming software development. Here''s how to get started integrating AI into your applications.

## AI APIs vs. Custom Models

You have two main options:

1. **Use AI APIs** (OpenAI, Anthropic, Google AI)
   - Fast to integrate
   - No training required
   - Pay per use

2. **Build Custom Models**
   - Full control
   - Privacy-friendly
   - Requires ML expertise

## Quick Start with OpenAI API

```typescript
import OpenAI from ''openai''

const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY
})

async function generateText(prompt: string) {
  const completion = await openai.chat.completions.create({
    model: "gpt-4",
    messages: [{ role: "user", content: prompt }]
  })
  
  return completion.choices[0].message.content
}
```

## Use Cases

- **Content Generation**: Blog posts, summaries, translations
- **Code Assistance**: Code completion, bug detection
- **Data Analysis**: Pattern recognition, predictions
- **Chatbots**: Customer support, virtual assistants

## Best Practices

1. **Start small** - Begin with simple use cases
2. **Monitor costs** - AI APIs can be expensive
3. **Handle errors** - Rate limits and failures
4. **Optimize prompts** - Better prompts = better results
5. **Consider privacy** - Be mindful of sensitive data

## Conclusion

AI development is more accessible than ever. Start experimenting and see how AI can enhance your applications!',
    'Learn how to integrate AI into your applications. From APIs to machine learning models, discover the tools and techniques you need.',
    'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800',
    (SELECT id FROM authors WHERE username = 'iabdinur'),
    '2025-12-30 16:00:00',
    true,
    3421,
    89,
    22,
    15,
    '2025-12-30 16:00:00',
    '2025-12-30 16:00:00'
);

-- Post 6: Essential Developer Tools
INSERT INTO posts (title, slug, content, excerpt, cover_image, author_id, published_at, is_published, views, likes, comments_count, reading_time, created_at, updated_at)
VALUES (
    'Essential Developer Tools for 2026',
    'essential-developer-tools-2026',
    '# Essential Developer Tools for 2026

The right tools can 10x your productivity. Here are the essential tools every developer should know about in 2026.

## Code Editors

### VS Code / Cursor
Still the most popular choice with an incredible extension ecosystem.

**Must-have extensions:**
- ESLint & Prettier
- GitLens
- Thunder Client
- Error Lens

## Terminal Tools

```bash
# Modern CLI tools
npm install -g zx           # Better shell scripting
brew install fzf            # Fuzzy finder
brew install ripgrep        # Faster grep
brew install bat            # Better cat
```

## Version Control

**Git + GitHub CLI**

```bash
# GitHub CLI makes PR management easy
gh pr create --title "Feature" --body "Description"
gh pr list
gh pr merge
```

## API Development

- **Thunder Client** - VS Code REST client
- **Postman** - Full-featured API platform
- **Hoppscotch** - Open-source alternative

## Database Tools

- **TablePlus** - Beautiful database GUI
- **Prisma Studio** - For Prisma users
- **MongoDB Compass** - MongoDB GUI

## Monitoring & Debugging

- **Sentry** - Error tracking
- **LogRocket** - Session replay
- **Datadog** - Performance monitoring

## AI Assistants

- **GitHub Copilot** - Code completion
- **Cursor** - AI-powered editor
- **ChatGPT** - General assistance

## Productivity

- **Notion** - Documentation
- **Linear** - Issue tracking
- **Raycast** - Mac launcher
- **Fig** - Terminal autocomplete

## Conclusion

These tools form a solid foundation for modern development. Experiment and find what works best for your workflow!',
    'Boost your productivity with these must-have developer tools. From code editors to debugging tools, discover what top developers are using.',
    'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800',
    (SELECT id FROM authors WHERE username = 'iabdinur'),
    '2025-12-28 11:00:00',
    true,
    1876,
    15,
    4,
    6,
    '2025-12-28 11:00:00',
    '2025-12-28 11:00:00'
);

-- Link posts to tags (post_tags junction table)
-- Welcome post -> Interview Prep
INSERT INTO post_tags (post_id, tag_id)
SELECT p.id, t.id
FROM posts p, tags t
WHERE p.slug = 'welcome-to-our-weekly-newsletter' AND t.slug = 'interview-prep';

-- Building Modern Web Apps -> AI, DevOps, Interview Prep
INSERT INTO post_tags (post_id, tag_id)
SELECT p.id, t.id
FROM posts p, tags t
WHERE p.slug = 'building-modern-web-apps-react-typescript' 
  AND t.slug IN ('ai', 'devops', 'interview-prep');

-- React Hooks -> AI, Interview Prep
INSERT INTO post_tags (post_id, tag_id)
SELECT p.id, t.id
FROM posts p, tags t
WHERE p.slug = 'understanding-react-hooks-deep-dive' 
  AND t.slug IN ('ai', 'interview-prep');

-- TypeScript Advanced Types -> DevOps, Interview Prep
INSERT INTO post_tags (post_id, tag_id)
SELECT p.id, t.id
FROM posts p, tags t
WHERE p.slug = 'typescript-advanced-types' 
  AND t.slug IN ('devops', 'interview-prep');

-- AI Development -> System Design, Interview Prep
INSERT INTO post_tags (post_id, tag_id)
SELECT p.id, t.id
FROM posts p, tags t
WHERE p.slug = 'getting-started-ai-development' 
  AND t.slug IN ('system-design', 'interview-prep');

-- Developer Tools -> UIUC MCS, Interview Prep
INSERT INTO post_tags (post_id, tag_id)
SELECT p.id, t.id
FROM posts p, tags t
WHERE p.slug = 'essential-developer-tools-2026' 
  AND t.slug IN ('uiuc-mcs', 'interview-prep');

-- Update tag post counts
UPDATE tags SET posts_count = (
    SELECT COUNT(DISTINCT pt.post_id)
    FROM post_tags pt
    WHERE pt.tag_id = tags.id
);

-- Remove any numeric or hex ID suffixes from slugs (clean slugs)
UPDATE posts 
SET slug = CASE 
    -- If slug ends with a dash and numeric ID (like -1, -2), remove it
    WHEN slug ~ '^.*-[0-9]+$' THEN 
        REGEXP_REPLACE(slug, '-[0-9]+$', '')
    -- If slug ends with a dash and hex ID (like -000000000001), remove it
    WHEN slug ~ '^.*-[0-9a-f]{1,}$' THEN 
        REGEXP_REPLACE(slug, '-[0-9a-f]+$', '')
    -- Otherwise, keep slug as-is
    ELSE 
        slug
END;

