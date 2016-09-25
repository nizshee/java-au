package com.github.nizshee;


import com.github.nizshee.cvs.CVS;
import com.github.nizshee.tags.Tags;

import java.util.Set;


/**
 * Frontend for class {@code CVS}.
 * Adds names for identificators.
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
        if (args.length == 1 && args[0].equals("status")) {
            System.out.println("On branch: " + tags.currentName());
            System.out.println("Hash: " + tags.currentHash());

            Set<String> files;
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

            files = cvs.removed();
            if (!files.isEmpty()) {
                System.out.println("Files removed:");
                files.forEach(System.out::println);
            }
        } else if (args.length == 2 && args[0].equals("commit")) {
            String hash = cvs.commit(args[1]);
            tags.changeCurrent(hash);
            System.out.println(hash);
        } else if (args.length == 2 && args[0].equals("branch")) {
            tags.create(args[1], tags.currentHash());
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
        } else {
            System.out.println("Unknown command.");
        }

    }


}
