import { Routes, Route, Navigate } from 'react-router-dom'
import { Home } from './pages/Home'
import { Blog } from './pages/Blog'
import { Post } from './pages/Post'
import { Author } from './pages/Author'
import { Series } from './pages/Series'
import { Search } from './pages/Search'
import { Newsletter } from './pages/Newsletter'
import { About } from './pages/About'
import { NotFound } from './pages/NotFound'
import { Profile } from './pages/Profile'
import { AdminLogin } from './pages/author/Login'
import { PostsList } from './pages/author/PostsList'
import { PostForm } from './pages/author/PostForm'
import { TagsList } from './pages/author/TagsList'
import { DraftsList } from './pages/author/DraftsList'
import { TagForm } from './pages/author/TagForm'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/archive" element={<Blog />} />
      <Route path="/post/:slug" element={<Post />} />
      <Route path="/author" element={<Author />} />
      <Route path="/series/:slug" element={<Series />} />
      <Route path="/search" element={<Search />} />
      <Route path="/newsletter" element={<Newsletter />} />
      <Route path="/about" element={<About />} />
      <Route path="/profile" element={<Profile />} />
      {/* Login route - works for both Authors and Readers */}
      <Route path="/login" element={<AdminLogin />} />
      {/* Authors Panel routes */}
      <Route path="/authors" element={<Navigate to="/authors/posts" replace />} />
      <Route path="/authors/posts" element={<PostsList />} />
      <Route path="/authors/posts/new" element={<PostForm />} />
      <Route path="/authors/posts/edit/:slug" element={<PostForm />} />
      <Route path="/authors/drafts" element={<DraftsList />} />
      <Route path="/authors/tags" element={<TagsList />} />
      <Route path="/authors/tags/new" element={<TagForm />} />
      <Route path="/authors/tags/edit/:slug" element={<TagForm />} />
      {/* Legacy admin routes - redirect to authors */}
      <Route path="/admin" element={<Navigate to="/authors/posts" replace />} />
      <Route path="/admin/login" element={<Navigate to="/login" replace />} />
      <Route path="/admin/*" element={<Navigate to="/authors/posts" replace />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App

