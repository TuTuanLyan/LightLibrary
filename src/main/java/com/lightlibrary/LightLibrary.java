package com.lightlibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LightLibrary extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LightLibrary.class.getResource("Views/LoginAndRegister.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 640);
        stage.setTitle("Light Library");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("Images/LightLibraryLogo.png")).toExternalForm()));
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}