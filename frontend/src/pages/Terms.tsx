import { VStack, Heading, Text, Box } from '@chakra-ui/react'
import { Layout } from '@/components/layout/Layout'

export const Terms = () => {
  return (
    <Layout>
      <VStack align="stretch" spacing={8}>
        <Heading fontSize="36px">Terms of Use</Heading>

        <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
          Last updated: March 20, 2026
        </Text>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Acceptance of Terms
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            By using this website, you agree to these terms. If you do not agree, please do not use
            the site.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Content and Intellectual Property
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            Unless otherwise noted, all content on this site is owned by the author. You may
            share links and short excerpts with attribution, but you may not republish full content
            without prior permission.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Educational Purpose
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            Content is shared for educational and informational purposes. It does not constitute
            professional, legal, financial, or other formal advice.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            External Links
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            This site may link to external websites for reference. We are not responsible for their
            content, policies, or practices.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Limitation of Liability
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            The site is provided as is, without warranties of any kind. We are not liable for any
            damages resulting from your use of the site.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Changes to These Terms
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            These terms may be updated from time to time. Continued use of the site after updates
            means you accept the revised terms.
          </Text>
        </Box>
      </VStack>
    </Layout>
  )
}
