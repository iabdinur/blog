# Ibrahim Abdinur's Blog

This is the source code for my blog at [blog.iabdinur.dev](https://blog.iabdinur.dev).
It's a personal blog where I write about AI, software engineering, and lessons from
graduate studies.

The whole site is just Markdown in git. There's no database, no backend, no admin
panel — the posts you read are the files in this repository, and GitHub is the CMS.
A build script turns the Markdown into static JSON, a React app reads that JSON, and
GitHub Pages serves the result. Publishing a post means merging a pull request.

## Why content-as-code

This blog used to run on Spring Boot, PostgreSQL, and AWS. That is a lot of moving
parts for a weekly newsletter, and most of those parts are services that come and go.
I moved to a static site for two reasons:

- **Longevity.** A pile of Markdown files and a static build will still work in ten
  years. The site runs perfectly well on my laptop, and it'll run fine anywhere else,
  too — after all, it's just static files. The fewer external services the content
  depends on, the longer it survives.
- **Ownership.** The posts, their history, and the discussion around them all live in
  version control on GitHub. Nothing is locked inside a hosted platform's database, so
  the entire blog can move elsewhere without losing anything.

I kept the React frontend I'd already built — the Markdown renderer, table of contents,
reading progress, dark mode, reading list — and dropped everything that needed a server.

## The previous version

The first version of this blog was a full-stack platform,
not a static site. The frontend was the same React + TypeScript + Chakra UI app, but
behind it sat a Spring Boot 3 / Java 21 REST API backed by PostgreSQL, with AWS S3 for
image storage and AWS SES for transactional email. It did everything a hosted blog does:
JWT auth, an author admin panel, comments, reactions, and a newsletter — and was built
to scale to tens of thousands of readers.

Deployment matched that ambition: the API and frontend were containerized with Docker,
pushed to Docker Hub, and deployed to AWS Elastic Beanstalk (with the database on RDS)
through GitHub Actions. That full-stack version lives on in its own repository,
[iabdinur/blog-app](https://github.com/iabdinur/blog-app), as a backup. It worked well,
but it was a lot of infrastructure — and recurring cost — for a weekly newsletter, which
is exactly what pushed me toward the static setup described here. The old backend, S3,
SES, and Elastic Beanstalk pieces are all gone from this repo.

## How it works

```text
content/*.md  →  scripts/build-content.mjs  →  frontend/public/data/*.json
                                             →  vite build  →  dist/  →  GitHub Pages
```

```text
content/
  posts/        # one Markdown file per post (frontmatter + body)
  authors/      # author profile(s)
  _drafts/      # ignored by the build
  series.json   # series/tag definitions
scripts/
  build-content.mjs     # Markdown → JSON generator
  validate-content.mjs  # CI validation (unique slugs, required fields)
frontend/       # React + Vite app (reads /data/*.json)
.github/workflows/pages.yml  # build + deploy to GitHub Pages
```

## Writing a post

1. Create `content/posts/YYYY-MM-DD-slug.md`:

   ```yaml
   ---
   title: My Post
   slug: my-post
   date: 2026-06-16T10:00:00Z
   excerpt: One-sentence summary.
   tags: [software-engineering]
   author: iabdinur
   coverImage: /images/covers/my-post.jpg   # optional
   featured: false
   draft: false
   ---

   Markdown body here.
   ```

2. Commit and open a PR. CI validates the content on the PR.
3. Merge to `main` → GitHub Pages deploys automatically.

Set `featured: true` on one post to control the Home page hero. `draft: true` (or
placing the file in `content/_drafts/`) excludes it from the build.

## Local development

```bash
npm install            # root: installs content-build tooling (gray-matter)
cd frontend
npm install            # frontend deps
npm run dev            # predev rebuilds content, then starts Vite
```

`npm run build` (in `frontend/`) validates + builds content, type-checks, builds the
app, and copies `index.html` → `404.html` for SPA routing on GitHub Pages.

## Comments

Comments are handled by [Giscus](https://giscus.app), which stores them as GitHub
Discussions — so the conversation around a post lives in version control alongside the
post itself, not in a third-party service. They stay hidden until configured:

1. Enable **Discussions** on the GitHub repo and install the **giscus** app.
2. Get the repo/category IDs from https://giscus.app.
3. Provide them as build-time env vars (in CI, or a `.env` in `frontend/`):

   ```text
   VITE_GISCUS_REPO=iabdinur/blog
   VITE_GISCUS_REPO_ID=...
   VITE_GISCUS_CATEGORY=Comments
   VITE_GISCUS_CATEGORY_ID=...
   ```

The Pages workflow reads Giscus/GoatCounter values from repo **Variables** (Settings →
Secrets and variables → Actions → Variables): `GISCUS_REPO`, `GISCUS_REPO_ID`,
`GISCUS_CATEGORY`, `GISCUS_CATEGORY_ID`, `GOATCOUNTER_CODE`.

## Engagement numbers (optional)

The **Latest / Discussions** sort and the per-post view count are wired in without a
backend, and both degrade gracefully (sort by 0, no views) when unconfigured.

- **Comment counts** (`discussions` sort) are baked into the content JSON at build time
  by `build-content.mjs`, which queries the GitHub Discussions GraphQL API. In CI the
  workflow passes `GITHUB_TOKEN` and `GISCUS_REPO` automatically — no setup needed once
  Discussions/giscus are on.
- **Views** come from [GoatCounter](https://www.goatcounter.com) (privacy-friendly).
  Set `VITE_GOATCOUNTER_CODE` to your site code or full URL to turn it on.

## Custom domain

`frontend/public/CNAME` contains `blog.iabdinur.dev`. Point a DNS `CNAME` record for
that subdomain at `iabdinur.github.io`, and set the custom domain in the repo's Pages
settings.

## Contributions

Spotted a typo, a broken link, or a bug? Pull requests are very welcome. Comments are
also contributions — they're stored as GitHub Discussions via Giscus. I'm not looking
for guest posts, though; the articles here are my own.

## License

The code in this repository is licensed under the [MIT License](LICENSE). The blog
content (the posts under `content/`) is © Ibrahim Abdinur.
