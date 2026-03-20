module com.svgs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires obd.java.api;
    requires eu.hansolo.medusa;

    opens com.svgs to javafx.fxml;
    exports com.svgs;
}
