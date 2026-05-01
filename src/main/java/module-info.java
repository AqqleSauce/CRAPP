module com.svgs {
    requires javafx.controls;
    requires javafx.fxml;
    requires obd.java.api;
    requires eu.hansolo.medusa;
    requires com.fazecast.jSerialComm;
    requires eu.hansolo.fx.charts;
    
    opens com.svgs to javafx.fxml;
    exports com.svgs;
}
