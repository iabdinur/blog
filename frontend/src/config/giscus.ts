// Giscus comments configuration.
// Fill these in from https://giscus.app after enabling Discussions on the repo
// and installing the giscus GitHub App. Comments are hidden until repo/repoId are set.
export const giscusConfig = {
  repo: import.meta.env.VITE_GISCUS_REPO ?? '',
  repoId: import.meta.env.VITE_GISCUS_REPO_ID ?? '',
  category: import.meta.env.VITE_GISCUS_CATEGORY ?? 'Comments',
  categoryId: import.meta.env.VITE_GISCUS_CATEGORY_ID ?? '',
}
