import { Box, useColorModeValue } from '@chakra-ui/react'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { vscDarkPlus, vs } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { getLanguageFromExtension } from '@/utils/codeHighlight'

export interface CodeBlockProps {
  code: string
  language?: string
  filename?: string
  showLineNumbers?: boolean
}

export const CodeBlock = ({ code, language, filename, showLineNumbers = true }: CodeBlockProps) => {
  const theme = useColorModeValue(vs, vscDarkPlus)
  const detectedLanguage = language || (filename ? getLanguageFromExtension(filename) : 'text')

  return (
    <Box
      borderRadius="md"
      overflow="hidden"
      my={4}
      boxShadow="md"
    >
      {filename && (
        <Box
          bg={useColorModeValue('gray.100', 'gray.700')}
          px={4}
          py={2}
          fontSize="sm"
          fontWeight="medium"
        >
          {filename}
        </Box>
      )}
      <SyntaxHighlighter
        language={detectedLanguage}
        style={theme}
        showLineNumbers={showLineNumbers}
        customStyle={{
          margin: 0,
          padding: '1rem',
          fontSize: '0.875rem',
        }}
      >
        {code}
      </SyntaxHighlighter>
    </Box>
  )
}

