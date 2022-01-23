package com.geekbrains.cloud.jan;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client implements Initializable {

    public TextField textField;
    public ListView<String> listView;
    public Button button;
    private DataInputStream in;
    private DataOutputStream out;

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String text = textField.getText();
        textField.clear();
        out.writeUTF(text);
        out.flush();
    }

    private void readLoop() {
        try {
            while (true) {
                String message = in.readUTF();
                Platform.runLater(() -> listView.getItems().add(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
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

    public void sendFile(ActionEvent actionEvent) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data/fileFromClient.txt"));
        out.writeObject(textField.getText());
    }
}
