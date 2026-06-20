import { Routes, Route } from 'react-router-dom'
import { Home } from './pages/Home'
import { Blog } from './pages/Blog'
import { Post } from './pages/Post'
import { Author } from './pages/Author'
import { Series } from './pages/Series'
import { Search } from './pages/Search'
import { About } from './pages/About'
import { Privacy } from './pages/Privacy'
import { Terms } from './pages/Terms'
import { NotFound } from './pages/NotFound'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/archive" element={<Blog />} />
      <Route path="/post/:slug" element={<Post />} />
      <Route path="/author" element={<Author />} />
      <Route path="/series/:slug" element={<Series />} />
      <Route path="/search" element={<Search />} />
      <Route path="/about" element={<About />} />
      <Route path="/privacy" element={<Privacy />} />
      <Route path="/terms" element={<Terms />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App
