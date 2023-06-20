package com.tinypet.dao;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.tinypet.model.Petition;
import com.tinypet.model.Signature;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignatureDao {

    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private final UserDao userDao = new UserDao();


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

        if (existingSignature != null) {
            return null;
        }

        com.googlecode.objectify.Key<Signature> key = ObjectifyService.ofy().save().entity(signature).now();

        executor.execute(() -> {
            try (Closeable closeable = ObjectifyService.begin()) {
                incrementPetitionSignatureCount(signature.getPetition());
                userDao.signPetition(userDao.getUser(signature.getUser()), signature.getPetition());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return key;
    }


    private void incrementPetitionSignatureCount(Long petitionId) {
        Petition petition = ObjectifyService.ofy().load().type(Petition.class).id(petitionId).now();
        petition.setSignatureCount(petition.getSignatureCount() + 1);
        ObjectifyService.ofy().save().entity(petition).now();
    }

    public List<Signature> getSignaturesByUser(String userId) {
        return ObjectifyService.ofy().load().type(Signature.class).filter("user", userId).list();
    }

    public List<Signature> getSignaturesByPetition(Long petitionId) {
        Query<Signature> q = ObjectifyService.ofy().load().type(Signature.class).filter("petition", petitionId);
        return q.list();
    }

}