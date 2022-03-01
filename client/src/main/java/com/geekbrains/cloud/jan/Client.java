package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.model.CloudMessage;
import com.geekbrains.cloud.jan.model.Constants;
import com.geekbrains.cloud.jan.model.FileMessage;
import com.geekbrains.cloud.jan.model.FileRequest;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
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
    private String message;

    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = (CloudMessage) in.readObject();
                log.info("received: {}", message);
                processor.processMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientDir = Paths.get(System.getProperty("user.home"));
            processor = new CloudMessageProcessor(clientDir, clientView, serverView);
            Socket socket = new Socket(Constants.SERVER_ADRESS, Constants.SERVER_PORT);
            System.out.println("Network created...");
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
            updateClientView();
            updateServerPath();
            initMouseListeners();
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
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
            Platform.runLater(() -> {
                clientPath.setText(clientDir.toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateServerPath() {
        Platform.runLater(() -> {
            serverPath.setText(String.valueOf(clientDir.getParent()));
        });
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
                try {
                    out.writeUTF("#enterDir");
                    out.writeUTF(serverView.getSelectionModel().getSelectedItem());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private String getItem() {
        return clientView.getSelectionModel().getSelectedItem();
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = getItem();
        out.writeObject(new FileMessage(clientDir.resolve(fileName)));
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        out.writeObject(new FileRequest(fileName));
    }

    @FXML
    private void backClientPath(ActionEvent actionEvent) {
        if (!clientDir.equals(clientDir.getRoot())) {
            clientDir = clientDir.getParent();
            updateClientView();
        }
    }

    @FXML
    public void backServerPath(ActionEvent actionEvent) {
        try {
            out.writeUTF("#backDir");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /*@FXML
    public boolean delFile(ActionEvent actionEvent) {


        String filePathAndName = null;
        boolean bea = false;

        try {
            String filePath = filePathAndName;
            File myDelFile = new File(filePath);
            if (myDelFile.exists()) {
                myDelFile.delete();
                bea = true;
            } else {
                bea = false;
                message = (filePathAndName + "Ошибка удаления файловой операции");
            }
        } catch (Exception e) {
            message = e.toString();
        }
        return bea;
    }

    @FXML
    public String createFolder(ActionEvent actionEvent) {
        String folderPath = "data";
        String txt = folderPath;
        try {
            java.io.File myFilePath = new java.io.File(txt);
            txt = folderPath;
            if (!myFilePath.exists()) {
                myFilePath.mkdir();
            }
        } catch (Exception e) {
            message = "Ошибка создания каталога";
        }
        return txt;
    }

    @FXML
    public void delFolder(ActionEvent actionEvent) {
        String folderPath = null;
        try {
            String filePath = folderPath;
            assert filePath != null;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // Удалить пустую папку
        } catch (Exception e) {
            message = ("Ошибка удаления папки");
        }
    }*/
}
