package com.geekbrains.cloud.jan;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.geekbrains.cloud.jan.Sender.getFile;
import static com.geekbrains.cloud.jan.Sender.sendFile;

public class Handler implements Runnable {

    private Path clientDir;
    private DataInputStream in;
    private DataOutputStream out;
    private final byte[] buf;

    public Handler(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        clientDir = Paths.get("data");
        buf = new byte[Constants.SIZE];
        sendServerFiles();
    }

    public void sendServerFiles() throws IOException {
        List<String> files = Files.list(clientDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        out.writeUTF("#list#");
        out.writeInt(files.size());
        for (String file : files) {
            out.writeUTF(file);
        }
        out.flush();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = in.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#file#")) {
                    getFile(in, clientDir, Constants.SIZE, buf);
                    sendServerFiles();
                } else if (command.equals("#get_file#")) {
                    String fileName = in.readUTF();
                    sendFile(fileName, out, clientDir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
