const base = import.meta.env.BASE_URL || '/'

export const dataUrl = (path: string) => `${base}data/${path}`.replace(/\/{2,}/g, '/')

export async function fetchData<T>(path: string): Promise<T> {
  const res = await fetch(dataUrl(path))
  if (!res.ok) throw new Error(`Failed to load ${path}: ${res.status}`)
  return res.json() as Promise<T>
}
