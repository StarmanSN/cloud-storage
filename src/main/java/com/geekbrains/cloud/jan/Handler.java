package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.serialization.TransferObject;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable {

    private DataInputStream in;
    private DataOutputStream out;

    public Handler(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public Handler() {

    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = in.readUTF();
                System.out.println("message from client: " + message);
                out.writeUTF(message);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receivedClientFile() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("data/fileFromClient.txt"));
        in.readObject();
    }
}
