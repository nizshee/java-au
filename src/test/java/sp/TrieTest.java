package sp;
 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;
 
public class TrieTest {

    @org.junit.Test
    public void testSerialize() throws Exception {
        TrieImpl trie = new TrieImpl();
        TrieImpl other = new TrieImpl();

        other.add("");
        other.add("aa");

        trie.add("");
        trie.add("aa");

        assertTrue(trie.equals(other));

        trie.add("a");
        trie.add("aaa");
        trie.add("aab");
        trie.add("aac");
        trie.add("ab");

        assertFalse(trie.equals(other));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        trie.serialize(out);
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        other.deserialize(in);


        assertTrue(trie.equals(other));

    }

    @org.junit.Test
    public void testAdd() throws Exception {
        Trie trie = new TrieImpl();

        assertTrue(trie.add(""));
        assertTrue(trie.add("aa"));
        assertTrue(trie.add("a"));
        assertTrue(trie.add("aaa"));
        assertTrue(trie.add("aab"));
        assertTrue(trie.add("aac"));
        assertTrue(trie.add("ab"));
        
        assertFalse(trie.add(""));
        assertFalse(trie.add("aa"));
        assertFalse(trie.add("a"));
        assertFalse(trie.add("aaa"));
        assertFalse(trie.add("aab"));
        assertFalse(trie.add("aac"));
        assertFalse(trie.add("ab"));

        trie.remove("");
        trie.remove("aab");

        assertTrue(trie.add("aaaa"));
        assertFalse(trie.add("aaaa"));

        trie.remove("a");
        trie.remove("aa");
        trie.remove("aaa");
        trie.remove("aac");
        trie.remove("ab");
    }

    @org.junit.Test
    public void testRemove() throws Exception {
        Trie trie = new TrieImpl();
        assertFalse(trie.contains(""));
        trie.add("");
        trie.add("aa");
        trie.add("a");
        trie.add("aaa");
        trie.add("aab");
        trie.add("aac");
        trie.add("ab");
        assertTrue(trie.remove(""));
        assertTrue(trie.remove("aab"));
        trie.add("aaaa");
        assertTrue(trie.remove("a"));
        assertTrue(trie.remove("aa"));
        assertTrue(trie.remove("aaa"));
        assertTrue(trie.remove("aac"));
        assertTrue(trie.remove("ab"));

        assertFalse(trie.remove(""));
        assertFalse(trie.remove("aab"));
        assertFalse(trie.remove(""));
        assertFalse(trie.remove("aab"));
        assertFalse(trie.remove("a"));
        assertFalse(trie.remove("aa"));
        assertFalse(trie.remove("aaa"));
        assertFalse(trie.remove("aac"));
        assertFalse(trie.remove("ab"));
    }    

    @org.junit.Test
    public void testContains() throws Exception {
        Trie trie = new TrieImpl();
        assertFalse(trie.contains(""));
        trie.add("");
        trie.add("aa");
        trie.add("a");
        trie.add("aaa");
        trie.add("aab");
        trie.add("aac");
        trie.add("ab");

        assertTrue(trie.contains(""));
        assertTrue(trie.contains("aa"));
        assertTrue(trie.contains("a"));
        assertTrue(trie.contains("aaa"));
        assertTrue(trie.contains("aab"));
        assertTrue(trie.contains("aac"));
        assertTrue(trie.contains("ab"));

        trie.remove("");
        trie.remove("aab");

        assertFalse(trie.contains(""));
        assertTrue(trie.contains("aa"));
        assertTrue(trie.contains("a"));
        assertTrue(trie.contains("aaa"));
        assertFalse(trie.contains("aab"));
        assertTrue(trie.contains("aac"));
        assertTrue(trie.contains("ab"));

        trie.add("aaaa");
        trie.remove("a");
        trie.remove("aa");
        trie.remove("aaa");
        trie.remove("aac");
        trie.remove("ab");

        assertFalse(trie.contains(""));
        assertFalse(trie.contains("aa"));
        assertFalse(trie.contains("a"));
        assertFalse(trie.contains("aaa"));
        assertFalse(trie.contains("aab"));
        assertFalse(trie.contains("aac"));
        assertFalse(trie.contains("ab"));
        assertTrue(trie.contains("aaaa"));
    }

    @org.junit.Test
    public void testSize() throws Exception {
        Trie trie = new TrieImpl();
        
        assertEquals(trie.size(), 0);
        trie.add("");
        assertEquals(trie.size(), 1);
        trie.add("aa");
        trie.add("a");
        trie.add("aaa");
        assertEquals(trie.size(), 4);
        trie.add("aab");
        trie.add("aac");
        trie.add("ab");
        assertEquals(trie.size(), 7);
        
        trie.remove("");
        trie.remove("aab");
        assertEquals(trie.size(), 5);

        trie.add("aaaa");
        assertEquals(trie.size(), 6);

        trie.remove("a");
        trie.remove("aa");
        trie.remove("aaa");
        assertEquals(trie.size(), 3);
        trie.remove("aac");
        trie.remove("ab");
        assertEquals(trie.size(), 1);

    }
 
    @org.junit.Test
    public void testHowManyStartsWithPrefix() throws Exception {
        Trie trie = new TrieImpl();
        
        assertEquals(trie.howManyStartsWithPrefix(""), 0);
        assertEquals(trie.howManyStartsWithPrefix("a"), 0);
        assertEquals(trie.howManyStartsWithPrefix("aa"), 0);
        assertEquals(trie.howManyStartsWithPrefix("aaa"), 0);

        trie.add("");
        trie.add("aa");
        trie.add("a");
        trie.add("aaa");
        trie.add("aab");
        trie.add("aac");
        trie.add("ab");

        assertEquals(trie.howManyStartsWithPrefix(""), 7);
        assertEquals(trie.howManyStartsWithPrefix("a"), 6);
        assertEquals(trie.howManyStartsWithPrefix("aa"), 4);
        assertEquals(trie.howManyStartsWithPrefix("aaa"), 1);
        
        trie.remove("");
        trie.remove("aab");

        trie.add("aaaa");

        assertEquals(trie.howManyStartsWithPrefix(""), 6);
        assertEquals(trie.howManyStartsWithPrefix("a"), 6);
        assertEquals(trie.howManyStartsWithPrefix("aa"), 4);
        assertEquals(trie.howManyStartsWithPrefix("aaa"), 2);

        trie.remove("a");
        trie.remove("aa");
        trie.remove("aaa");
        trie.remove("aac");
        trie.remove("ab");

        assertEquals(trie.howManyStartsWithPrefix(""), 1);
        assertEquals(trie.howManyStartsWithPrefix("a"), 1);
        assertEquals(trie.howManyStartsWithPrefix("aa"), 1);
        assertEquals(trie.howManyStartsWithPrefix("aaa"), 1);
    }


}