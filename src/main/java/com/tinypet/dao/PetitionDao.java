package com.tinypet.dao;

import com.googlecode.objectify.ObjectifyService;
import com.tinypet.model.Petition;
import com.tinypet.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class PetitionDao {
    private final UserDao userDao = new UserDao();

    public void save(Petition petition) {
        ObjectifyService.ofy().save().entity(petition).now();
    }

    public Petition load(Long id) {
        return ObjectifyService.ofy().load().type(Petition.class).id(id).now();
    }

    public void delete(Long id) {
        ObjectifyService.ofy().delete().type(Petition.class).id(id).now();
    }

    public List<Petition> getTop100Petitions() {
        return ObjectifyService.ofy().load().type(Petition.class)
                .order("-date").limit(100).list();
    }

    public Petition getPetition(Long id) {
        return ObjectifyService.ofy().load().type(Petition.class).id(id).now();
    }

    public com.googlecode.objectify.Key<Petition> createPetition(Petition petition) {
        return ObjectifyService.ofy().save().entity(petition).now();
    }

    public List<Petition> getPetitionsByTag(String tagId) {
        return ObjectifyService.ofy().load().type(Petition.class).filter("tags", tagId).list();
    }

    public List<Petition> getSignedPetitionsByUser(String userId) {
        User user = userDao.getUser(userId);
        return user.getSignedPetitions().stream()
                .map(this::load)
                .collect(Collectors.toList());
    }

}