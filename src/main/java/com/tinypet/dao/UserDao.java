package com.tinypet.dao;

import com.googlecode.objectify.ObjectifyService;
import com.tinypet.model.Signature;
import com.tinypet.model.User;

import java.util.List;

public class UserDao {
    public void save(User user) {
        ObjectifyService.ofy().save().entity(user).now();
    }

    public User load(Long id) {
        return ObjectifyService.ofy().load().type(User.class).id(id).now();
    }

    public void delete(Long id) {
        ObjectifyService.ofy().delete().type(User.class).id(id).now();
    }

    public List<Signature> getSignaturesSortedByDate(String userId) {
        return ObjectifyService.ofy().load().type(Signature.class)
                .filter("user =", userId).order("-date").list();
    }
}