package com.github.nizshee.client;

import java.io.*;


public class ClientMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        TorrentClient torrentClient = new TorrentClient(Short.parseShort(args[0]), e -> null);

        label:
        while (true) {
            if (in.ready()) {
                String input = in.readLine();
                try {
                    String[] parts = input.split(" ");
                    switch (parts[0]) {
                        case "exit":
                            break label;
                        case "list": {
                            torrentClient.list(list -> {
                                System.out.println("list:");
                                list.forEach(System.out::println);
                                return null;
                            });
                            break;
                        }
                        case "upload": {
                            String name = parts[1];
                            torrentClient.upload(name, identifier -> {
                                System.out.println("file added with id " + identifier);
                                return null;
                            });
                            break;
                        }
                        case "get": {
                            int identifier = Integer.parseInt(parts[1]);
                            torrentClient.get(identifier);
                            break;
                        }
                        case "sources": {
                            int identifier = Integer.parseInt(parts[1]);
                            torrentClient.sources(identifier, sources -> {
                                System.out.println("sources:");
                                sources.forEach(System.out::println);
                                return null;
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            torrentClient.runNow();

        }

        torrentClient.close();
    }

}
