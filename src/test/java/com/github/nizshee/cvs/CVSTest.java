package com.github.nizshee.cvs;

import com.github.nizshee.CommitMessage;
import com.github.nizshee.index.InMemoryIndex;
import com.github.nizshee.index.Index;
import com.github.nizshee.node.Commit;
import com.github.nizshee.node.Init;
import com.github.nizshee.node.Node;
import com.github.nizshee.workspace.InMemoryWorkspace;
import com.github.nizshee.workspace.Workspace;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;


public class CVSTest {

    @Test
    public void changedTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index);
        cvs.change("2");

        Set<String> set = new HashSet<>();
        set.add("file2");

        assertEquals(set, cvs.changed());
    }

    @Test
    public void removedTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index);
        cvs.change("2");

        Set<String> set = new HashSet<>();
        set.add("file1");

        assertEquals(set, cvs.removed());
    }

    @Test
    public void createdTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index);
        cvs.change("2");

        Set<String> set = new HashSet<>();
        set.add("file3");

        assertEquals(set, cvs.created());
    }

    @Test
    public void savedTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceSameMap());
        CVS cvs = new CVS(workspace, index);
        cvs.change("2");

        assertTrue(cvs.saved());
    }

    @Test
    public void commitTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index);
        cvs.change("2");

        String hash = cvs.commit("12345");

        Node node = index.getNode(hash);
        assertTrue(index.getNode(hash) instanceof Commit);
        CommitMessage message = node.message(hash);
        assertEquals(message.message, "12345");

        assertTrue(cvs.created().isEmpty());
        assertTrue(cvs.changed().isEmpty());
        assertTrue(cvs.removed().isEmpty());
    }

    @Test
    public void checkoutTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceSameMap());
        CVS cvs = new CVS(workspace, index);
        cvs.change("2");

        cvs.checkout("1");
        assertTrue(workspace.getFiles().isEmpty());
    }

    @Test
    public void mergeTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index);
        cvs.change("1");

        cvs.commit("321");
        cvs.mergeWith("2");

        assertEquals(new HashSet<>(Arrays.asList("file1", "file2", "file3")), new HashSet<>(workspace.getFiles()));
        assertEquals(workspace.get("file1"), Arrays.asList("1", "2"));
        assertEquals(workspace.get("file2"), Arrays.asList("<<<", "1", "2", "===", "1", "2", "3", ">>>"));
        assertEquals(workspace.get("file3"), Arrays.asList("1", "2", "3"));
    }

    @Test
    public void logTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceSameMap());
        CVS cvs = new CVS(workspace, index);
        cvs.change("1");

        assertEquals(
                new HashSet<>(Arrays.asList(new CommitMessage("123", "2"), new CommitMessage("init", "1"))),
                cvs.log("2")
        );

        String commit = cvs.commit("321");
        String merge = cvs.mergeWith("2");

        assertEquals(
                new HashSet<>(Arrays.asList(new CommitMessage("123", "2"), new CommitMessage("init", "1"),
                        new CommitMessage("321", commit), new CommitMessage(commit + " with " + "2", merge))),
                cvs.log()
        );
    }

    private static Map<String, Node> getIndexMap() {
        Map<String, Node> map = new HashMap<>();
        map.put("1", new Init());
        Set<Commit.Diff> diff = new HashSet<>();
        diff.add(new Commit.Create("file1", Arrays.asList("1", "2")));
        diff.add(new Commit.Create("file2", Arrays.asList("1", "2", "3")));
        map.put("2", new Commit("123", "1", diff));
        return map;
    }

    private static Map<String, List<String>> getWorkspaceDifferentMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("file2", Arrays.asList("1", "2"));
        map.put("file3", Arrays.asList("1", "2", "3"));
        return map;
    }

    private static Map<String, List<String>> getWorkspaceSameMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("file1", Arrays.asList("1", "2"));
        map.put("file2", Arrays.asList("1", "2", "3"));
        return map;
    }
}
