package by.bsuir.controllers;

import by.bsuir.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class AuthorizationController implements Initializable {

    @FXML
    private ChoiceBox<String> networkInterfaceIpChoiceBox;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button connectButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        networkInterfaceIpChoiceBox.getItems().addAll(getNetworkInterfaceIpList());
        networkInterfaceIpChoiceBox.setValue(networkInterfaceIpChoiceBox.getItems().get(0));
        connectButton.setOnAction(this::connectButtonActionHandler);
    }

    private void connectButtonActionHandler(ActionEvent event) {
        try {
            // Получение ввода
            String ip = networkInterfaceIpChoiceBox.getValue();
            String name = nameTextField.getText();
            // Загрузка
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ChatView.fxml"));
            Parent parent = fxmlLoader.load();
            // Подключение к сети
            ChatController chatController = fxmlLoader.getController();
            chatController.connect(name, InetAddress.getByName(ip));
            // Отображение
            App.getMainScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getNetworkInterfaceIpList() {
        List<String> list = new LinkedList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (inetAddress instanceof Inet4Address) {
                            list.add(inetAddress.getHostAddress());
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
