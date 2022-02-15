package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.model.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        startServer();
    }

    public static void startServer() throws IOException {
        ServerSocket server = new ServerSocket(Constants.SERVER_PORT);
        System.out.println("Server is waiting for a connection");
        while (true) {
            Socket socket = server.accept();
            System.out.println("New client connected...");
            new Thread(new Handler(socket)).start();
        }
    }
}
