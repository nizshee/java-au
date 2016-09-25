package com.github.nizshee.tags;


import com.github.nizshee.exception.TagException;

/**
 * Stores names for identificators.
 */
public interface Tags {

    void create(String name, String hash) throws TagException;

    String getHash(String name) throws TagException;

    void changeCurrent(String hash) throws TagException;

    String currentName() throws TagException;

    String currentHash() throws TagException;

    void setCurrent(String hashOrName) throws TagException;

    void remove(String name) throws TagException;
}
