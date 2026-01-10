/**
 * Extracts the first sentence from a text string.
 * A sentence is defined as text ending with . ! or ? followed by a space or end of string.
 */
export const getFirstSentence = (text: string): string => {
  if (!text) return ''
  
  // Remove leading/trailing whitespace
  const trimmed = text.trim()
  
  // Find the first sentence ending (., !, or ? followed by space or end of string)
  const sentenceEndRegex = /[.!?]+(?:\s+|$)/
  const match = trimmed.match(sentenceEndRegex)
  
  if (match && match.index !== undefined) {
    // Return text up to and including the sentence ending
    return trimmed.substring(0, match.index + match[0].length).trim()
  }
  
  // If no sentence ending found, return the whole text (it's already one sentence or less)
  return trimmed
}

