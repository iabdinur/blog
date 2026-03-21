import { VStack, Heading, Text, Box, Link, HStack, Icon, Input, Button, UnorderedList, ListItem } from '@chakra-ui/react'
import { Layout } from '@/components/layout/Layout'
import { FaLinkedin, FaGithub, FaEnvelope, FaGlobe } from 'react-icons/fa'

export const About = () => {
  return (
    <Layout>
      <VStack align="stretch" spacing={8}>
        <Heading fontSize="36px">Me</Heading>
        
        <Text fontSize="18px" lineHeight="tall">
          I am Ibrahim Abdinur, a software engineer and Master of Computer Science candidate committed to building practical, human-centered systems that create real value.
        </Text>
        
        <Text fontSize="18px" lineHeight="tall">
          This blog is a space to think and document what I build, especially the parts of software engineering that only start to make sense once you have broken them a few times and fixed them again.
        </Text>
        
        <Text fontSize="18px" lineHeight="tall">
          I care deeply about clear communication, accessible learning, and sharing knowledge in ways that help others grow with confidence.
        </Text>
        
        <Text fontSize="18px" lineHeight="tall">
          If you'd like to connect, reach out on LinkedIn.
        </Text>

        <Box 
          borderTop="1px" 
          borderColor="gray.200" 
          _dark={{ borderColor: 'gray.700' }}
        />

        <Box>
          <Heading fontSize="24px" mb={4}>
            What you will find here
          </Heading>
          <UnorderedList
            spacing={2}
            pl={5}
            ml={0}
            lineHeight="tall"
            sx={{
              '& > li': {
                fontSize: '18px',
                '&::marker': {
                  fontSize: '14px',
                },
              },
            }}
          >
            <ListItem>Backend engineering concepts</ListItem>
            <ListItem>Java and Spring Boot projects</ListItem>
            <ListItem>Cloud and DevOps workflows</ListItem>
            <ListItem>System design notes</ListItem>
            <ListItem>Reflections from the UIUC MCS journey</ListItem>
          </UnorderedList>
          <Text mt={4} fontSize="18px" fontWeight="bold">
            No fluff. Just thoughtful, practical writing with useful resources and projects I can stand behind.
          </Text>
        </Box>

        <Box 
          borderTop="1px" 
          borderColor="gray.200" 
          _dark={{ borderColor: 'gray.700' }}
        />

        <Box>
          <Heading fontSize="24px" mb={4}>
            My journey
          </Heading>
          <Text fontSize="18px">
            I came to software development by a nontraditional route. My academic background is in chemical engineering (MEng), and it taught me to approach complex problems with systems thinking, structure, and curiosity.
          </Text>
          <Text mt={4} fontSize="18px">
            The transition into software was not straightforward, but it was intentional. I carried forward an engineering mindset and built technical depth through steady practice, graduate study, and hands-on projects. Over time, I learned to build human-centered systems.
          </Text>
          <Text mt={4} fontSize="18px">
            Today, I focus on developing reliable systems with clarity and care. I also share what I learn so that others from nontraditional backgrounds can navigate their own path with less uncertainty.
          </Text>
        </Box>

        <Box 
          borderTop="1px" 
          borderColor="gray.200" 
          _dark={{ borderColor: 'gray.700' }}
        />

        <Box>
          <Heading fontSize="24px" mb={4}>
            Outside tech
          </Heading>
          <Text fontSize="18px">
            I enjoy staying active, spending time outdoors, reading, traveling when I can, and cooking.
          </Text>
        </Box>

        <Box 
          borderTop="1px" 
          borderColor="gray.200" 
          _dark={{ borderColor: 'gray.700' }}
        />

        <Box>
          <Heading fontSize="24px" mb={4}>
            Subscribe
          </Heading>
          <Text fontSize="18px">
            If these notes are useful to you, subscribe and I'll send new posts directly to your inbox.
          </Text>
          
          <HStack mt={4}>
            <Input 
              placeholder="Type your email..." 
              size="md"
              flex={1}
            />
            <Button 
              leftIcon={<FaEnvelope />} 
              bg="brand.600"
              color="white"
              _hover={{ bg: 'brand.700' }}
            >
              Subscribe
            </Button>
          </HStack>
          <Text mt={4} fontSize="sm" color="gray.600" textAlign="center">
            We'll never share your email. Unsubscribe at any time.
          </Text>
        </Box>

        <Box 
          borderTop="1px" 
          borderColor="gray.200" 
          _dark={{ borderColor: 'gray.700' }}
        />

        <Box>
          <Heading fontSize="24px" mb={4}>
            Links
          </Heading>
          <HStack spacing={4}>
            <Link href="https://github.com/iabdinur" isExternal>
              <Icon as={FaGithub} boxSize={6} />
            </Link>
            <Link href="https://www.linkedin.com/in/ibrahim-abdinur/" isExternal>
              <Icon as={FaLinkedin} boxSize={6} />
            </Link>
            <Link href="https://iabdinur.com" isExternal>
              <Icon as={FaGlobe} boxSize={6} />
            </Link>
          </HStack>
        </Box>
      </VStack>
    </Layout>
  )
}