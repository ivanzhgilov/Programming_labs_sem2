package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import managers.NetworkManager;
import protocol.CommandResponse;
import structs.Coordinates;
import structs.MusicBand;
import structs.MusicGenre;
import structs.Studio;

import java.util.Optional;

public class BandEditDialog {
    private final Stage stage;
    private final MusicBand originalBand;
    private final boolean isEditMode;
    private final VBox root = new VBox(15);

    private final TextField nameField = new TextField();
    private final TextField xField = new TextField();
    private final TextField yField = new TextField();
    private final TextField participantsField = new TextField();
    private final ComboBox<MusicGenre> genreBox = new ComboBox<>();
    private final TextField studioField = new TextField();

    public BandEditDialog(Stage owner, MusicBand band) {
        this.stage = new Stage();
        this.originalBand = band;
        this.isEditMode = (band != null);

        setupUI();

        if (isEditMode) {
            fillFields();
        }

        Scene scene = new Scene(root, 400, 450);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
    }

    private void setupUI() {
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        LocalizationManager loc = LocalizationManager.getInstance();

        Label title = new Label();
        title.textProperty().bind(loc.createBinding(isEditMode ? "edit.title" : "add.title"));
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        addFormRow(grid, 0, "Name:", nameField);
        addFormRow(grid, 1, "X Coord:", xField);
        addFormRow(grid, 2, "Y Coord:", yField);
        addFormRow(grid, 3, "Participants:", participantsField);

        genreBox.getItems().addAll(MusicGenre.values());
        addFormRow(grid, 4, "Genre:", genreBox);
        addFormRow(grid, 5, "Studio Address:", studioField);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button();
        saveButton.textProperty().bind(loc.createBinding("save.button"));
        saveButton.setOnAction(e -> handleSave());

        buttonBox.getChildren().add(saveButton);

        if (isEditMode) {
            Button deleteButton = new Button();
            deleteButton.textProperty().bind(loc.createBinding("delete.button"));
            deleteButton.setStyle("-fx-background-color: #ffcccc;");
            deleteButton.setOnAction(e -> handleDelete());
            buttonBox.getChildren().add(deleteButton);
        }

        root.getChildren().addAll(title, grid, buttonBox);
    }

    private void addFormRow(GridPane grid, int row, String labelText, javafx.scene.Node field) {
        grid.add(new Label(labelText), 0, row);
        grid.add(field, 1, row);
    }

    private void fillFields() {
        nameField.setText(originalBand.getName());
        xField.setText(String.valueOf(originalBand.getCoordinates().getX()));
        yField.setText(String.valueOf(originalBand.getCoordinates().getY()));
        participantsField.setText(String.valueOf(originalBand.getNumberOfParticipants()));
        genreBox.setValue(originalBand.getGenre());
        studioField.setText(originalBand.getStudio().getAddress());
    }

    private void handleSave() {
        try {
            // Базовая валидация
            String name = nameField.getText();
            int x = Integer.parseInt(xField.getText());
            double y = Double.parseDouble(yField.getText());
            int participants = Integer.parseInt(participantsField.getText());
            MusicGenre genre = genreBox.getValue();
            String studioAddr = studioField.getText();

            MusicBand band = MusicBand.builder()
                    .name(name)
                    .coordinates(new Coordinates(x, y))
                    .numberOfParticipants(participants)
                    .genre(genre)
                    .studio(new Studio(studioAddr))
                    .build();

            CommandResponse resp;
            if (isEditMode) {
                MusicBand bandWithId = MusicBand.builder()
                        .id(originalBand.getId())
                        .name(name)
                        .coordinates(new Coordinates(x, y))
                        .numberOfParticipants(participants)
                        .genre(genre)
                        .studio(new Studio(studioAddr))
                        .build();
                resp = NetworkManager.getInstance().sendUpdate(bandWithId);
            } else {
                resp = NetworkManager.getInstance().sendAdd(band);
            }

            if (resp != null && resp.success()) {
                stage.close();
            } else {
                showError(resp == null ? "Network error" : resp.message());
            }

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for coordinates and participants");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void handleDelete() {
        if (originalBand == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение удаления");
        confirm.setHeaderText("Вы уверены, что хотите удалить эту группу?");
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                CommandResponse resp = NetworkManager.getInstance().sendRemoveById(originalBand.getId());
                if (resp != null && resp.success()) {
                    stage.close();
                } else {
                    showError(resp == null ? "Network error" : resp.message());
                }
            }
        });
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}
