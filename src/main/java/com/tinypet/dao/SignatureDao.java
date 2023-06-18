package com.tinypet.dao;

import com.googlecode.objectify.ObjectifyService;
import com.tinypet.model.Signature;

public class SignatureDao {
    public void save(Signature signature) {
        ObjectifyService.ofy().save().entity(signature).now();
    }

    public Signature load(Long id) {
        return ObjectifyService.ofy().load().type(Signature.class).id(id).now();
    }

    public void delete(Long id) {
        ObjectifyService.ofy().delete().type(Signature.class).id(id).now();
    }
}