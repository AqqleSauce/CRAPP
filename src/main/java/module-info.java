module com.svgs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires obd.java.api;

    opens com.svgs to javafx.fxml;
    exports com.svgs;
}
