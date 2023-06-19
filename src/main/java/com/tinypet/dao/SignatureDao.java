package com.tinypet.dao;

import com.google.cloud.datastore.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.tinypet.model.Signature;

import java.util.List;

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

    public com.googlecode.objectify.Key<Signature> createSignature(Signature signature) {
        Signature existingSignature = ObjectifyService.ofy().load().type(Signature.class)
                .filter("user", signature.getUser())
                .filter("petition", signature.getPetition())
                .first().now();

        // If the user has already signed the petition, return the existing signature
        if (existingSignature != null) {
            return null;
        }

        // If the user hasn't signed the petition yet, save the new signature
        return ObjectifyService.ofy().save().entity(signature).now();    }

    public List<Signature> getSignaturesByUser(String userId) {
        return ObjectifyService.ofy().load().type(Signature.class).filter("user", userId).list();
    }

    public List<Signature> getSignaturesByPetition(Long petitionId) {
        Query<Signature> q = ObjectifyService.ofy().load().type(Signature.class).filter("petition", petitionId);
        return q.list();
    }
}