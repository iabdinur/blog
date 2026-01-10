import { ReactNode } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Box, VStack, HStack, Heading, Button, Divider, useColorModeValue, IconButton, Tooltip } from '@chakra-ui/react'
import { FiLogOut } from 'react-icons/fi'
import { accountApi } from '@/api/admin'
import { ThemeToggleButton } from '@/components/layout/ThemeToggleButton'

interface AdminLayoutProps {
  children: ReactNode
}

export const AdminLayout = ({ children }: AdminLayoutProps) => {
  const navigate = useNavigate()
  const bg = useColorModeValue('white', 'gray.900')
  const headerBg = useColorModeValue('white', 'gray.800')
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  const textColor = useColorModeValue('gray.900', 'gray.100')

  const handleLogout = () => {
    accountApi.logout()
    navigate('/admin/login')
  }

  return (
    <Box minH="100vh" bg={bg}>
      <Box bg={headerBg} borderBottom="1px" borderColor={borderColor}>
        <Box maxW="1200px" mx="auto" px={6} py={4}>
          <HStack justify="space-between">
            <HStack spacing={4}>
              <Heading size="md" color={textColor}>Admin Panel</Heading>
              <Link to="/admin/posts">
                <Button variant="ghost" size="sm" color={textColor}>
                  Posts
                </Button>
              </Link>
              <Link to="/admin/tags">
                <Button variant="ghost" size="sm" color={textColor}>
                  Tags
                </Button>
              </Link>
              <Link to="/admin/authors">
                <Button variant="ghost" size="sm" color={textColor}>
                  Authors
                </Button>
              </Link>
            </HStack>
            <HStack spacing={4}>
              <ThemeToggleButton />
              <Tooltip label="Logout" placement="bottom">
                <IconButton
                  aria-label="Logout"
                  icon={<FiLogOut />}
                  variant="ghost"
                  onClick={handleLogout}
                />
              </Tooltip>
            </HStack>
          </HStack>
        </Box>
      </Box>

      <Box maxW="1200px" mx="auto" px={6} py={8}>
        {children}
      </Box>
    </Box>
  )
}

