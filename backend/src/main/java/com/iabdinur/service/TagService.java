package com.iabdinur.service;

import com.iabdinur.dao.TagDao;
import com.iabdinur.dto.CreateTagRequest;
import com.iabdinur.dto.TagDTO;
import com.iabdinur.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TagService {
    private final TagDao tagDao;
    private final JdbcTemplate jdbcTemplate;

    public TagService(TagDao tagDao, JdbcTemplate jdbcTemplate) {
        this.tagDao = tagDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TagDTO> getAllTags() {
        List<Tag> tags = tagDao.selectAllTags();
        return tags.stream()
            .map(TagDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public Optional<TagDTO> getTagBySlug(String slug) {
        return tagDao.selectTagBySlug(slug)
            .map(TagDTO::fromEntity);
    }

    public List<TagDTO> searchTags(String query) {
        // Manual search implementation
        var sql = """
                SELECT id, name, slug, description, posts_count, created_at, updated_at
                FROM tags
                WHERE LOWER(name) LIKE LOWER(CONCAT('%', ?, '%'))
                   OR LOWER(slug) LIKE LOWER(CONCAT('%', ?, '%'))
                   OR LOWER(description) LIKE LOWER(CONCAT('%', ?, '%'))
                ORDER BY name
                """;
        List<Tag> tags = jdbcTemplate.query(sql,
            (rs, rowNum) -> {
                Tag tag = new Tag(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("slug"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime()
                );
                tag.setDescription(rs.getString("description"));
                tag.setPostsCount(rs.getInt("posts_count"));
                return tag;
            },
            query, query, query);
        return tags.stream()
            .map(TagDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public TagDTO createTag(CreateTagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.name());
        tag.setSlug(request.slug());
        tag.setDescription(request.description());
        tag.setPostsCount(0);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        
        tagDao.insertTag(tag);
        return TagDTO.fromEntity(tag);
    }

    @Transactional
    public Optional<TagDTO> updateTag(String slug, CreateTagRequest request) {
        Optional<Tag> tagOpt = tagDao.selectTagBySlug(slug);
        if (tagOpt.isEmpty()) {
            return Optional.empty();
        }

        Tag tag = tagOpt.get();
        tag.setName(request.name());
        tag.setSlug(request.slug());
        tag.setDescription(request.description());
        tag.setUpdatedAt(LocalDateTime.now());

        tagDao.updateTag(tag);
        return Optional.of(TagDTO.fromEntity(tag));
    }

    @Transactional
    public boolean deleteTag(String slug) {
        Optional<Tag> tagOpt = tagDao.selectTagBySlug(slug);
        if (tagOpt.isEmpty()) {
            return false;
        }
        tagDao.deleteTagById(tagOpt.get().getId());
        return true;
    }
}

