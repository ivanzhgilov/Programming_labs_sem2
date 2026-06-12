package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.GuiClientApplication;
import managers.NetworkManager;
import managers.SessionManager;
import protocol.CommandResponse;

import java.util.Locale;

public class AuthWindow {
    private final Stage stage;
    private final VBox root;
    private final TextField loginField;
    private final PasswordField passwordField;
    private final Button actionButton;
    private final Button switchButton;
    private final Label statusLabel;
    private final ComboBox<String> languageBox;

    private boolean isRegisterMode = false;

    public AuthWindow(Stage stage) {
        this.stage = stage;
        this.root = new VBox(10);
        this.root.setPadding(new Insets(20));
        this.root.setAlignment(Pos.CENTER);

        LocalizationManager loc = LocalizationManager.getInstance();

        // Language Selector
        this.languageBox = new ComboBox<>();
        languageBox.getItems().addAll("Русский", "Íslenska", "Français", "Español (HN)");
        languageBox.setValue("Русский");
        languageBox.setOnAction(e -> {
            String selected = languageBox.getValue();
            if ("Русский".equals(selected)) loc.setLocale(new Locale("ru"));
            else if ("Íslenska".equals(selected)) loc.setLocale(new Locale("is"));
            else if ("Français".equals(selected)) loc.setLocale(new Locale("fr"));
            else if ("Español (HN)".equals(selected)) loc.setLocale(new Locale("es", "HN"));
        });

        Label titleLabel = new Label();
        titleLabel.textProperty().bind(loc.createBinding("auth.title"));
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label loginLabel = new Label();
        loginLabel.textProperty().bind(loc.createBinding("auth.login"));

        this.loginField = new TextField();

        Label passLabel = new Label();
        passLabel.textProperty().bind(loc.createBinding("auth.password"));

        this.passwordField = new PasswordField();

        this.actionButton = new Button();
        updateActionButton();

        this.switchButton = new Button();
        updateSwitchButton();
        switchButton.setOnAction(e -> {
            isRegisterMode = !isRegisterMode;
            updateActionButton();
            updateSwitchButton();
        });

        this.statusLabel = new Label();

        root.getChildren().addAll(
            new Label("Language:"), languageBox,
            titleLabel,
            loginLabel, loginField,
            passLabel, passwordField,
            actionButton, switchButton,
            statusLabel
        );

        actionButton.setOnAction(e -> handleAction());

        Scene scene = new Scene(root, 300, 400);
        stage.setScene(scene);
    }

    private void updateActionButton() {
        LocalizationManager loc = LocalizationManager.getInstance();
        if (isRegisterMode) {
            actionButton.textProperty().bind(loc.createBinding("auth.register.button"));
        } else {
            actionButton.textProperty().bind(loc.createBinding("auth.login.button"));
        }
    }

    private void updateSwitchButton() {
        LocalizationManager loc = LocalizationManager.getInstance();
        // Note: In a real app we'd have keys for both "Switch to Reg" and "Switch to Login"
        switchButton.textProperty().bind(loc.createBinding("auth.switch"));
    }

    private void handleAction() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Fill all fields!");
            return;
        }

        NetworkManager nm = NetworkManager.getInstance();
        if (isRegisterMode) {
            // Registration Logic
            CommandResponse resp = nm.sendRegister(login, password);
            handleResponse(resp);
        } else {
            // Login Logic
            CommandResponse resp = nm.sendLogin(login, password);
            if (resp != null && resp.isSuccess()) {
                SessionManager.setSession(login, password);

                statusLabel.setText("Success!");
                // Transition to MainWindow
                javafx.application.Platform.runLater(() -> {
                    MainWindow mainWindow = new MainWindow(stage);
                    mainWindow.show();
                });
            } else {
                handleResponse(resp);
            }
        }
    }

    private void handleResponse(CommandResponse resp) {
        if (resp == null) {
            statusLabel.setText("Server unavailable");
        } else if (resp.isSuccess()) {
            statusLabel.setText("Operation successful!");
        } else {
            statusLabel.setText("Error: " + resp.getMessage());
        }
    }

    public void show() {
        stage.show();
    }
}
