package com.tinypet.dao;

import com.googlecode.objectify.ObjectifyService;
import com.tinypet.model.Tag;

import java.util.List;

public class TagDao {
    public void save(Tag tag) {
        ObjectifyService.ofy().save().entity(tag).now();
    }

    public Tag load(Long id) {
        return ObjectifyService.ofy().load().type(Tag.class).id(id).now();
    }

    public void delete(Long id) {
        ObjectifyService.ofy().delete().type(Tag.class).id(id).now();
    }

    public Tag getTagById(String tagId) {
        return ObjectifyService.ofy().load().type(Tag.class).id(tagId).now();
    }

    public List<Tag> getAllTags() {
        return ObjectifyService.ofy().load().type(Tag.class).list();
    }
    public Tag getTagByName(String tagName) {
        return ObjectifyService.ofy().load().type(Tag.class).filter("tagName", tagName).first().now();
    }

}