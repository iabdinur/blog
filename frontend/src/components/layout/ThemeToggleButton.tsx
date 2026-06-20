import { IconButton, useColorMode, useColorModeValue } from '@chakra-ui/react'
import { FaMoon, FaSun } from 'react-icons/fa'

export const ThemeToggleButton = () => {
  const { toggleColorMode } = useColorMode()
  const Icon = useColorModeValue(FaMoon, FaSun)

  return (
    <IconButton
      aria-label="Toggle theme"
      icon={<Icon />}
      onClick={toggleColorMode}
      variant="ghost"
    />
  )
}

