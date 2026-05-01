package com.svgs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import eu.hansolo.fx.charts.Axis;
import eu.hansolo.fx.charts.AxisType;
import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.Grid;
import eu.hansolo.fx.charts.Position;
import eu.hansolo.fx.charts.XYChart;
import eu.hansolo.fx.charts.XYPane;
import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class SaveViewer{
    private static final int[] csvColumns = {1, 3, 4, 5, 2, 6, 7, 8, 9};
    private static final DateTimeFormatter timeStuff = DateTimeFormatter.ofPattern("HH:mm:ss");

    FileChooser fileChooser = new FileChooser();
    Path saveDir = Paths.get("src", "main", "java", "com", "svgs", "saves");
    private File selectedFile;
    private final ToggleGroup poop = new ToggleGroup();

    @FXML
    private Button saveSelector;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private HBox space;

    @FXML
    private Button exitButton;

    @FXML
    private StackPane chartHolder;

    @FXML
    private VBox metricOptions;

    @FXML
    void initialize() {
        for (int i = 0; i < SecondaryController.getMetricNames().size(); i++) {
            RadioButton button = new RadioButton(SecondaryController.getMetricNames().get(i));
            button.setToggleGroup(poop);
            button.setUserData(i);
            metricOptions.getChildren().add(button);
        }

        poop.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> updateChart());
        if (!poop.getToggles().isEmpty()) {
            poop.selectToggle(poop.getToggles().get(0));
        }
    }

    @FXML
    void pickSave(ActionEvent event) {
        fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("CSV Files","*.csv"));
        fileChooser.setTitle("Select a Save.");
        File saveFolder = saveDir.toFile();
        if (saveFolder.exists()) {
            fileChooser.setInitialDirectory(saveFolder);
        }

        File selected = fileChooser.showOpenDialog(App.scene.getWindow());

        if(selected == null){return;}

        selectedFile = selected;
        saveSelector.setText(selectedFile.getName());
        updateChart();
    }

    private void updateChart() {
        Toggle selectedToggle = poop.getSelectedToggle();
        if (selectedFile == null || selectedToggle == null) {
            return;
        }

        int metricIndex = (int) selectedToggle.getUserData();
        try {
            chartHolder.getChildren().setAll(buildChart(selectedFile.toPath(), metricIndex));
        } catch (Exception e) {
            chartHolder.getChildren().clear();
            System.out.println("Could not load save graph");
            System.out.println(e);
        }
    }

    private XYChart<XYChartItem> buildChart(Path csvFile, int metricIndex) throws IOException {
        List<XYChartItem> items = readChartItems(csvFile, metricIndex);
        XYSeries<XYChartItem> series = new XYSeries<>(
            items,
            ChartType.LINE,
            SecondaryController.getMetricNames().get(metricIndex),
            Color.web("#31a6ff"),
            Color.web("#31a6ff"),
            false
        );

        double maxX = items.isEmpty() ? 1 : items.get(items.size() - 1).getX();
        double minY = items.stream().mapToDouble(XYChartItem::getY).min().orElse(0);
        double maxY = items.stream().mapToDouble(XYChartItem::getY).max().orElse(1);
        if (minY == maxY) {
            minY -= 1;
            maxY += 1;
        }

        Axis xAxis = new Axis(0, Math.max(maxX, 1), Orientation.HORIZONTAL, AxisType.LINEAR, Position.BOTTOM, "Time (seconds)");
        Axis yAxis = new Axis(minY, maxY, Orientation.VERTICAL, AxisType.LINEAR, Position.LEFT, SecondaryController.getMetricNames().get(metricIndex));
        xAxis.setAutoScale(true);
        yAxis.setAutoScale(true);

        XYPane<XYChartItem> pane = new XYPane<>(series);
        Grid grid = new Grid(xAxis, yAxis);
        XYChart<XYChartItem> chart = new XYChart<>(pane, grid, xAxis, yAxis);
        chart.setTitle(SecondaryController.getMetricNames().get(metricIndex));
        chart.setSubTitle(selectedFile.getName());
        chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return chart;
    }

    private List<XYChartItem> readChartItems(Path csvFile, int metricIndex) throws IOException {
        List<XYChartItem> items = new ArrayList<>();
        int columnIndex = csvColumns[metricIndex];
        long firstTime = -1;

        try (BufferedReader reader = Files.newBufferedReader(csvFile)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length <= columnIndex) {
                    continue;
                }

                long timeMillis = Long.parseLong(parts[0].trim());
                if (firstTime < 0) {
                    firstTime = timeMillis;
                }

                double elapsedSeconds = (timeMillis - firstTime) / 1000.0;
                double value = Double.parseDouble(parts[columnIndex].trim());
                LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
                items.add(new XYChartItem(elapsedSeconds, value, timestamp.format(timeStuff)));
            }
        }

        return items;
    }

    @FXML
    void leave(ActionEvent event) throws IOException{
        App.setRoot("mainScreen");
    }
}
