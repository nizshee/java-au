package com.github.nizshee.tags;


import com.github.nizshee.exception.TagException;

/**
 * Stores names for identificators.
 */
public interface Tags {

    void create(String name, String hash) throws TagException;

    String current() throws TagException;

    void changeCurrent(String hash) throws TagException;

    void setCurrent(String maybeName) throws TagException;

    String getHash(String maybeName) throws TagException;

    void remove(String name) throws TagException;
}
