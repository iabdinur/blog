package com.iabdinur.service;

import com.iabdinur.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final PostService postService;
    private final AuthorService authorService;
    private final TagService tagService;

    public SearchService(PostService postService,
                        AuthorService authorService,
                        TagService tagService) {
        this.postService = postService;
        this.authorService = authorService;
        this.tagService = tagService;
    }

    public SearchResponse search(String query, String type, Integer limit) {
        List<PostDTO> posts = new ArrayList<>();
        List<AuthorDTO> authors = new ArrayList<>();
        List<TagDTO> tags = new ArrayList<>();

        if (type == null || type.equals("all") || type.equals("posts")) {
            PostListResponse postResponse = postService.searchPosts(query, 1, limit != null ? limit : 10);
            posts = postResponse.posts();
        }

        if (type == null || type.equals("all") || type.equals("authors")) {
            authors = authorService.searchAuthors(query);
            if (limit != null && authors.size() > limit) {
                authors = authors.subList(0, limit);
            }
        }

        if (type == null || type.equals("all") || type.equals("tags")) {
            tags = tagService.searchTags(query);
            if (limit != null && tags.size() > limit) {
                tags = tags.subList(0, limit);
            }
        }

        int total = posts.size() + authors.size() + tags.size();

        return new SearchResponse(posts, authors, tags, total);
    }
}

