package main;

import javafx.application.Application;
import javafx.stage.Stage;
import gui.AuthWindow;

public class GuiClientApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        AuthWindow authWindow = new AuthWindow(primaryStage);
        authWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
