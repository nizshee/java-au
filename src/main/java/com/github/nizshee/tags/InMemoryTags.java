package com.github.nizshee.tags;


import com.github.nizshee.exception.TagException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTags implements Tags, Serializable {

    private final Map<String, String> tags;
    private String name;

    public InMemoryTags() {
        this.tags = new HashMap<>();
    }

    @Override
    public void create(String name, String hash) throws TagException {
        if (tags.containsKey(name)) throw new TagException("Name already exists.");
        tags.put(name, hash);
    }

    @Override
    public String getHash(String maybeName) throws TagException {
        if (tags.containsKey(maybeName)) {
            return tags.get(maybeName);
        } else return maybeName;
    }

    @Override
    public void changeCurrent(String hash) throws TagException {
        if (name != null) {
            tags.put(name, hash);
        }
    }

    @Override
    public String current() throws TagException {
        return name == null ? "no name" : name;
    }

    @Override
    public void setCurrent(String maybeName) throws TagException {
        if (tags.containsKey(maybeName)) {
            name = maybeName;
        } else {
            name = null;
        }
    }

    @Override
    public void remove(String name) throws TagException {
        if (!tags.containsKey(name)) throw new TagException("Tag not found,");
        tags.remove(name);
    }
}
