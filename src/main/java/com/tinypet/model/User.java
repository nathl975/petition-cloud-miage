package com.tinypet.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    String userId;
    @Index
    String name;
    Set<Long> signedPetitions = new HashSet<>();

    protected User() {}
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getId() {
        return userId;
    }

    public void setId(String id) {
        this.userId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getSignedPetitions() {
        return signedPetitions;
    }

    public void addSignedPetition(Long petitionId) {
        this.signedPetitions.add(petitionId);
    }

    public void setSignedPetitions(Set<Long> signedPetitions) {
        this.signedPetitions = signedPetitions;
    }
}
