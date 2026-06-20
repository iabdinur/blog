import { useEffect } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { useUIStore } from '@/store/useUIStore'

export const Search = () => {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { setSearchPopupOpen } = useUIStore()
  const query = searchParams.get('q') || ''

  useEffect(() => {
    // Open the search popup when the /search route is accessed
    setSearchPopupOpen(true)
    
    // If there's a query parameter, let the popup read it first
    // Then navigate back to home after a brief delay to keep URL clean
    const timer = setTimeout(() => {
      navigate('/', { replace: true })
    }, 100)
    
    return () => clearTimeout(timer)
  }, [setSearchPopupOpen, navigate, query])

  // Return null since we're just opening the popup
  return null
}

