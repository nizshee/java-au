package sp;

import java.lang.Character;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map;
 
public class TrieImpl implements Trie {

    Node root = new Node();
 
    public boolean add(String element) {
        Node node = root;
        for(char c : element.toCharArray()) 
            node = node.next(c);
        return node.setLeaf(true);
    }
 
    public boolean contains(String element) {
        Node node = root;
        for(char c : element.toCharArray()) {
            if (!node.containsKey(c))
                return false;
            node = node.get(c);
        }
        return node.isLeaf();
    }
 
    public boolean remove(String element) {
        Node node = root;
        for(char c : element.toCharArray()) {
            if (!node.containsKey(c))
                return false;
            node = node.get(c);
        }
        return node.setLeaf(false);
    }
 
    public int size() {
        return root.getSize();
    }
 
    public int howManyStartsWithPrefix(String prefix) {
        Node node = root;
        for(char c : prefix.toCharArray()) {
            if (!node.containsKey(c))
                return 0;
            node = node.get(c);
        }
        return node.getSize();
    }

    private class Node extends HashMap<Character, Node> {

        private int wordCount = 0;
        private boolean isLeaf = false;
        private WeakReference<Node> parrent = null;
        private Character character = null;

        public Node() {}

        public Node(Node prev, Character c) { 
            parrent = new WeakReference<>(prev); 
            character = c;
        }

        public Node next(Character key) {
            if (!containsKey(key)) 
                put(key, new Node(this, key));
            return get(key);
        }

        public int getSize() { return wordCount; }

        private void updateSize(Character c, int delta) {
            if (c != null && get(c).getSize() == 0) {
                remove(c);
            }
            wordCount += delta;
            if (parrent != null)
                parrent.get().updateSize(this.character, delta);
        }

        boolean isLeaf() { return isLeaf; }

        boolean setLeaf(boolean value) {
            if (isLeaf == value)
                return false;
            isLeaf = value;          
            updateSize(null, value ? 1 : -1);  
            return true;
        }

    }
}