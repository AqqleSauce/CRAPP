package com.svgs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class SecondaryController {
    @FXML
    private Button addAddGaugeButton;

    @FXML
    private ListView<String> doodoo;
    
    ObservableList<String> names = FXCollections.observableArrayList(
      "Boost Pressure",
      "Fuel Trim",
      "Fuel Pressure",
      "Coolant Temperature",
      "Engine RPMs",
      "Engine Load",  
      "Vehicle Speed",
      "Throttle Position",
      "Timing Position"
    );

    @FXML
    void initialize(){
      doodoo.setItems(names);
    }

    @FXML
    void doTheThing(ActionEvent event){
      if(doodoo.getSelectionModel().getSelectedItem() == null){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("None Selected Warning");
        alert.setHeaderText("Select One! or not");
        alert.setContentText("You haven't selected a gauge type yet. If you want to return without changes, press the return button.");
        alert.showAndWait();
        return;
      }
      else{
        int selected = doodoo.getSelectionModel().getSelectedIndex();
        
        System.out.println(selected);
        Data.gauges.add(selected);
        try {
          App.setRoot("mainScreen");
        } catch (Exception e) {
          System.out.println("doTheThing error");
          System.out.println(e);
        }
      }

    }
    
}
