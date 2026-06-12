package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import managers.NetworkManager;
import managers.SessionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import structs.MusicBand;
import structs.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainWindow {
    private final Stage stage;
    private final BorderPane root;
    private final BandTable bandTable;
    private final BandCanvas bandCanvas;
    private final ComboBox<String> languageBox;

    public MainWindow(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();

        this.bandTable = new BandTable();
        this.bandCanvas = new BandCanvas();
        this.languageBox = new ComboBox<>();

        setupUI();
        setupNetworkListeners();
        syncData();
    }

    private void setupUI() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #ddd;");

        Label userLabel = new Label("Пользователь: " + SessionManager.getCurrentLogin());

        languageBox.getItems().addAll("Русский", "Íslenska", "Français", "Español (HN)");
        languageBox.setValue("Русский");
        languageBox.setOnAction(e -> {
            String selected = languageBox.getValue();
            LocalizationManager loc = LocalizationManager.getInstance();
            if ("Русский".equals(selected)) loc.setLocale(new Locale("ru"));
            else if ("Íslenska".equals(selected)) loc.setLocale(new Locale("is"));
            else if ("Français".equals(selected)) loc.setLocale(new Locale("fr"));
            else if ("Español (HN)".equals(selected)) loc.setLocale(new Locale("es", "HN"));
        });

        Button logoutButton = new Button("Выход");
        logoutButton.setOnAction(e -> {
            SessionManager.clearSession();
            AuthWindow auth = new AuthWindow(stage);
            auth.show();
        });

        Button addBandButton = new Button("Добавить группу");
        addBandButton.setOnAction(e -> {
            new BandEditDialog(stage, null).show();
        });

        topBar.getChildren().addAll(userLabel, new Label("Язык:"), languageBox, addBandButton, logoutButton);
        root.setTop(topBar);

        javafx.scene.control.SplitPane splitPane = new javafx.scene.control.SplitPane();
        splitPane.getItems().addAll(bandTable, bandCanvas);
        splitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);

        root.setCenter(splitPane);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Music Bands Collection");
    }

    private void setupNetworkListeners() {
        NetworkManager.getInstance().setOnMessageReceived(message -> {
            javafx.application.Platform.runLater(() -> {
                processServerMessage(message);
            });
        });
    }

    private void processServerMessage(String message) {
        // Формат: [EVENT_TYPE]:[OBJECT_ID]:[PAYLOAD]
        String[] parts = message.split(":", 3);
        if (parts.length < 2) return;

        String type = parts[0];
        int id = Integer.parseInt(parts[1]);

        if ("ADD".equals(type) || "UPDATE".equals(type)) {
            if (parts.length == 3) {
                try {
                    // В реальном приложении здесь будет десериализация MusicBand из payload
                    // используя SerializationUtils.deserialize
                    // Для примера пока оставим упрощенную версию, но подготовим структуру
                    MusicBand band = mockDeserialize(parts[2]);
                    // Примечание: в полноценной версии здесь будет:
                    // MusicBand band = SerializationUtils.deserialize(Base64.getDecoder().decode(parts[2]), MusicBand.class);

                    // Обновляем таблицу (в идеале - добавляем в список)
                    // Для этого нужно будет хранить ObservableList в MainWindow и передавать его в BandTable
                    bandCanvas.addOrUpdateBand(band);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if ("REMOVE".equals(type)) {
            bandCanvas.removeBand(id);
        }
    }

    private void syncData() {
        CommandRequest showRequest = new CommandRequest("show", null, null, null, null);
        CommandResponse response = NetworkManager.getInstance().sendRequestSync(showRequest);
        if (response != null && response.success()) {
            Object payload = response.payload();
            if (payload instanceof java.util.List<?> list) {
                java.util.List<MusicBand> bands = (java.util.List<MusicBand>) list;
                bandTable.updateData(bands);
                bands.forEach(bandCanvas::addOrUpdateBand);
            }
        }
    }

    private MusicBand mockDeserialize(String payload) {
        // Заглушка для десериализации
        return MusicBand.builder()
                .id(1)
                .name("Mock Band " + payload)
                .coordinates(new Coordinates(100, 100))
                .build();
    }

    public void show() {
        stage.show();
    }
}
