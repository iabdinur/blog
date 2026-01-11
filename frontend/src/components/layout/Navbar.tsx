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
import { ThemeToggleButton } from './ThemeToggleButton'
import { useUIStore } from '@/store/useUIStore'

export const Navbar = () => {
  const { isOpen, onToggle } = useDisclosure()
  const { setNewsletterPopupOpen, setSearchPopupOpen } = useUIStore()
  const navigate = useNavigate()
  const location = useLocation()
  const bg = useColorModeValue('white', 'gray.900')
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  
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
                    <MenuItem as={RouterLink} to="/series/ai">
                      AI
                    </MenuItem>
                    <MenuItem as={RouterLink} to="/series/devops">
                      DevOps
                    </MenuItem>
                    <MenuItem as={RouterLink} to="/series/interview-prep">
                      Interview Prep
                    </MenuItem>
                    <MenuItem as={RouterLink} to="/series/system-design">
                      System Design
                    </MenuItem>
                    <MenuItem as={RouterLink} to="/series/uiuc-mcs">
                      UIUC MCS
                    </MenuItem>
                    <MenuItem as={RouterLink} to="/series/software-engineering">
                      Software Engineering
                    </MenuItem>
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
            <Link as={RouterLink} to="/series/ai" onClick={onToggle} pl={4}>
              AI
            </Link>
            <Link as={RouterLink} to="/series/devops" onClick={onToggle} pl={4}>
              DevOps
            </Link>
            <Link as={RouterLink} to="/series/interview-prep" onClick={onToggle} pl={4}>
              Interview Prep
            </Link>
            <Link as={RouterLink} to="/series/system-design" onClick={onToggle} pl={4}>
              System Design
            </Link>
            <Link as={RouterLink} to="/series/uiuc-mcs" onClick={onToggle} pl={4}>
              UIUC MCS
            </Link>
            <Link as={RouterLink} to="/series/software-engineering" onClick={onToggle} pl={4}>
              Software Engineering
            </Link>
          </Stack>
        </Box>
      )}
    </Box>
  )
}

