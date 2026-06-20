export const formatDate = (date: string | Date): string => {
  const d = typeof date === 'string' ? new Date(date + (date.includes('T') ? '' : 'T00:00:00')) : date
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  const month = months[d.getMonth()]
  const day = d.getDate().toString().padStart(2, '0')
  const year = d.getFullYear()
  return `${month} ${day}, ${year}`
}

export const formatShortDate = (date: string | Date): string => {
  const d = typeof date === 'string' ? new Date(date + (date.includes('T') ? '' : 'T00:00:00')) : date
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  const month = months[d.getMonth()]
  const day = d.getDate().toString().padStart(2, '0')
  const year = d.getFullYear()
  return `${month} ${day}, ${year}`
}

export const formatRelativeTime = (date: string | Date): string => {
  const d = new Date(date)
  const now = new Date()
  const diffInSeconds = Math.floor((now.getTime() - d.getTime()) / 1000)

  if (diffInSeconds < 60) return 'just now'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} minutes ago`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hours ago`
  if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)} days ago`
  if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 604800)} weeks ago`
  if (diffInSeconds < 31536000) return `${Math.floor(diffInSeconds / 2592000)} months ago`
  return `${Math.floor(diffInSeconds / 31536000)} years ago`
}

