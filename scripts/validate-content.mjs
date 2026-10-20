#!/usr/bin/env node
import { readFileSync, readdirSync, existsSync } from 'node:fs'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'
import matter from 'gray-matter'

const __dirname = dirname(fileURLToPath(import.meta.url))
const ROOT = join(__dirname, '..')
const POSTS_DIR = join(ROOT, 'content', 'posts')
const AUTHORS_DIR = join(ROOT, 'content', 'authors')

const REQUIRED = ['title', 'slug', 'date', 'excerpt', 'author']
const errors = []

const authorUsernames = new Set()
if (existsSync(AUTHORS_DIR)) {
  for (const file of readdirSync(AUTHORS_DIR).filter((f) => f.endsWith('.md'))) {
    const { data } = matter(readFileSync(join(AUTHORS_DIR, file), 'utf8'))
    authorUsernames.add(data.username || file.replace(/\.md$/, ''))
  }
}

const slugs = new Set()
// Future-dated posts are still validated in full - the build holds them back until their
// date, so a problem must surface here rather than on the day they go live.
const scheduled = []
if (existsSync(POSTS_DIR)) {
  for (const file of readdirSync(POSTS_DIR).filter((f) => f.endsWith('.md'))) {
    const { data } = matter(readFileSync(join(POSTS_DIR, file), 'utf8'))
    if (data.draft === true) continue
    for (const field of REQUIRED) {
      if (data[field] === undefined || data[field] === '') {
        errors.push(`${file}: missing required field "${field}"`)
      }
    }
    if (data.slug) {
      if (slugs.has(data.slug)) errors.push(`${file}: duplicate slug "${data.slug}"`)
      slugs.add(data.slug)
    }
    if (data.author && !authorUsernames.has(data.author)) {
      errors.push(`${file}: unknown author "${data.author}"`)
    }
    if (data.date && Number.isNaN(new Date(data.date).getTime())) {
      errors.push(`${file}: invalid date "${data.date}"`)
    } else if (data.date && new Date(data.date).getTime() > Date.now()) {
      scheduled.push({ slug: data.slug ?? file, date: new Date(data.date) })
    }
  }
}

if (errors.length) {
  console.error('Content validation failed:')
  for (const e of errors) console.error(`  - ${e}`)
  process.exit(1)
}
const live = slugs.size - scheduled.length
console.log(`Content valid: ${slugs.size} posts (${live} live, ${scheduled.length} scheduled), ${authorUsernames.size} authors`)
if (scheduled.length) {
  scheduled.sort((a, b) => a.date - b.date)
  for (const s of scheduled) console.log(`  scheduled ${s.date.toISOString().slice(0, 10)}  ${s.slug}`)
}
