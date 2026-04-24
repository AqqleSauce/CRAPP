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
                .animated(true)
                .build();
                gauge.valueProperty().bind(ObdReader.boostProperty());
                //ObdReader.startBoostThread();
                ObdReader.gaugesToUse.add(ObdReader::getBoost);
                vbux.getChildren().add(gauge);
                break;

                case(1): gauge = GaugeBuilder.create()
                .skinType(SkinType.LINEAR)
                .title("Fuel Trim")
                .maxValue(50)
                .minValue(-50)
                .animated(true)
                .build();
                gauge.valueProperty().bind(ObdReader.trimProperty());
                ObdReader.gaugesToUse.add(ObdReader::getFuelTrim);             
                //gauge.valueProperty().bind(ObdReader);
                vbux.getChildren().add(gauge);
                break;
                
                case(3): gauge = GaugeBuilder.create()
                .skinType(SkinType.FLAT)
                .title("Coolant Temperature")
                .maxValue(120)
                .minValue(0)
                .animated(true)
                .build();
                gauge.valueProperty().bind(ObdReader.coolantProperty());
                ObdReader.gaugesToUse.add(ObdReader::getCoolantTemp);
                vbux.getChildren().add(gauge);
                break;

                case(2): gauge = GaugeBuilder.create()
                .skinType(SkinType.LCD)
                .title("Fuel Pressure")
                .animated(true)
                .build();
                gauge.valueProperty().bind(ObdReader.fuelPressureProperty());
                ObdReader.gaugesToUse.add(ObdReader::getFuelPressure);
                vbux.getChildren().add(gauge);
                break;

                case(4): gauge = GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .title("RPMS")
                .maxValue(7000)
                .minValue(0)
                .angleRange(120)
                .valueVisible(true)
                .animated(true)
                .build();
                gauge.valueProperty().bind(ObdReader.revProperty());
                //ObdReader.startRpmsThread();
                ObdReader.gaugesToUse.add(ObdReader::getRevs);
                vbux.getChildren().add(gauge);
                break;

                case(5): gauge = GaugeBuilder.create()
                .skinType(SkinType.MODERN)
                .title("Engine Load")
                .maxValue(100)
                .minValue(0)
                .valueVisible(true)
                .build();
                gauge.valueProperty().bind(ObdReader.loadProperty());
                ObdReader.gaugesToUse.add(ObdReader::getEngineLoad);
                vbux.getChildren().add(gauge);
                break;

                case(6): gauge = GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .title("Speed")
                .angleRange(120)
                .maxValue(160)
                .minValue(0)
                .valueVisible(true)
                .animated(true)
                .build();
                gauge.valueProperty().bind(ObdReader.speedProperty());
                ObdReader.gaugesToUse.add(ObdReader::getVehicleSpeed);
                vbux.getChildren().add(gauge);
                break;

                case(7): gauge = GaugeBuilder.create()
                .skinType(SkinType.LINEAR)
                .title("Throttle Position")
                .animated(true)
                .maxValue(100)
                .minValue(0)
                .valueVisible(true)
                .build();
                gauge.valueProperty().bind(ObdReader.throttleProperty());
                ObdReader.gaugesToUse.add(ObdReader::getThrottlePosition);
                vbux.getChildren().add(gauge);
                break;

                case(8): gauge = GaugeBuilder.create()
                .skinType(SkinType.SIMPLE_DIGITAL)
                .title("Timing Advance")
                .animated(true)
                .maxValue(100)
                .minValue(-100)
                .valueVisible(true)
                .build();
                gauge.valueProperty().bind(ObdReader.timingProperty());
                ObdReader.gaugesToUse.add(ObdReader::getTimingPosition);
                vbux.getChildren().add(gauge);
                break;
            }
        }
    }
}
