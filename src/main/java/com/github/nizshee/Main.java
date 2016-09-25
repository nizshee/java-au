package com.github.nizshee;


import com.github.nizshee.cvs.CVS;
import com.github.nizshee.index.InMemoryIndex;
import com.github.nizshee.index.Index;
import com.github.nizshee.tags.InMemoryTags;
import com.github.nizshee.tags.Tags;
import com.github.nizshee.workspace.FileWorkspace;
import com.github.nizshee.workspace.Workspace;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private final static String INDEX = "index";
    private final static String TAGS = "tags";
    private final static String WORKDIR = "workdir";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String args[]) throws Exception {

        Paths.get(WORKDIR).toFile().mkdirs();

        Workspace workspace = new FileWorkspace(WORKDIR);

        Tags tags = restoreTags();
        Index index = restoreIndex();
        CVS cvs = new CVS(workspace, index);

        String current = tags.currentHash();
        if (current != null) {
            cvs.change(current);
        } else {
            String hash = cvs.create();
            tags.create("master", hash);
            tags.setCurrent("master");
            cvs.change(hash);
        }

        Frontend frontend = new Frontend(tags, cvs);
        frontend.eval(args);

        dumpIndex(index);
        dumpTags(tags);
    }


    private static Index restoreIndex() throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(INDEX)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (Index) ois.readObject();
        } catch (FileNotFoundException ignore) {
            return new InMemoryIndex(new HashMap<>());
        }
    }

    private static Tags restoreTags() throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(TAGS)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (Tags) ois.readObject();
        } catch (FileNotFoundException ignore) {
            return new InMemoryTags();
        }
    }

    private static void dumpIndex(Index index) throws IOException {
        FileOutputStream fos = new FileOutputStream(INDEX);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(index);
        fos.close();
    }

    private static void dumpTags(Tags tags) throws IOException {
        FileOutputStream fos = new FileOutputStream(TAGS);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(tags);
        fos.close();
    }
}
