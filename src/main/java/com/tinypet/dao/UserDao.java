package com.tinypet.dao;

import com.google.cloud.datastore.*;
import com.googlecode.objectify.ObjectifyService;
import com.tinypet.model.Signature;
import com.tinypet.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.util.List;

public class UserDao {
    private static final java.security.Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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


    public User getUserById(String userId) {
        return ObjectifyService.ofy().load().type(User.class).id(userId).now();
    }

    public User validateCredential(String jwtToken) {

        String userId = getVerifiedIdFromCredential(jwtToken);

        if (userId == null) {
            return null;
        }
        return getUser(userId);
    }

    private String getVerifiedIdFromCredential(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);

            return claims.getBody().getSubject();
        } catch (JwtException e) {
            System.out.println(e +"Invalid JWT token");
            return null;
        }
    }

    public static String createJwt(User user) {
        return Jwts.builder()
                .setSubject(user.getId())
                .signWith(key)
                .compact();
    }
    public List<Signature> getSignaturesSortedByDate(String userId) {
        return ObjectifyService.ofy().load().type(Signature.class)
                .filter("user =", userId).order("-date").list();
    }

    public void signPetition(User user, Long petitionId) {
        user.addSignedPetition(petitionId);
        save(user);
    }
}