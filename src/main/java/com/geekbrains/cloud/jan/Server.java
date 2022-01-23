package com.geekbrains.cloud.jan;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        startServer();
        receivedClientFile();
    }

    public static void startServer() throws IOException {
        ServerSocket server = new ServerSocket(Constants.SERVER_PORT);
        while (true) {
            Socket socket = server.accept();
            System.out.println("New client connected...");
            new Thread(new Handler(socket)).start();
        }
    }

    public static void receivedClientFile() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("data/fileFromClient.txt"));
        String str = (String) in.readObject();
        System.out.println(str);
    }
}
