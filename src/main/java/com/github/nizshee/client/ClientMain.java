package com.github.nizshee.client;


import com.github.nizshee.shared.RemoteFile;

import java.util.List;

public class ClientMain {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);

        if (args.length == 2 && args[0].equals("list")) {
            List<RemoteFile> list = client.getList(args[1]);
            for (RemoteFile file: list) {
                System.out.print(file + " ");
                if (file.isDirectory) {
                    System.out.println("<dir>");
                } else {
                    System.out.println();
                }
            }
        } else if (args.length == 2 && args[0].equals("get")) {
            client.getFile(args[1]);
            System.out.println("file downloaded");
        } else {
            System.out.println("usage: list <path> | get <file>");
        }
    }
}
