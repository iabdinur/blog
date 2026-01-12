package com.iabdinur.dao;

import com.iabdinur.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    List<Tag> selectAllTags();
    Optional<Tag> selectTagById(Long tagId);
    Optional<Tag> selectTagBySlug(String slug);
    void insertTag(Tag tag);
    boolean existsTagWithSlug(String slug);
    boolean existsTagWithName(String name);
    boolean existsTagById(Long tagId);
    void deleteTagById(Long tagId);
    void updateTag(Tag update);
}
