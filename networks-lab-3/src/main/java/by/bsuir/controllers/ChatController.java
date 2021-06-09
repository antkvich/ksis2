package by.bsuir.controllers;

import by.bsuir.App;
import by.bsuir.events.ConnectEvent;
import by.bsuir.events.DisconnectEvent;
import by.bsuir.events.TextEvent;
import by.bsuir.models.Chat;
import by.bsuir.models.messages.ConnectMessage;
import by.bsuir.models.messages.DisconnectMessage;
import by.bsuir.models.messages.TextMessage;
import by.bsuir.models.server.services.History;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    private String name;
    private Chat chat;

    @FXML
    private VBox messagesVBox;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private Button sendButton;

    public void connect(String name, InetAddress address) throws IOException {
        // Инициализация
        this.name = name;
        this.chat = new Chat();
        // Установка обработчиков событий
        chat.setOnConnected(this::chatConnectHandler);
        chat.setOnDisconnected(this::chatDisconnectHandler);
        chat.setOnTextMessageSent(this::chatTextMessageSentHandler);
        chat.setOnTextMessageReceived(this::chatTextMessageReceiveHandler);
        // Подключение к чату и получение истории локальной сети
        History history = chat.connect(address);
        // Отображение истории
        displayHistory(history);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        App.getMainWindow().setOnCloseRequest(this::windowCloseRequestHandler);
        messageTextArea.setOnKeyPressed(this::messageTextAreaKeyPressedHandler);
        sendButton.setOnAction(this::sendButtonActionHandler);
    }

    private void windowCloseRequestHandler(WindowEvent event) {
        chat.disconnect();
    }

    private void messageTextAreaKeyPressedHandler(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.isShiftDown()) {
                messageTextArea.appendText("\n");
            } else {
                sendTextMessage(messageTextArea.getText());
            }
            event.consume();
        }
    }

    private void sendButtonActionHandler(ActionEvent event) {
        sendTextMessage(messageTextArea.getText());
    }

    private void chatConnectHandler(ConnectEvent connectEvent) {
        displayConnectMessage(connectEvent.getConnectMessage());
    }

    private void chatDisconnectHandler(DisconnectEvent disconnectEvent) {
        displayDisconnectMessage(disconnectEvent.getDisconnectMessage());
    }

    private void chatTextMessageSentHandler(TextEvent textEvent) {
        displayTextMessage(textEvent.getTextMessage());
    }

    private void chatTextMessageReceiveHandler(TextEvent textEvent) {
        displayTextMessage(textEvent.getTextMessage());
    }

    private void sendTextMessage(String text) {
        if (!text.isBlank()) {
            // Отправка
            chat.sendTextMessage(name, text);
        }
        // Очистка поля ввода
        messageTextArea.clear();
    }

    private void displayHistory(History history) {
        history.forEach(message -> {
            if (message instanceof ConnectMessage) {
                displayConnectMessage((ConnectMessage) message);
            } else if (message instanceof DisconnectMessage) {
                displayDisconnectMessage((DisconnectMessage) message);
            } else if (message instanceof TextMessage) {
                displayTextMessage((TextMessage) message);
            }
        });
    }

    private synchronized void displayConnectMessage(ConnectMessage connectMessage) {
        String str = connectMessage.getAddress().getHostName() + " connected (" + connectMessage.getTime() + ")";
        Label label = new Label(str);
        Platform.runLater(() -> messagesVBox.getChildren().add(label));
    }

    private synchronized void displayDisconnectMessage(DisconnectMessage disconnectMessage) {
        String str = disconnectMessage.getAddress().getHostName() + " disconnected (" + disconnectMessage.getTime() + ")";
        Label label = new Label(str);
        Platform.runLater(() -> messagesVBox.getChildren().add(label));
    }

    private synchronized void displayTextMessage(TextMessage textMessage) {
        Label nameLabel = new Label(textMessage.getName() + ":");
        Label messageLabel = new Label(textMessage.getText());
        HBox hBox = new HBox(nameLabel, messageLabel);
        hBox.setSpacing(20);
        Platform.runLater(() -> messagesVBox.getChildren().add(hBox));
    }
}