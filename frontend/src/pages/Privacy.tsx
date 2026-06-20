import { VStack, Heading, Text, Box } from '@chakra-ui/react'
import { Layout } from '@/components/layout/Layout'

export const Privacy = () => {
  return (
    <Layout>
      <VStack align="stretch" spacing={8}>
        <Heading fontSize="36px">Privacy Policy</Heading>

        <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
          Last updated: June 20, 2026
        </Text>

        <Box>
          <Heading fontSize="24px" mb={4}>
            The Short Version
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            This is a static website with no backend, no database, and no user accounts. It does
            not ask for your email, and it has no server of its own that collects or stores
            personal information about you.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Hosting
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            The site is hosted on GitHub Pages. As with any web host, GitHub processes basic
            request data such as your IP address to deliver the pages and protect the service.
            That processing is governed by GitHub's privacy statement, not this one.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Analytics
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            If analytics are enabled, the site uses GoatCounter, a privacy-friendly service that
            counts page views without cookies and without tracking you across other websites. It
            uses your IP address and browser type only momentarily to count unique visits, then
            discards them, and stores no personal data.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Comments
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            If comments are enabled, they are powered by Giscus and stored as GitHub Discussions.
            Leaving a comment requires signing in with a GitHub account, and your comments are
            public. This is governed by GitHub's and Giscus's privacy practices.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Information Stored In Your Browser
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            The site saves your theme preference (light or dark) and your reading list in your
            browser's local storage. This data stays on your device, is never sent to any server,
            and you can clear it at any time through your browser.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Data Sharing
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            Your personal information is never sold. The site itself holds no personal data to
            share; the only third parties involved are the hosting, analytics, and comment
            services described above, used solely to run the site.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Contact
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            If you have privacy questions, please reach out through the contact methods listed on
            the About page.
          </Text>
        </Box>
      </VStack>
    </Layout>
  )
}
