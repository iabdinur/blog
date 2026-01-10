import { InputGroup, Input, InputLeftElement } from '@chakra-ui/react'
import { CiSearch } from 'react-icons/ci'
import { useNavigate } from 'react-router-dom'
import { useState } from 'react'

export interface SearchFormProps {
  placeholder?: string
  size?: 'sm' | 'md' | 'lg'
}

export const SearchForm = ({ placeholder = 'Search...', size = 'md' }: SearchFormProps) => {
  const [query, setQuery] = useState('')
  const navigate = useNavigate()

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (query.trim()) {
      navigate(`/search?q=${encodeURIComponent(query)}`)
      setQuery('')
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <InputGroup size={size}>
        <InputLeftElement pointerEvents="none">
          <CiSearch size={20} />
        </InputLeftElement>
        <Input
          placeholder={placeholder}
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </InputGroup>
    </form>
  )
}

