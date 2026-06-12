package gui;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import structs.Coordinates;
import structs.MusicBand;

import java.util.HashMap;
import java.util.Map;

public class BandCanvas extends Pane {
    private final Map<Integer, Node> nodes = new HashMap<>();
    private static final double SCALE_FACTOR = 1.0; // Коэффициент масштабирования координат

    public BandCanvas() {
        this.setStyle("-fx-background-color: white; -fx-border-color: black;");
        this.setPrefSize(600, 600);
    }

    public void addOrUpdateBand(MusicBand band) {
        javafx.application.Platform.runLater(() -> {
            Node node = nodes.get(band.getId());
            Coordinates coords = band.getCoordinates();
            if (coords == null) return;

            double x = coords.getX() * SCALE_FACTOR;
            double y = coords.getY() * SCALE_FACTOR;

            if (node == null) {
                // Создание нового объекта
                Circle circle = new Circle(5, getColorForOwner(band.getOwnerId()));
                circle.setLayoutX(x);
                circle.setLayoutY(y);

                // Анимация "Pop-in"
                ScaleTransition scale = new ScaleTransition(Duration.millis(300), circle);
                scale.setFromX(0);
                scale.setFromY(0);
                scale.setToX(1);
                scale.setToY(1);

                FadeTransition fade = new FadeTransition(Duration.millis(300), circle);
                fade.setFromValue(0);
                fade.setToValue(1);

                ParallelTransition pt = new ParallelTransition(scale, fade);
                pt.play();

                circle.setOnMouseClicked(e -> {
                    // В реальном приложении здесь будет la.
                    // BandEditDialog требует MusicBand, поэтому нам нужно хранить связь
                    // или искать объект по ID.
                    // Для простоты выведем информацию
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Информация об объекте");
                    alert.setHeaderText(band.getName());
                    alert.setContentText("ID: " + band.getId() + "\n" +
                                         "Участников: " + band.getNumberOfParticipants() + "\n" +
                                         "Жанр: " + band.getGenre() + "\n" +
                                         "Студия: " + band.getStudio().getAddress());
                    alert.showAndWait();

                    // Чтобы открыть редактор, можно добавить кнопку "Редактировать" в Alert
                    // или вызвать диалог сразу.
                });

                nodes.put(band.getId(), circle);
                this.getChildren().add(circle);
            } else {
                // Плавное перемещение существующего объекта
                TranslateTransition translate = new TranslateTransition(Duration.millis(500), node);
                translate.setToX(x);
                translate.setToY(y);
                translate.play();
            }
        });
    }

    public void removeBand(int id) {
        javafx.application.Platform.runLater(() -> {
            Node node = nodes.remove(id);
            if (node != null) {
                this.getChildren().remove(node);
            }
        });
    }

    private Color getColorForOwner(int ownerId) {
        // Генерируем стабильный цвет на основе ownerId
        java.util.Random random = new java.util.Random(ownerId);
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }
}
