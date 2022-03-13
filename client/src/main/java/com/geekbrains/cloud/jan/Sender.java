package com.geekbrains.cloud.jan;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Sender {

    private Sender() {}

    static void getFile(DataInputStream in, Path clientDir, int size2, byte[] buf) throws IOException {
        String fileName = in.readUTF();
        System.out.println("received: " + fileName);
        long size = in.readLong();
        try (OutputStream fos = new FileOutputStream(clientDir.resolve(fileName).toFile())) {
            for (int i = 0; i < (size + size2 - 1) / size2; i++) {
                int readBytes = in.read(buf);
                fos.write(buf, 0, readBytes);
            }
        }
    }

    static void sendFile(String fileName, DataOutputStream out, Path clientDir) throws IOException {
        out.writeUTF("#file#");
        out.writeUTF(fileName);
        Path file = clientDir.resolve(fileName);
        long size = Files.size(file);
        byte[] bytes = Files.readAllBytes(file);
        out.writeLong(size);
        out.write(bytes);
        out.flush();
    }
}
