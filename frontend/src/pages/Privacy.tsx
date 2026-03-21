import { VStack, Heading, Text, Box } from '@chakra-ui/react'
import { Layout } from '@/components/layout/Layout'

export const Privacy = () => {
  return (
    <Layout>
      <VStack align="stretch" spacing={8}>
        <Heading fontSize="36px">Privacy Policy</Heading>

        <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
          Last updated: March 20, 2026
        </Text>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Information We Collect
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            We collect only what we need to run this site responsibly: your email address if you
            subscribe to the newsletter, and basic analytics such as pages visited, browser type,
            and referral source.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            How We Use Information
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            We use this information to send newsletter updates you asked for, improve the website
            experience, and better understand which topics are most useful to readers.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Newsletter and Email
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            If you subscribe, you may receive email updates when new posts are published. You can
            unsubscribe at any time using the link included in every email.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Data Sharing
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            We do not sell your personal information. We may use trusted service providers for
            hosting, analytics, and newsletter delivery, only to support this site.
          </Text>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={4}>
            Your Rights
          </Heading>
          <Text fontSize="18px" lineHeight="tall">
            You can request access to, correction of, or deletion of your personal data by reaching
            out through the contact details on this site.
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
