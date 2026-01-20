import {
  Box,
  Flex,
  HStack,
  Link,
  IconButton,
  Button,
  useDisclosure,
  useColorModeValue,
  Stack,
  Text,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
} from '@chakra-ui/react'
import { Link as RouterLink, useNavigate, useLocation } from 'react-router-dom'
import { FaBars, FaTimes, FaEnvelope, FaChevronDown } from 'react-icons/fa'
import { CiSearch } from 'react-icons/ci'
import { FiLogIn } from 'react-icons/fi'
import { ThemeToggleButton } from './ThemeToggleButton'
import { useUIStore } from '@/store/useUIStore'
import { LoginRegisterModal } from '@/components/ui/LoginRegisterModal'
import { Avatar } from '@/components/ui/Avatar'
import { useState, useEffect } from 'react'
import { apiClient } from '@/api/client'
import { decodeJWT, getUserEmailFromToken } from '@/utils/auth'
import { useTags } from '@/api/tags'
import { Tag } from '@/types'

export const Navbar = () => {
  const { isOpen, onToggle } = useDisclosure()
  const { setNewsletterPopupOpen, setSearchPopupOpen } = useUIStore()
  const navigate = useNavigate()
  const location = useLocation()
  const bg = useColorModeValue('white', 'gray.900')
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [showLoginModal, setShowLoginModal] = useState(false)
  const [userEmail, setUserEmail] = useState<string | null>(null)
  const [userName, setUserName] = useState<string | null>(null)
  const { data: tagsData } = useTags()
  
  // Sort tags alphabetically
  const sortedTags = tagsData
    ? [...tagsData].sort((a: Tag, b: Tag) => a.name.localeCompare(b.name))
    : []
  
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const token = localStorage.getItem('auth_token')
        if (!token) {
          setIsAuthenticated(false)
          setUserEmail(null)
          setUserName(null)
          return
        }

        // Decode token to check if expired
        const decoded = decodeJWT(token)
        if (!decoded) {
          // Invalid token format
          localStorage.removeItem('auth_token')
          setIsAuthenticated(false)
          setUserEmail(null)
          setUserName(null)
          return
        }

        // Check if token is expired
        if (decoded.exp) {
          const expirationTime = decoded.exp * 1000 // Convert to milliseconds
          if (Date.now() >= expirationTime) {
            // Token expired
            localStorage.removeItem('auth_token')
            setIsAuthenticated(false)
            setUserEmail(null)
            setUserName(null)
            return
          }
        }

        // Token exists and not expired, validate with backend
        const email = getUserEmailFromToken(token)
        if (email) {
          try {
            // Try to fetch user info to validate token
            const userResponse = await apiClient.get(`/users/${encodeURIComponent(email)}`)
            // Token is valid
            setIsAuthenticated(true)
            setUserEmail(email)
            // Store user name for Avatar consistency
            setUserName(userResponse.data.name || null)
          } catch (error: any) {
            // Token invalid or user not found
            if (error.response?.status === 401 || error.response?.status === 404) {
              localStorage.removeItem('auth_token')
              setIsAuthenticated(false)
              setUserEmail(null)
              setUserName(null)
            } else {
              // Other error, keep token but don't set as authenticated
              setIsAuthenticated(false)
              setUserEmail(null)
              setUserName(null)
            }
          }
        } else {
          // No email in token
          localStorage.removeItem('auth_token')
          setIsAuthenticated(false)
          setUserEmail(null)
          setUserName(null)
        }
      } catch (error) {
        // Handle any localStorage errors
        localStorage.removeItem('auth_token')
        setIsAuthenticated(false)
        setUserEmail(null)
        setUserName(null)
      }
    }
    checkAuth()
    // Listen for storage changes (e.g., when user logs in from another tab)
    window.addEventListener('storage', checkAuth)
    // Also check on focus in case token was set in same tab
    window.addEventListener('focus', checkAuth)
    return () => {
      window.removeEventListener('storage', checkAuth)
      window.removeEventListener('focus', checkAuth)
    }
  }, [])

  const isActive = (path: string) => {
    if (path === '/') return location.pathname === '/'
    return location.pathname.startsWith(path)
  }

  const handleSearchClick = () => {
    setSearchPopupOpen(true)
  }

  const handleSubscribeClick = () => {
    setNewsletterPopupOpen(true)
  }

  const handleLoginClick = () => {
    setShowLoginModal(true)
  }

  const handleLogout = () => {
    localStorage.removeItem('auth_token')
    setIsAuthenticated(false)
    setUserEmail(null)
    setUserName(null)
    navigate('/')
  }

  const handleAuthSuccess = async () => {
    setIsAuthenticated(true)
    const token = localStorage.getItem('auth_token')
    if (token) {
      const email = getUserEmailFromToken(token)
      setUserEmail(email)
      // Fetch user name for Avatar consistency
      if (email) {
        try {
          const userResponse = await apiClient.get(`/users/${encodeURIComponent(email)}`)
          setUserName(userResponse.data.name || null)
        } catch (error) {
          // If fetch fails, just use email
          setUserName(null)
        }
      }
    }
  }

  return (
    <Box bg={bg} borderBottom="1px" borderColor={borderColor} position="sticky" top={0} zIndex={1000}>
      <Flex
        h={16}
        alignItems="center"
        justifyContent="space-between"
        px={{ base: 4, md: 8, lg: 16 }}
        maxW={{ base: "680px", md: "100%" }}
        mx="auto"
      >
        <HStack spacing={8} alignItems="center">
          <Link as={RouterLink} to="/" fontWeight="bold" fontSize="xl">
            Ibrahim Abdinur's Blog
          </Link>
          <HStack as="nav" spacing={4} display={{ base: 'none', md: 'flex' }}>
            <Link 
              as={RouterLink} 
              to="/"
              pb={1}
              borderBottom="2px solid"
              borderColor={isActive('/') ? 'brand.600' : 'transparent'}
              _hover={{ textDecoration: 'none', borderColor: 'brand.600' }}
            >
              Home
            </Link>
            <Link 
              as={RouterLink} 
              to="/about"
              pb={1}
              borderBottom="2px solid"
              borderColor={isActive('/about') ? 'brand.600' : 'transparent'}
              _hover={{ textDecoration: 'none', borderColor: 'brand.600' }}
            >
              About
            </Link>
            <Link 
              as={RouterLink} 
              to="/archive"
              pb={1}
              borderBottom="2px solid"
              borderColor={isActive('/archive') ? 'brand.600' : 'transparent'}
              _hover={{ textDecoration: 'none', borderColor: 'brand.600' }}
            >
              Archive
            </Link>
            <Menu placement="bottom-start">
              {({ isOpen }) => (
                <>
                  <MenuButton
                    as={Box}
                    cursor="pointer"
                  >
                    <HStack spacing={1}>
                      <Text fontWeight="normal" fontSize="md">
                        Series
                      </Text>
                      <Box
                        as={FaChevronDown}
                        fontSize="10px"
                        transform={isOpen ? 'rotate(180deg)' : 'rotate(0deg)'}
                        transition="transform 0.2s"
                      />
                    </HStack>
                  </MenuButton>
                  <MenuList>
                    {sortedTags.map((tag: Tag) => (
                      <MenuItem key={tag.id} as={RouterLink} to={`/series/${tag.slug}`}>
                        {tag.name}
                      </MenuItem>
                    ))}
                  </MenuList>
                </>
              )}
            </Menu>
          </HStack>
        </HStack>

        <Flex alignItems="center" gap={2}>
          <IconButton
            aria-label="Search"
            icon={<CiSearch size={20} />}
            variant="ghost"
            onClick={handleSearchClick}
          />
          <ThemeToggleButton />
          <Button
            leftIcon={<FaEnvelope />}
            onClick={handleSubscribeClick}
            bg="brand.600"
            color="white"
            _hover={{ bg: 'brand.700' }}
            size="sm"
            display={{ base: 'none', md: 'flex' }}
          >
            Subscribe
          </Button>
          {isAuthenticated ? (
            <Menu>
              <MenuButton
                as={IconButton}
                aria-label="User menu"
                icon={
                  <Avatar
                    size="sm"
                    name={userName || userEmail || 'User'}
                    src={userEmail ? `/api/v1/users/${encodeURIComponent(userEmail)}/profile-image` : undefined}
                    onError={() => {
                      // Silently fail if image can't load
                    }}
                  />
                }
                variant="ghost"
              />
              <MenuList>
                <MenuItem onClick={() => navigate('/profile')}>
                  Profile
                </MenuItem>
                <MenuItem onClick={handleLogout}>
                  Logout
                </MenuItem>
              </MenuList>
            </Menu>
          ) : (
            <IconButton
              aria-label="Login"
              icon={<FiLogIn />}
              variant="ghost"
              onClick={handleLoginClick}
              display={{ base: 'none', md: 'flex' }}
            />
          )}

          <IconButton
            display={{ base: 'flex', md: 'none' }}
            onClick={onToggle}
            icon={isOpen ? <FaTimes /> : <FaBars />}
            variant="ghost"
            aria-label="Toggle menu"
          />
        </Flex>
      </Flex>

      {isOpen && (
        <Box pb={4} display={{ md: 'none' }} px={4} maxW="680px" mx="auto">
          <Stack as="nav" spacing={4}>
            <Link 
              as={RouterLink} 
              to="/" 
              onClick={onToggle}
              fontWeight={isActive('/') ? 'semibold' : 'normal'}
              color={isActive('/') ? 'brand.600' : 'inherit'}
            >
              Home
            </Link>
            <Link 
              as={RouterLink} 
              to="/about" 
              onClick={onToggle}
              fontWeight={isActive('/about') ? 'semibold' : 'normal'}
              color={isActive('/about') ? 'brand.600' : 'inherit'}
            >
              About
            </Link>
            <Link 
              as={RouterLink} 
              to="/archive" 
              onClick={onToggle}
              fontWeight={isActive('/archive') ? 'semibold' : 'normal'}
              color={isActive('/archive') ? 'brand.600' : 'inherit'}
            >
              Archive
            </Link>
            <Text fontWeight="medium" color="gray.600" _dark={{ color: 'gray.400' }}>
              Series
            </Text>
            {sortedTags.map((tag: Tag) => (
              <Link key={tag.id} as={RouterLink} to={`/series/${tag.slug}`} onClick={onToggle} pl={4}>
                {tag.name}
              </Link>
            ))}
            <HStack spacing={4} pt={4} borderTop="1px" borderColor={borderColor}>
              {isAuthenticated ? (
                <>
                  <Link as={RouterLink} to="/profile" onClick={onToggle}>
                    Profile
                  </Link>
                  <Link onClick={() => { handleLogout(); onToggle(); }}>
                    Logout
                  </Link>
                </>
              ) : (
                <Button
                  leftIcon={<FiLogIn />}
                  onClick={() => {
                    handleLoginClick()
                    onToggle()
                  }}
                  variant="outline"
                  size="sm"
                  w="full"
                >
                  Login
                </Button>
              )}
              <Button
                leftIcon={<FaEnvelope />}
                onClick={() => {
                  handleSubscribeClick()
                  onToggle()
                }}
                bg="brand.600"
                color="white"
                _hover={{ bg: 'brand.700' }}
                size="sm"
                w="full"
              >
                Subscribe
              </Button>
            </HStack>
          </Stack>
        </Box>
      )}

      <LoginRegisterModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        onSuccess={handleAuthSuccess}
      />
    </Box>
  )
}

