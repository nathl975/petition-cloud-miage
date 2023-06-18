package com.tinypet.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import java.util.Date;

@Entity
public class Signature {
    @Id
    Long id;
    @Index
    String user; // Use user ID as the signer
    @Index
    Long petition; // Use petition ID as the signed petition
    @Index
    Date date;

    protected Signature() {}
    public Signature(Long id, String user, Long petition) {
        this.id = id;
        this.user = user;
        this.petition = petition;
        this.date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getPetition() {
        return petition;
    }

    public void setPetition(Long petition) {
        this.petition = petition;
    }

    public Date getDate() {
        return date;
    }
}
