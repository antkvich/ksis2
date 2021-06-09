package by.bsuir;

import by.bsuir.controllers.ChatController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage mainWindow;
    private static Scene mainScene;

    public static Stage getMainWindow() {
        return mainWindow;
    }

    public static Scene getMainScene() {
        return mainScene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AuthorizationView.fxml"));
        Parent parent = fxmlLoader.load();
        mainScene = new Scene(parent);
        mainWindow = stage;
        // Отображение
        mainWindow.setScene(mainScene);
        mainWindow.setMaximized(true);
        mainWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
