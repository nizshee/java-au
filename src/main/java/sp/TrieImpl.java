package sp;

import java.io.*;
import java.lang.Character;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TrieImpl implements Trie, StreamSerializable {

    private final Node root = new Node();

    @Override
    public boolean add(String element) {
        Node node = root;
        for (char c : element.toCharArray()) {
            node = node.getNextNodeOrCreateIfNotExists(c);
        }
        return changeLeafState(node, true);
    }

    @Override
    public boolean contains(String element) {
        Node node = findNode(element);
        return node != null && node.isLeaf();
    }

    @Override
    public boolean remove(String element) {
        Node node = findNode(element);
        return node != null && changeLeafState(node, false);
    }

    @Override
    public int size() {
        return root.getSize();
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        Node node = findNode(prefix);
        return node == null ? 0 : node.getSize();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TrieImpl && root.equals(((TrieImpl) obj).root);
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }

    @Override
    public void serialize(OutputStream out) throws IOException {
        JSONObject obj = root.toJson();
        out.write(obj.toJSONString().getBytes());
    }

    @Override
    public void deserialize(InputStream in) throws IOException {
        try {
            JSONObject obj = (JSONObject) JSONValue.parseWithException(new InputStreamReader(in));
            root.fromJson(obj, null);
        } catch (Exception e) {
            throw new IOException("can't parse json", e);
        }
    }

    private Node findNode(String element) {
        Node node = root;
        for (char c : element.toCharArray()) {
            if (!node.containsKey(c)) return null;
            node = node.get(c);
        }
        return node;
    }

    private boolean changeLeafState(Node node, boolean value) {
        if (node.isLeaf() == value) return false;
        node.setLeaf(value);

        int delta = value ? 1 : -1;
        for (Node prev = null; node != null; node = node.getParent()) {
            if (prev != null && prev.getSize() == 0) {
                node.remove(prev.character);
            }
            node.setSize(node.getSize() + delta);
            prev = node;
        }

        return true;
    }

    private static class Node {

        private int wordCount = 0;
        private boolean isLeaf = false;
        private char character = '\n';
        private WeakReference<Node> parent = null;
        private Map<Character, Node> nextNodes = new HashMap<>();

        public static final String WORD_COUNT = "wordCount";
        public static final String CHARACTER = "character";
        public static final String IS_LEAF = "isLeaf";
        public static final String OTHER = "other";

        public Node() {
        }

        private Node(Node prev, char c) {
            parent = new WeakReference<>(prev);
            character = c;
        }

        public Node get(char key) {
            return nextNodes.get(key);
        }

        public Node remove(char key) {
            return nextNodes.remove(key);
        }

        public boolean containsKey(char key) {
            return nextNodes.containsKey(key);
        }

        public void put(char key, Node value) {
            nextNodes.put(key, value);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node other = (Node) o;
            return wordCount == other.wordCount &&
                    isLeaf == other.isLeaf &&
                    character == other.character &&
                    nextNodes.keySet().equals(other.nextNodes.keySet()) &&
                    nextNodes.equals(other.nextNodes);
        }

        @Override
        public int hashCode() {
            return nextNodes.hashCode() + (isLeaf ? 1 : 0);
        }

        public void fromJson(JSONObject obj, Node prev) throws Exception {
            parent = prev == null ? null : new WeakReference<>(prev);
            wordCount = ((Long) obj.get(WORD_COUNT)).intValue();
            isLeaf = (Boolean) obj.get(IS_LEAF);
            character = ((String) obj.get(CHARACTER)).charAt(0);
            JSONObject other = (JSONObject) obj.get(OTHER);
            nextNodes.clear();
            for (Object key : other.keySet()) {
                Node node = new Node();
                node.fromJson((JSONObject) other.get(key), this);
                put(node.character, node);
            }
        }

        @SuppressWarnings("unchecked")
        public JSONObject toJson() {
            JSONObject obj = new JSONObject();
            obj.put(WORD_COUNT, wordCount);
            obj.put(IS_LEAF, isLeaf);
            obj.put(CHARACTER, Character.toString(character));
            JSONObject other = new JSONObject();
            for (Map.Entry<Character, Node> entry : nextNodes.entrySet()) {
                other.put(entry.getKey(), entry.getValue().toJson());
            }
            obj.put(OTHER, other);
            return obj;
        }

        public Node getNextNodeOrCreateIfNotExists(char key) {
            if (!containsKey(key)) {
                put(key, new Node(this, key));
            }
            return get(key);
        }

        public int getSize() {
            return wordCount;
        }

        public void setSize(int count) {
            wordCount = count;
        }

        public Node getParent() {
            return parent == null ? null : parent.get();
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public void setLeaf(boolean value) {
            isLeaf = value;
        }

    }
}