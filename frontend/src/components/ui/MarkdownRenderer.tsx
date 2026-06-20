import { Box, useColorModeValue } from '@chakra-ui/react'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import rehypeRaw from 'rehype-raw'
import rehypeSanitize from 'rehype-sanitize'
import { CodeBlock } from './CodeBlock'

export interface MarkdownRendererProps {
  content: string
}

export const MarkdownRenderer = ({ content }: MarkdownRendererProps) => {
  const codeBg = useColorModeValue('gray.50', 'gray.800')

  return (
    <Box
      className="markdown-content"
      sx={{
        '& h1, & h2, & h3, & h4, & h5, & h6': {
          fontWeight: 'bold',
          mt: 6,
          mb: 4,
        },
        '& h1': { fontSize: '2xl' },
        '& h2': { fontSize: 'xl' },
        '& h3': { fontSize: 'lg' },
        '& p': {
          mb: 4,
          lineHeight: 1.8,
        },
        '& ul, & ol': {
          mb: 4,
          pl: 6,
        },
        '& li': {
          mb: 2,
        },
        '& blockquote': {
          borderLeft: '4px solid',
          borderColor: 'brand.500',
          pl: 4,
          py: 2,
          my: 4,
          bg: codeBg,
          fontStyle: 'italic',
        },
        '& code': {
          bg: codeBg,
          px: 2,
          py: 1,
          borderRadius: 'sm',
          fontSize: '0.875em',
        },
        '& pre': {
          mb: 4,
        },
        '& a': {
          color: 'brand.500',
          textDecoration: 'underline',
        },
        '& img': {
          maxW: '100%',
          borderRadius: 'md',
          my: 4,
          display: 'block',
          mx: 'auto',
          boxShadow: 'md',
          _hover: {
            boxShadow: 'lg',
            transform: 'scale(1.01)',
            transition: 'all 0.2s',
          },
        },
        '& table': {
          width: '100%',
          borderCollapse: 'collapse',
          my: 4,
        },
        '& th, & td': {
          border: '1px solid',
          borderColor: 'gray.300',
          _dark: { borderColor: 'gray.600' },
          px: 4,
          py: 2,
        },
        '& th': {
          bg: codeBg,
          fontWeight: 'bold',
        },
      }}
    >
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        rehypePlugins={[rehypeRaw, rehypeSanitize]}
        components={{
          code({ node, inline, className, children, ...props }: any) {
            const match = /language-(\w+)/.exec(className || '')
            const codeString = String(children).replace(/\n$/, '')
            
            return !inline && match ? (
              <CodeBlock code={codeString} language={match[1]} />
            ) : (
              <code className={className} {...props}>
                {children}
              </code>
            )
          },
        }}
      >
        {content}
      </ReactMarkdown>
    </Box>
  )
}

