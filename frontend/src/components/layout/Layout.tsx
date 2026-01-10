import { Box, Container, Flex, useColorModeValue } from '@chakra-ui/react'
import { ReactNode } from 'react'
import { Navbar } from './Navbar'
import { Footer } from './Footer'
import { Sidebar } from './Sidebar'
import { NewsletterPopup } from '../ui/NewsletterPopup'
import { SearchPopup } from '../ui/SearchPopup'

interface LayoutProps {
  children: ReactNode
  showSidebar?: boolean
}

export const Layout = ({ children, showSidebar = false }: LayoutProps) => {
  const bg = useColorModeValue('white', 'gray.900')

  return (
    <Box minH="100vh" display="flex" flexDirection="column" bg={bg}>
      <Navbar />
      <Box flex="1">
        <Container maxW="680px" py={8} px={{ base: 4, md: 8 }}>
          {showSidebar ? (
            <Flex gap={8}>
              <Box flex="1">{children}</Box>
              <Box w="300px" display={{ base: 'none', lg: 'block' }}>
                <Sidebar />
              </Box>
            </Flex>
          ) : (
            children
          )}
        </Container>
      </Box>
      <Footer />
      <NewsletterPopup />
      <SearchPopup />
    </Box>
  )
}

