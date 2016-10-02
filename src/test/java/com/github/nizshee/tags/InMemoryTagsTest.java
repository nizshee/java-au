package com.github.nizshee.tags;

import org.junit.Test;
import static org.junit.Assert.*;

public class InMemoryTagsTest {

    @Test
    public void createTest() throws Exception {
        Tags tags = new InMemoryTags();
        tags.create("a", "1");

        assertEquals(tags.getHash("a"), "1");
    }

    @Test
    public void getHashTest() throws Exception {
        Tags tags = new InMemoryTags();
        tags.create("a", "1");
        tags.create("b", "2");

        assertEquals("1", tags.getHash("a"));
        assertEquals("2", tags.getHash("b"));
        assertEquals("2", tags.getHash("2"));
        assertEquals("3", tags.getHash("3"));
    }

    @Test
    public void changeTest() throws Exception {
        Tags tags = new InMemoryTags();
        tags.create("a", "1");
        tags.setCurrent("a");

        tags.changeCurrent("2");

        assertEquals("2", tags.getHash("a"));
    }

    @Test
    public void currentTest() throws Exception {
        Tags tags = new InMemoryTags();
        tags.create("a", "1");
        tags.setCurrent("a");

        assertEquals("a", tags.current());
    }
}
