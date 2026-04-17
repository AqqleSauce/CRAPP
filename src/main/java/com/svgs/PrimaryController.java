package com.svgs;

import java.io.IOException;
import java.util.ArrayList;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML; // tried an animation thing for the gauges, didn't work, might try again
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class PrimaryController{
    ArrayList<Gauge> gaugeList = new ArrayList<>();

    @FXML
    private Button addGaugeButton;
    
    @FXML
    private AnchorPane poop;

    @FXML
    private HBox vbux;

    

    void makeGauge(ActionEvent event) {
        Gauge gauge = GaugeBuilder.create()
    .skinType(SkinType.BULLET_CHART) // Choose a skin type (e.g., LEVEL, MODERN, AMP, etc.)
    .prefSize(400, 400)
    .title("Temperature")
    .unit("°C")
    .minValue(0)
    .maxValue(100)
    .barColor(Color.RED)
    .animated(true)
    .build();
    vbux.getChildren().add(gauge);
    } //sort of a reference rn

    @FXML
    void addGaugeScreen(ActionEvent event) throws IOException {
        App.setRoot("gaugeList");
    }

    @FXML
    void initialize(){
        decider(); 
    }

    public void decider(){
        Gauge gauge;
        for(int i =0; i<Data.gauges.size(); i++){
            switch (Data.gauges.get(i)){
                case(0): gauge = GaugeBuilder.create()
                .title("Boost Pressure")
                .skinType(SkinType.KPI)
                .minValue(0)
                .maxValue(15)
                .threshold(8)
                .thresholdVisible(true)
                .valueVisible(true)
                .build();
                gauge.valueProperty().bind(ObdReader.boostProperty());
                ObdReader.startBoostThread();
                vbux.getChildren().add(gauge);
                break;

                case(1): gauge = GaugeBuilder.create()
                .skinType(SkinType.LINEAR)
                .title("Fuel Trim")
                .maxValue(15)
                .minValue(-15).build();
                //gauge.valueProperty().bind(ObdReader);
                vbux.getChildren().add(gauge);
                break;
                
                case(2): gauge = GaugeBuilder.create()
                .skinType(SkinType.QUARTER)
                .title("Coolant Temperature")
                .maxValue(80)
                .minValue(0)
                .build();
                vbux.getChildren().add(gauge);
                break;

                case(3):
                break;

                case(4): gauge = GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .title("RPMS")
                .maxValue(7000)
                .minValue(0)
                .angleRange(120)
                .valueVisible(true)
                .build();
                gauge.valueProperty().bind(ObdReader.revProperty());
                ObdReader.startRpmsThread();
                vbux.getChildren().add(gauge);
                break;

                case(5):
                break;

                case(6):
                break;

                case(7):
                break;

                case(8):
                break;
            }
        }
    }
}
