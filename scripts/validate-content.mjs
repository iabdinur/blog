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
    }
  }
}

if (errors.length) {
  console.error('Content validation failed:')
  for (const e of errors) console.error(`  - ${e}`)
  process.exit(1)
}
console.log(`Content valid: ${slugs.size} posts, ${authorUsernames.size} authors`)
