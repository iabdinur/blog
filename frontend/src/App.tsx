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
import { AdminLogin } from './pages/admin/Login'
import { PostsList } from './pages/admin/PostsList'
import { PostForm } from './pages/admin/PostForm'
import { AuthorsList } from './pages/admin/AuthorsList'
import { AuthorForm } from './pages/admin/AuthorForm'
import { TagsList } from './pages/admin/TagsList'
import { TagForm } from './pages/admin/TagForm'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/archive" element={<Blog />} />
      <Route path="/post/:slug" element={<Post />} />
      <Route path="/author/:username" element={<Author />} />
      <Route path="/series/:slug" element={<Series />} />
      <Route path="/search" element={<Search />} />
      <Route path="/newsletter" element={<Newsletter />} />
      <Route path="/about" element={<About />} />
      <Route path="/admin" element={<Navigate to="/admin/login" replace />} />
      <Route path="/admin/login" element={<AdminLogin />} />
      <Route path="/admin/posts" element={<PostsList />} />
      <Route path="/admin/posts/new" element={<PostForm />} />
      <Route path="/admin/posts/edit/:slug" element={<PostForm />} />
      <Route path="/admin/authors" element={<AuthorsList />} />
      <Route path="/admin/authors/new" element={<AuthorForm />} />
      <Route path="/admin/authors/edit/:username" element={<AuthorForm />} />
      <Route path="/admin/tags" element={<TagsList />} />
      <Route path="/admin/tags/new" element={<TagForm />} />
      <Route path="/admin/tags/edit/:slug" element={<TagForm />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App

