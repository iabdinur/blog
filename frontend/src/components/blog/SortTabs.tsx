import { HStack, Button } from '@chakra-ui/react'

export type SortValue = 'latest' | 'discussions'

export interface SortTabsProps {
  value: SortValue
  onChange: (value: SortValue) => void
}

const TABS: { value: SortValue; label: string }[] = [
  { value: 'latest', label: 'Latest' },
  { value: 'discussions', label: 'Discussions' },
]

export const SortTabs = ({ value, onChange }: SortTabsProps) => {
  return (
    <HStack
      spacing={0}
      border="1px solid"
      borderColor="gray.200"
      borderRadius="md"
      w="fit-content"
      _dark={{ borderColor: 'gray.600' }}
    >
      {TABS.map((tab, i) => {
        const active = value === tab.value
        return (
          <Button
            key={tab.value}
            variant={active ? 'solid' : 'ghost'}
            size="sm"
            onClick={() => onChange(tab.value)}
            bg={active ? 'gray.100' : 'transparent'}
            color={active ? 'gray.900' : 'gray.600'}
            border="none"
            _dark={{
              bg: active ? 'gray.700' : 'transparent',
              color: active ? 'gray.100' : 'gray.400',
            }}
            _hover={{
              bg: active ? 'gray.100' : 'gray.50',
              _dark: { bg: active ? 'gray.700' : 'gray.800' },
            }}
            borderLeftRadius={i === 0 ? 'md' : '0'}
            borderRightRadius={i === TABS.length - 1 ? 'md' : '0'}
            fontWeight="normal"
          >
            {tab.label}
          </Button>
        )
      })}
    </HStack>
  )
}
