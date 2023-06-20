package com.tinypet.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Petition {
    @Id
    Long id;
    @Index
    String owner;
    @Index
    Date date;
    String body;
    String description;
    Set<Long> tags = new HashSet<>();
    Integer signatureCount;

    protected Petition() {}
    public Petition(Long id, String owner, String body, String description, Set<Long> tags) {
        this.id = id;
        this.owner = owner;
        this.body = body;
        this.description = description;
        this.tags = tags;
    }

    public int getSignatureCount() {
        return signatureCount;
    }

    public void setSignatureCount(int signatureCount) {
        this.signatureCount = signatureCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getTags() {
        return tags;
    }

    public void setTags(Set<Long> tags) {
        this.tags = tags;
    }


}