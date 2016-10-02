package com.github.nizshee;


import com.github.nizshee.cvs.CVS;
import com.github.nizshee.exception.StateException;
import com.github.nizshee.exception.WorkspaceException;
import com.github.nizshee.frontend.Frontend;
import com.github.nizshee.index.InMemoryIndex;
import com.github.nizshee.tags.InMemoryTags;
import com.github.nizshee.tags.Tags;
import com.github.nizshee.workspace.FileWorkspace;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private final static String CVS = ".cvs";
    private final static String TAGS = ".tags";
    private final static String WORKDIR = "workdir";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String args[]) throws Exception {

        Paths.get(WORKDIR).toFile().mkdirs();

        Tags tags = restoreTags();
        CVS cvs = restoreCVS();


        Frontend frontend = new Frontend(tags, cvs);
        frontend.eval(args);

        if (cvs.current() != null) {
            dumpTags(tags);
            dumpCVS(cvs);
        }
    }

    private static CVS restoreCVS() throws IOException, ClassNotFoundException, WorkspaceException, StateException {
        try (FileInputStream fis = new FileInputStream(WORKDIR + "/" + CVS)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (CVS) ois.readObject();
        } catch (FileNotFoundException ignore) {
            return new CVS(new FileWorkspace(WORKDIR), new InMemoryIndex(new HashMap<>()),
                    Arrays.asList(CVS, TAGS));
        }
    }

    private static void dumpCVS(CVS cvs) throws IOException {
        FileOutputStream fos = new FileOutputStream(WORKDIR + "/" + CVS);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(cvs);
        fos.close();
    }

    private static Tags restoreTags() throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(WORKDIR + "/" + TAGS)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (Tags) ois.readObject();
        } catch (FileNotFoundException ignore) {
            return new InMemoryTags();
        }
    }

    private static void dumpTags(Tags tags) throws IOException {
        FileOutputStream fos = new FileOutputStream(WORKDIR + "/" + TAGS);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(tags);
        fos.close();
    }
}
