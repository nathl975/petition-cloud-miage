package com.tinypet.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    Long id;
    @Index
    String name;
    Set<Long> signedPetitions = new HashSet<>();

    protected User() {}
    public User(Long id, String name, Set<Long> signedPetitions) {
        this.id = id;
        this.name = name;
        this.signedPetitions = signedPetitions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setSignedPetitions(Set<Long> signedPetitions) {
        this.signedPetitions = signedPetitions;
    }
}
