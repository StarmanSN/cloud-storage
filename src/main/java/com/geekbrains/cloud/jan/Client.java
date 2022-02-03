package com.geekbrains.cloud.jan;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Client implements Initializable {

    public TextField clientPath;
    public TextField serverPath;
    private Path clientDir;
    public ListView<String> clientView;
    public ListView<String> serverView;
    private DataInputStream in;
    private DataOutputStream out;
    private byte[] buf;

    /**
     * read from network
     */
    private void readLoop() {
        try {
            while (true) {
                String command = in.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#list#")) {
                    Platform.runLater(() -> serverView.getItems().clear());
                    int filesCount = in.readInt();
                    for (int i = 0; i < filesCount; i++) {
                        String fileName = in.readUTF();
                        Platform.runLater(() -> serverView.getItems().add(fileName));
                    }
                } else if (command.equals("#file#")) {
                    Sender.getFile(in, clientDir, Constants.SIZE, buf);
                    Platform.runLater(this::updateClientView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateClientView() {
        try {
            clientView.getItems().clear();
            Files.list(clientDir)
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setClientPath() {
            clientPath.clear();
            clientPath.appendText(String.valueOf(clientDir));

    }

    private void setServerPath() throws IOException {
        serverPath.clear();
        clientDir = Paths.get("data");
        serverPath.appendText(String.valueOf(clientDir.toAbsolutePath()));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            buf = new byte[Constants.SIZE];
            clientDir = Paths.get(System.getProperty("user.home"));
            updateClientView();
            setClientPath();
            setServerPath();
            Socket socket = new Socket(Constants.SERVER_ADRESS, Constants.SERVER_PORT);
            System.out.println("Network created...");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload file from client
     */
    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        Sender.sendFile(fileName, out, clientDir);
    }

    /**
     * Download file from server
     */
    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        out.writeUTF("#get_file#");
        out.writeUTF(fileName);
        out.flush();
    }

    public void backClientPath(ActionEvent actionEvent) {
        String str = clientPath.getText();
        int position = -1;
        for (int i = str.length(); position < 0; i--) {
            position = str.indexOf("\\", i);
        }
        clientPath.clear();
        clientPath.appendText(str.substring(0, position));
        clientDir = Paths.get(clientPath.getText());
    }

    public void backServerPath(ActionEvent actionEvent) {
        String str = serverPath.getText();
        int position = -1;
        for (int i = str.length(); position < 0; i--) {
            position = str.indexOf("\\", i);
        }
        serverPath.clear();
        serverPath.appendText(str.substring(0, position));
        try {
            out.writeUTF(serverPath.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * В процессе...
     */

    public void openDirectory() {

    }

    public void enterToDirectory(MouseEvent mouseEvent) throws IOException {

    }


}
