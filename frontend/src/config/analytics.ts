// GoatCounter analytics (privacy-friendly, optional).
// Set VITE_GOATCOUNTER_CODE to your site code ("myblog") or full URL to enable.
// When empty, tracking and view counts are silently disabled.
const raw = (import.meta.env.VITE_GOATCOUNTER_CODE ?? '').trim()

export const goatcounterEndpoint = raw
  ? raw.startsWith('http')
    ? raw.replace(/\/$/, '')
    : `https://${raw}.goatcounter.com`
  : ''

export const analyticsEnabled = goatcounterEndpoint !== ''
