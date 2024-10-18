package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class loadBookInMainScreen implements Initializable {
    @FXML
    private HBox HBox1;
    /*@FXML
    public void init(){
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = DatabaseConnection.getConnection();
        if (connection == null) {
            System.out.println("Something were wrong connectDB is null!");
        }
        String connectQuery = "SELECT * FROM books WHERE isbn = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(connectQuery);
            preparedStatement.setString(1,"1234566");
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){

                Image image1 = new Image(resultSet.getString("thumbnail"));
                imageView1.setImage(image1);
                nameBook1.setText(resultSet.getString("title"));

            }
            else System.out.println("resultSet is null");

        } catch(SQLException e){
            e.printStackTrace();
        }
    }*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init(HBox1);
    }

    public void init(HBox hBox) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = DatabaseConnection.getConnection();
        if (connection == null) {
            System.out.println("Something were wrong connectDB is null!");
        }
        try {
            String connectQuery = "SELECT * FROM books order by readed desc";
            PreparedStatement preparedStatement = connection.prepareStatement(connectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            int index = 0;
            while(resultSet.next()) {
                AnchorPane tmp = (AnchorPane) hBox.getChildren().get(index);
                ImageView imgView = (ImageView) tmp.getChildren().get(0);
                imgView.setImage(new Image(resultSet.getString("thumbnail")));
                Label titleLabel = (Label) tmp.getChildren().get(1);
                titleLabel.setText(resultSet.getString("title"));
                index++;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
