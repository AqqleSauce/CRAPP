package com.svgs;

import java.util.ArrayList;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class PrimaryController{
    ArrayList<Gauge> gaugeList = new ArrayList<>();

    @FXML
    private Button addGaugeButton;
    
    @FXML
    private AnchorPane poop;

    @FXML
    void makeGauge(ActionEvent event) {
        Gauge gauge = GaugeBuilder.create()
    .skinType(SkinType.GAUGE) // Choose a skin type (e.g., LEVEL, MODERN, AMP, etc.)
    .prefSize(400, 400)
    .title("Temperature")
    .unit("°C")
    .minValue(0)
    .maxValue(100)
    .barColor(Color.RED)
    .animated(true)
    .build();
    poop.getChildren().add(gauge);
    }

}
