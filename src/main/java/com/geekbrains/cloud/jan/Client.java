package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.model.CloudMessage;
import com.geekbrains.cloud.jan.model.FileMessage;
import com.geekbrains.cloud.jan.model.FileRequest;
import com.geekbrains.cloud.jan.model.ListMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

@Slf4j
public class Client implements Initializable {

    public TextField clientPath;
    public TextField serverPath;
    private Path clientDir;
    public ListView<String> clientView;
    public ListView<String> serverView;
    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;
    private CloudMessageProcessor processor;
    /*private byte[] buf;*/

    /**
     * read from network
     */
    private void readLoop() {
        try {
            while (true) {
                /*String command = in.readUTF();*/
                CloudMessage message = (CloudMessage) in.readObject();
                /*System.out.println("received: " + command);*/
                log.info("received: {}", message);
                processor.processMessage(message);
            }
                /*if (command.equals("#list#")) {
                    Platform.runLater(() -> serverView.getItems().clear());
                    int filesCount = in.readInt();
                    for (int i = 0; i < filesCount; i++) {
                        String fileName = in.readUTF();
                        Platform.runLater(() -> serverView.getItems().add(fileName));
                    }*/
                /*} else if (command.equals("#file#")) {
                    Sender.getFile(in, clientDir, Constants.SIZE, buf);
                    Platform.runLater(this::updateClientView);
                }*/

        } catch (
                Exception e) {
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
            /*buf = new byte[Constants.SIZE];*/
            clientDir = Paths.get(System.getProperty("user.home"));
            updateClientView();
            initMouseListeners();
            setClientPath();
            setServerPath();
            processor = new CloudMessageProcessor(clientDir, clientView, serverView);
            Socket socket = new Socket(Constants.SERVER_ADRESS, Constants.SERVER_PORT);
            System.out.println("Network created...");
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMouseListeners() {

        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Path current = clientDir.resolve(getItem());
                if (Files.isDirectory(current)) {
                    clientDir = current;
                    Platform.runLater(this::updateClientView);
                }
            }
        });

        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {

            }
        });
    }

    private String getItem() {
        return clientView.getSelectionModel().getSelectedItem();
    }

    /**
     * Upload file from client
     */
    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        out.writeObject(new FileMessage(clientDir.resolve(fileName)));
        /*Sender.sendFile(fileName, out, clientDir);*/
    }

    /**
     * Download file from server
     */
    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        out.writeObject(new FileRequest(fileName));
        /*out.writeUTF("#get_file#");
        out.writeUTF(fileName);
        out.flush();*/
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
