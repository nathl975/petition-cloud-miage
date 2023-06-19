package com.tinypet.dao;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.cloud.datastore.*;
import com.googlecode.objectify.ObjectifyService;
import com.tinypet.model.Signature;
import com.tinypet.model.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class UserDao {
    private static final String CLIENT_ID = "545960234174-ji3pbb20h0g5hq9ui28a1u0oui4htcjv.apps.googleusercontent.com";

    public void save(User user) {
        ObjectifyService.ofy().save().entity(user).now();
    }

    public User load(Long id) {
        return ObjectifyService.ofy().load().type(User.class).id(id).now();
    }

    public void delete(Long id) {
        ObjectifyService.ofy().delete().type(User.class).id(id).now();
    }

    public User getUser(String userId) {
        return ObjectifyService.ofy().load().type(User.class).id(userId).now();
    }

    public User createUser(User user) {
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        Transaction txn = datastore.newTransaction();

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(user.getId());
            Entity queryResult = txn.get(userKey);

            if (queryResult != null) {
                txn.rollback();
                return null;
            }

            FullEntity userEntity = Entity.newBuilder(userKey)
                    .set("id", user.getId())
                    .set("name", user.getName())
                    .build();

            txn.put(userEntity);
            txn.commit();

            return user;
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }


    public User validateIdToken(HttpServletRequest req) throws IOException {
        String idTokenString = req.getHeader("Authorization").substring("Bearer ".length());
        GoogleIdToken idToken = null;

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException e) {
            throw new IOException("Failed to verify token", e);
        }
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
            String email = payload.getEmail();
            User user = getUserById(userId);
            if (user == null) {
                user = createUser(new User(userId, email));
            }
            return user;
        }
        return null;
    }


    public User getUserById(String userId) {
        return ObjectifyService.ofy().load().type(User.class).id(userId).now();
    }


    public List<Signature> getSignaturesSortedByDate(String userId) {
        return ObjectifyService.ofy().load().type(Signature.class)
                .filter("user =", userId).order("-date").list();
    }
}