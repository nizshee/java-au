package com.github.nizshee.tags;


import com.github.nizshee.exception.TagException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTags implements Tags, Serializable {

    private final Map<String, String> tags;
    private String current;
    private String name;

    public InMemoryTags() {
        this.tags = new HashMap<>();
    }

    public InMemoryTags(Map<String, String> tags, String current) {
        this.tags = tags;
        this.current = current;
    }

    @Override
    public void create(String name, String hash) throws TagException {
        if (tags.containsKey(name)) throw new TagException("Name already exists.");
        tags.put(name, hash);
    }

    @Override
    public String getHash(String name) throws TagException {
        if (tags.containsKey(name)) {
            return tags.get(name);
        } else return name;

    }

    @Override
    public void changeCurrent(String hash) throws TagException {
        if (name != null) {
            current = hash;
            tags.put(name, current);
        } else {
            current = hash;
        }
    }

    @Override
    public String currentName() throws TagException {
        return name == null ? "no name" : name;
    }

    @Override
    public String currentHash() throws TagException {
        return current;
    }

    @Override
    public void setCurrent(String hashOrName) throws TagException {
        if (tags.containsKey(hashOrName)) {
            name = hashOrName;
            current = tags.get(hashOrName);
        } else {
            name = null;
            current = hashOrName;
        }
    }

    @Override
    public void remove(String name) throws TagException {
        if (!tags.containsKey(name)) throw new TagException("Tag not found,");
        tags.remove(name);
    }
}
