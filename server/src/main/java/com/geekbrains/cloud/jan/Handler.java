package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.model.Constants;
import com.geekbrains.cloud.jan.netty.BaseNettyServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.geekbrains.cloud.jan.Sender.getFile;
import static com.geekbrains.cloud.jan.Sender.sendFile;

public class Handler implements Runnable {

    private Path clientDir;
    private Path rootDir;
    private DataInputStream in;
    private DataOutputStream out;
    private final byte[] buf;
    private String enterDir;
    private String name;
    private BaseNettyServer server;

    public Handler(Socket socket) throws IOException {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            clientDir = Paths.get("data");
            buf = new byte[Constants.SIZE];
            try {
                authentication();
                sendServerFiles();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
                socket.close();
            }
        } catch (IOException ex) {
            throw new RemoteException("Проблемы при создании обработчика");
        }

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

    private void authentication() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith(Constants.AUTH_COMMAND)) {
                String[] tokens = str.split("\\s+");
                Optional<String> nick = server.getAuthService().getNickByLoginAndPass(tokens[1], tokens[2]);
                if (nick.isPresent()) {
                    name = nick.get();
                    sendMessage(Constants.AUTH_OK_COMMAND + " " + nick);
                    server.broadcastMessage(nick + " вошел в чат");
                    server.broadcastMessage(server.getActiveClients());
                    server.subscribe(this);
                    return;
                } else {
                    sendMessage("Неверные логин/пароль");
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String message) throws IOException {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                } else if (command.equals("#backDir")) {
                    if (!clientDir.equals(rootDir)) {
                        clientDir = clientDir.getParent();
                    }
                    sendServerFiles();
                } else if (command.equals("#enterDir")) {
                    enterDir = in.readUTF();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
