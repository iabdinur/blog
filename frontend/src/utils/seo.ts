import { PostMeta } from '@/types'

export const generateSEOTags = (meta: PostMeta) => {
  const tags = [
    { property: 'og:title', content: meta.title },
    { property: 'og:description', content: meta.description },
    { property: 'og:type', content: 'article' },
    { name: 'twitter:card', content: 'summary_large_image' },
    { name: 'twitter:title', content: meta.title },
    { name: 'twitter:description', content: meta.description },
  ]

  if (meta.image) {
    tags.push(
      { property: 'og:image', content: meta.image },
      { name: 'twitter:image', content: meta.image }
    )
  }

  if (meta.publishedTime) {
    tags.push({ property: 'article:published_time', content: meta.publishedTime })
  }

  if (meta.modifiedTime) {
    tags.push({ property: 'article:modified_time', content: meta.modifiedTime })
  }

  if (meta.author) {
    tags.push({ property: 'article:author', content: meta.author })
  }

  if (meta.tags) {
    meta.tags.forEach((tag) => {
      tags.push({ property: 'article:tag', content: tag })
    })
  }

  return tags
}

