module com.lightlibrary {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.lightlibrary to javafx.fxml;
    exports com.lightlibrary;
    exports com.lightlibrary.Controllers;
    opens com.lightlibrary.Controllers to javafx.fxml;
}