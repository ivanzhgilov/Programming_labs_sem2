package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import structs.MusicBand;
import gui.BandEditDialog;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BandTable extends VBox {
    private final TableView<MusicBand> table = new TableView<>();
    private final ObservableList<MusicBand> masterData = FXCollections.observableArrayList();
    private final FilteredList<MusicBand> filteredData = new FilteredList<>(masterData, p -> true);
    private final SortedList<MusicBand> sortedData = new SortedList<>(filteredData);
    private final TextField searchField = new TextField();

    public BandTable() {
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        setupTable();
        setupFiltering();

        table.setRowFactory(tv -> {
            TableRow<MusicBand> row = new TableRow<>() {
                @Override
                protected void doubleMouseClicked() {
                    MusicBand band = getItem();
                    if (band != null) {
                        Stage stage = (Stage) table.getScene().getWindow();
                        new BandEditDialog(stage, band).show();
                    }
                }
            };
            return row;
        });

        this.getChildren().addAll(new Label("Поиск:"), searchField, table);
    }

    private void setupTable() {
        TableColumn<MusicBand, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<MusicBand, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<MusicBand, Date> dateCol = new TableColumn<>("Дата создания");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCreationDate()));
        dateCol.setCellFactory(col -> new DateTableCell());

        TableColumn<MusicBand, Integer> partCol = new TableColumn<>("Участники");
        partCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getNumberOfParticipants()));
        partCol.setCellFactory(col -> new NumberTableCell());

        TableColumn<MusicBand, String> genreCol = new TableColumn<>("Жанр");
        genreCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenre() == null ? "N/A" : cellData.getValue().getGenre().name()));

        TableColumn<MusicBand, String> studioCol = new TableColumn<>("Студия");
        studioCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudio() == null ? "N/A" : cellData.getValue().getStudio().getAddress()));

        table.getColumns().addAll(idCol, nameCol, dateCol, partCol, genreCol, studioCol);
        table.setItems(sortedData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
    }

    private static class DateTableCell extends TableCell<MusicBand, Date> {
        @Override
        protected void updateItem(Date item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                Locale locale = LocalizationManager.getInstance().getLocale();
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale).withZone(ZoneId.systemDefault());
                setText(formatter.format(item.toInstant()));
            }
        }
    }

    private static class NumberTableCell extends TableCell<MusicBand, Integer> {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                NumberFormat nf = NumberFormat.getInstance(LocalizationManager.getInstance().getLocale());
                setText(nf.format(item));
            }
        }
    }

    private void setupFiltering() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(band -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Использование Streams API для фильтрации по нескольким полям
                String lowerCaseFilter = newValue.toLowerCase();
                return Stream.of(
                    band.getName(),
                    String.valueOf(band.getId()),
                    band.getGenre() == null ? "" : band.getGenre().name(),
                    band.getStudio() == null ? "" : band.getStudio().getAddress()
                )
                .filter(java.util.Objects::nonNull)
                .anyMatch(s -> s.toLowerCase().contains(lowerCaseFilter));
            });
        });
    }

    public void updateData(java.util.List<MusicBand> newData) {
        masterData.setAll(newData);
    }
}
