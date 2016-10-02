package com.github.nizshee.cvs;

import com.github.nizshee.message.CommitMessage;
import com.github.nizshee.exception.CVSException;
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
    public void initTest() throws Exception {
        Index index = new InMemoryIndex(new HashMap<>());
        Workspace workspace = new InMemoryWorkspace(new HashMap<>());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.init();

        assertNotNull(cvs.current());
    }

    @Test
    public void addTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        assertEquals(0, cvs.createdToCommit().size());
        assertEquals(0, cvs.changedToCommit().size());
        assertEquals(0, cvs.removedToCommit().size());
        assertEquals(0, cvs.created().size());
        assertEquals(1, cvs.changed().size());
        assertEquals(1, cvs.removed().size());
        assertEquals(1, cvs.notTracked().size());

        cvs.add("file1");

        assertEquals(0, cvs.createdToCommit().size());
        assertEquals(0, cvs.changedToCommit().size());
        assertEquals(1, cvs.removedToCommit().size());
        assertEquals(0, cvs.created().size());
        assertEquals(1, cvs.changed().size());
        assertEquals(0, cvs.removed().size());
        assertEquals(1, cvs.notTracked().size());

        cvs.add("file2");

        assertEquals(0, cvs.createdToCommit().size());
        assertEquals(1, cvs.changedToCommit().size());
        assertEquals(1, cvs.removedToCommit().size());
        assertEquals(0, cvs.created().size());
        assertEquals(0, cvs.changed().size());
        assertEquals(0, cvs.removed().size());
        assertEquals(1, cvs.notTracked().size());

        cvs.add("file3");

        assertEquals(1, cvs.createdToCommit().size());
        assertEquals(1, cvs.changedToCommit().size());
        assertEquals(1, cvs.removedToCommit().size());
        assertEquals(0, cvs.created().size());
        assertEquals(0, cvs.changed().size());
        assertEquals(0, cvs.removed().size());
        assertEquals(0, cvs.notTracked().size());
    }

    @Test
    public void resetTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");
        cvs.add("file1");
        cvs.add("file2");
        cvs.add("file3");

        cvs.reset("file1");

        assertEquals(1, cvs.createdToCommit().size());
        assertEquals(1, cvs.changedToCommit().size());
        assertEquals(0, cvs.removedToCommit().size());
        assertEquals(0, cvs.created().size());
        assertEquals(0, cvs.changed().size());
        assertEquals(1, cvs.removed().size());
        assertEquals(0, cvs.notTracked().size());

        cvs.reset("file3");

        assertEquals(0, cvs.createdToCommit().size());
        assertEquals(1, cvs.changedToCommit().size());
        assertEquals(0, cvs.removedToCommit().size());
        assertEquals(0, cvs.created().size());
        assertEquals(0, cvs.changed().size());
        assertEquals(1, cvs.removed().size());
        assertEquals(1, cvs.notTracked().size());

        cvs.reset("file2");

        assertEquals(0, cvs.createdToCommit().size());
        assertEquals(0, cvs.changedToCommit().size());
        assertEquals(0, cvs.removedToCommit().size());
        assertEquals(0, cvs.created().size());
        assertEquals(1, cvs.changed().size());
        assertEquals(1, cvs.removed().size());
        assertEquals(1, cvs.notTracked().size());
    }

    @Test
    public void cleanTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        cvs.clean();

        assertEquals(new HashSet<>(Collections.singletonList("file2")), workspace.getFiles());

        cvs.clean();

        assertEquals(new HashSet<>(Collections.singletonList("file2")), workspace.getFiles());
    }

    @Test
    public void rmTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        cvs.rm("file2");

        assertEquals(1, cvs.removedToCommit().size());

        cvs.rm("file1");

        assertEquals(2, cvs.removedToCommit().size());

        cvs.rm("file3");

        assertEquals(2, cvs.removedToCommit().size());
    }

    @Test
    public void changedTest() throws Exception {
        Set<String> set = new HashSet<>();

        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        assertEquals(set, cvs.changedToCommit());

        cvs.add("file1");
        cvs.add("file2");
        cvs.add("file3");
        set.add("file2");

        assertEquals(set, cvs.changedToCommit());
    }

    @Test
    public void removedTest() throws Exception {
        Set<String> set = new HashSet<>();

        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        assertEquals(set, cvs.removedToCommit());

        cvs.add("file1");
        cvs.add("file2");
        cvs.add("file3");
        set.add("file1");

        assertEquals(set, cvs.removedToCommit());
        System.out.println("");
    }

    @Test
    public void createdTest() throws Exception {
        Set<String> set = new HashSet<>();

        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        assertEquals(set, cvs.createdToCommit());

        cvs.add("file1");
        cvs.add("file2");
        cvs.add("file3");
        set.add("file3");

        assertEquals(set, cvs.createdToCommit());
    }

    @Test
    public void notIndexedFilesTest() throws Exception {
        Set<String> set = new HashSet<>();
        set.add("file3");

        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        assertEquals(set, cvs.notTracked());

        cvs.add("file1");
        cvs.add("file2");

        assertEquals(set, cvs.notTracked());

        cvs.add("file3");

        assertEquals(new HashSet<String>(), cvs.notTracked());
    }

    @Test
    public void savedTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceSameMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        assertTrue(cvs.saved());

        cvs.add("file1");
        cvs.add("file2");

        assertTrue(cvs.saved());

        workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        assertTrue(cvs.saved());

        cvs.add("file1");
        cvs.add("file2");
        cvs.add("file3");

        assertFalse(cvs.saved());
    }

    @Test
    public void commitTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        cvs.add("file1");
        cvs.add("file2");
        cvs.add("file3");

        String hash = cvs.commit("12345");

        Node node = index.getNode(hash);
        assertTrue(index.getNode(hash) instanceof Commit);
        CommitMessage message = node.message(hash);
        assertEquals(message.message, "12345");

        assertTrue(cvs.createdToCommit().isEmpty());
        assertTrue(cvs.changedToCommit().isEmpty());
        assertTrue(cvs.removedToCommit().isEmpty());
    }

    @Test(expected = CVSException.class)
    public void checkoutTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("2");

        cvs.checkout("1");
        assertTrue(workspace.getFiles().isEmpty());

        cvs.checkout("2");
        assertEquals(new HashSet<>(Arrays.asList("file1", "file2")), workspace.getFiles());

        cvs.add("file1");
        cvs.add("file2");
        cvs.add("file3");

        cvs.checkout("1");
    }

    @Test
    public void mergeTest() throws Exception {
        Index index = new InMemoryIndex(getIndexMap());
        Workspace workspace = new InMemoryWorkspace(getWorkspaceDifferentMap());
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("1");

        cvs.add("file2");
        cvs.add("file3");

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
        CVS cvs = new CVS(workspace, index, Collections.emptyList());
        cvs.change("1");
        cvs.add("file1");
        cvs.add("file2");

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
