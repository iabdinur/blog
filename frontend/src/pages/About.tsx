import { VStack, Heading, Text, Box, Link, HStack, Icon, Input, Button } from '@chakra-ui/react'
import { Layout } from '@/components/layout/Layout'
import { FaLinkedin, FaGithub, FaEnvelope, FaGlobe } from 'react-icons/fa'

export const About = () => {
  return (
    <Layout>
      <VStack align="stretch" spacing={8}>
        <Heading fontSize="36px">Me</Heading>
        
        <Text fontSize="18px" lineHeight="tall">
          I am Ibrahim Abdinur. I am a software engineer who likes building practical systems and writing about what I learn along the way.
        </Text>
        
        <Text fontSize="18px" lineHeight="tall">
          This blog is where I share notes from projects, coursework, and the parts of software engineering that only start to make sense once you have broken them a few times and fixed them again.
        </Text>
        
        <Text fontSize="18px" lineHeight="tall">
          If you want to connect, reach out on LinkedIn.
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
          <VStack align="stretch" spacing={2}>
            <Text fontSize="18px">• Backend engineering notes in plain language</Text>
            <Text fontSize="18px">• Java and Spring Boot writeups</Text>
            <Text fontSize="18px">• Cloud and DevOps workflows that actually ship software</Text>
            <Text fontSize="18px">• System design fundamentals and patterns</Text>
            <Text fontSize="18px">• Interview preparation when I am in that season</Text>
            <Text fontSize="18px">• UIUC Master of Computer Science reflections and course reviews</Text>
          </VStack>
          <Text mt={4} fontSize="18px" fontWeight="bold">
            No fluff. No recycled motivation posts. Just work I can stand behind.
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
            My interest in software started early and stuck. I began with beginner friendly tools and small projects, then kept following the curiosity into deeper concepts: data structures, APIs, distributed systems, and cloud infrastructure.
          </Text>
          <Text mt={4} fontSize="18px">
            Over time, I learned that the fastest way to grow is to build in public, write down what you learned, and tighten your thinking until you can explain it clearly. That is the habit behind this site.
          </Text>
        </Box>

        <Box 
          borderTop="1px" 
          borderColor="gray.200" 
          _dark={{ borderColor: 'gray.700' }}
        />

        <Box>
          <Heading fontSize="24px" mb={4}>
            What I am building now
          </Heading>
          <VStack align="stretch" spacing={2}>
            <Text fontSize="18px">• Writing more consistently</Text>
            <Text fontSize="18px">• Strengthening my backend and cloud depth</Text>
            <Text fontSize="18px">• Documenting what I learn in the UIUC MCS program</Text>
            <Text fontSize="18px">• Building projects that are simple, usable, and finished</Text>
          </VStack>
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
            When I am not coding, I am usually doing something active or hands on. I like staying outdoors, training, reading, traveling when I can, and cooking.
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
            If you want the new posts in one place, subscribe. Every time I publish, you will get it in your inbox.
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