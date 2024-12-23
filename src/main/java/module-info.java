module com.lightlibrary {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.google.api.client;
    requires com.google.api.services.books;
    requires com.google.api.client.json.gson;
    requires javafx.media;
    requires org.glassfish.tyrus.server;
    requires java.desktop;
    requires mysql.connector.j;

    opens com.lightlibrary to javafx.fxml;
    exports com.lightlibrary;
    exports com.lightlibrary.Controllers;
    exports com.lightlibrary.Models;
    opens com.lightlibrary.Controllers to javafx.fxml;

    exports com.lightlibrary.Models.Chat;
}