import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'
import { analyticsEnabled, goatcounterEndpoint } from '@/config/analytics'

declare global {
  interface Window {
    goatcounter?: { count?: (opts: { path: string }) => void; no_onload?: boolean }
  }
}

// Loads GoatCounter (when configured) and records a pageview on each SPA route change.
export const Analytics = () => {
  const location = useLocation()

  useEffect(() => {
    if (!analyticsEnabled || document.getElementById('goatcounter-script')) return
    window.goatcounter = { no_onload: true }
    const script = document.createElement('script')
    script.id = 'goatcounter-script'
    script.async = true
    script.src = '//gc.zgo.at/count.js'
    script.setAttribute('data-goatcounter', `${goatcounterEndpoint}/count`)
    script.onload = () => window.goatcounter?.count?.({ path: location.pathname })
    document.body.appendChild(script)
    // location.pathname intentionally read once on mount; route changes handled below.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => {
    if (!analyticsEnabled) return
    window.goatcounter?.count?.({ path: location.pathname })
  }, [location.pathname])

  return null
}
