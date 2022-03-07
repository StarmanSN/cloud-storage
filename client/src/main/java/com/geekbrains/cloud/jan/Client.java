package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.model.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static java.nio.file.Files.delete;

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
    public TextField newDirName;
    public Button okBtn;
    public Button cancel;
    public AnchorPane newDirPane;
    public Label newTitle;

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
                    out.writeObject(new ListRequest(false, serverView.getSelectionModel().getSelectedItem()));
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
    public void backServerPath(ActionEvent actionEvent) throws IOException {
        out.writeObject(new ListRequest(true));
    }

    @FXML
    public void createClientFolder(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            newDirName.setText("");
            newTitle.setText("Введите название папки");
            newDirPane.setVisible(true);
        });
        okBtn.setOnMouseClicked(e -> {
            if (!newDirName.getText().equals("")) {
                try {
                    Files.createDirectory(clientDir.resolve(newDirName.getText()));
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
                newDirPane.setVisible(false);
                updateClientView();
            }
        });
        cancel.setOnMouseClicked(e -> {
            newDirPane.setVisible(false);
        });
    }

    @FXML
    public void delInClient(ActionEvent actionEvent) {
        if (!clientView.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> {
                newDirName.setVisible(false);
                newTitle.setText("Вы действительно хотите удалить?");
                newDirPane.setVisible(true);
            });
            okBtn.setOnMouseClicked(e -> {
                try {
                    delete(clientDir.resolve(clientView.getSelectionModel().getSelectedItem()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                newDirPane.setVisible(false);
                newDirName.setVisible(true);
                updateClientView();
            });
            cancel.setOnMouseClicked(e -> {
                newDirPane.setVisible(false);
                newDirName.setVisible(true);
            });
        }
    }
}
