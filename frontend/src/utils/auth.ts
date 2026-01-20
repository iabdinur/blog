/**
 * Utility functions for authentication
 */

export interface DecodedJWT {
  sub?: string
  username?: string
  roles?: string[]
  iat?: number
  exp?: number
}

/**
 * Decode JWT token (without verification - client-side only)
 */
export const decodeJWT = (token: string): DecodedJWT | null => {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) {
      return null
    }
    const payload = JSON.parse(atob(parts[1]))
    return payload
  } catch (error) {
    return null
  }
}

/**
 * Check if user has ROLE_AUTHOR in their JWT token
 */
export const isAuthor = (token: string | null): boolean => {
  if (!token) return false
  const decoded = decodeJWT(token)
  if (!decoded || !decoded.roles) return false
  return decoded.roles.includes('ROLE_AUTHOR')
}

/**
 * Get user email from JWT token
 */
export const getUserEmailFromToken = (token: string | null): string | null => {
  if (!token) return null
  const decoded = decodeJWT(token)
  if (!decoded) return null
  return decoded.sub || decoded.username || null
}
