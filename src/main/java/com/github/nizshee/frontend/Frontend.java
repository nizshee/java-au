package com.github.nizshee.frontend;


import com.github.nizshee.cvs.CVS;
import com.github.nizshee.tags.Tags;

import java.util.Set;


/**
 * Frontend for class {@code CVS}.
 * Adds names for identificators with {@code Tags}.
 */
@SuppressWarnings("all")
public class Frontend {

    private final Tags tags;
    private final CVS cvs;

    public Frontend(Tags tags, CVS cvs) {
        this.tags = tags;
        this.cvs = cvs;
    }

    public void eval(String args[]) throws Exception {


        if (cvs.current() == null) {
            if (args.length == 1 && args[0].equals("init")) {
                cvs.init();
                tags.create("master", cvs.current());
                tags.setCurrent("master");
            } else
                throw new Exception("Need init.");
        } else if (args.length == 1 && args[0].equals("status")) {
            System.out.println("On branch: " + tags.current());
            System.out.println("Hash: " + cvs.current());

            Set<String> files;
            files = cvs.notTracked();
            if (!files.isEmpty()) {
                System.out.println("Not indexed:");
                files.forEach(System.out::println);
            }

            files = cvs.createdToCommit();
            if (!files.isEmpty()) {
                System.out.println("Files to commit, created :");
                files.forEach(System.out::println);
            }

            files = cvs.changedToCommit();
            if (!files.isEmpty()) {
                System.out.println("Files to commit, changed:");
                files.forEach(System.out::println);
            }

            files = cvs.removedToCommit();
            if (!files.isEmpty()) {
                System.out.println("Files to commit, removed:");
                files.forEach(System.out::println);
            }

            files = cvs.removed();
            if (!files.isEmpty()) {
                System.out.println("Files removed:");
                files.forEach(System.out::println);
            }

            files = cvs.created();
            if (!files.isEmpty()) {
                System.out.println("Files created:");
                files.forEach(System.out::println);
            }

            files = cvs.changed();
            if (!files.isEmpty()) {
                System.out.println("Files changed:");
                files.forEach(System.out::println);
            }
        } else if (args.length == 2 && args[0].equals("commit")) {
            String hash = cvs.commit(args[1]);
            tags.changeCurrent(hash);
            System.out.println(hash);
        } else if (args.length == 2 && args[0].equals("branch")) {
            tags.create(args[1], cvs.current());
        } else if (args.length == 2 && args[0].equals("remove")) {
            tags.remove(args[1]);
        } else if (args.length == 2 && args[0].equals("checkout")) {
            String hash = tags.getHash(args[1]);
            cvs.checkout(hash);
            tags.setCurrent(args[1]);
        } else if (args.length == 1 && args[0].equals("log")) {
            cvs.log().forEach(msg -> System.out.println(msg.hash + " - " + msg.message));
        } else if (args.length == 2 && args[0].equals("merge")) {
            String hash = tags.getHash(args[1]);
            String newHash = cvs.mergeWith(hash);
            tags.changeCurrent(newHash);
            System.out.println(newHash);
        } else if (args.length == 2 && args[0].equals("add")) {
            cvs.add(args[1]);
        } else if (args.length == 2 && args[0].equals("reset")) {
            cvs.reset(args[1]);
        } else if (args.length == 2 && args[0].equals("rm")) {
            cvs.rm(args[1]);
        } else if (args.length == 1 && args[0].equals("clean")) {
            cvs.clean();
        } else {
            System.out.println("Unknown command.");
        }

    }


}
