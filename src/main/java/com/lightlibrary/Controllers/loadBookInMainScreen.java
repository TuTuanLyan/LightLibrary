package com.lightlibrary.Controllers;

import com.google.api.services.books.v1.model.Volume;
import com.lightlibrary.Models.DatabaseConnection;
import com.lightlibrary.Models.GoogleBooksAPIClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class loadBookInMainScreen implements Initializable {
    @FXML
    private HBox HBox1;

    @FXML
    private TextField searchBar;
    @FXML
    private AnchorPane searchResult_AnchorPane;
    @FXML
    private AnchorPane inforBook;


    @FXML
    private ImageView test;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init(HBox1);
        if(!searchBar.getText().isEmpty()){
            search();
        }
    }

    public void init(HBox hBox) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection == null) {
            System.out.println("Something were wrong connectDB is null!");
        }
        try {
            String connectQuery = "select count(b.title and t.isbn) as readed, b.title, t.isbn,b.thumbnail from transactions t\n" +
                    "join books b on t.isbn = b.isbn \n" +
                    "group by t.isbn\n" +
                    "order by readed desc;";
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

    @FXML
    public void search(){
        String input = searchBar.getText();
        if(input.isEmpty()){
            searchResult_AnchorPane.setVisible(false);
            return;
        }
        GoogleBooksAPIClient googleBooksAPIClient = new GoogleBooksAPIClient();
        try {
            // Tìm kiếm sách với từ khóa
            List<Volume> books = googleBooksAPIClient.searchBooks(input);
            ScrollPane searchResult_ScrollPane = (ScrollPane) searchResult_AnchorPane.getChildren().getFirst();
            VBox searchResult_VBox = (VBox) searchResult_ScrollPane.getContent();
            searchResult_VBox.getChildren().clear();
            for(int i = 0 ; i < books.size(); i++){
                // Tạo 1 AnchorPane lưu kết quả trả về từ apis và nhét vào ScrollPane
                {
                    Label title = new Label(books.get(i).getVolumeInfo().getTitle());
                    Image img = new Image(books.get(i).getVolumeInfo().getImageLinks().getThumbnail());
                    ImageView imgView = new ImageView(img);
                    AnchorPane tmp = new AnchorPane();
                    tmp.setPrefSize(searchResult_VBox.getPrefWidth(), imgView.getFitHeight());

                    AnchorPane.setLeftAnchor(imgView,10.0);
                    AnchorPane.setTopAnchor(imgView,10.0);

                    AnchorPane.setLeftAnchor(title,img.getWidth() + 30.0);
                    AnchorPane.setTopAnchor(title,30.0);

                    tmp.getChildren().addAll(imgView, title);

                    searchResult_VBox.getChildren().add(tmp);
                    Volume volume = books.get(i);
                    searchResult_VBox.setOnMouseClicked(event -> {
                        RenderBook(inforBook,volume);
                    });
                }
            }
            searchResult_AnchorPane.setVisible(true);
            System.out.println(inforBook);
            // Hiển thị thông tin sách
            googleBooksAPIClient.printBookInfo(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
    public void RenderBook(AnchorPane inforBook,Volume volume){
        ScrollPane scrollPane = (ScrollPane) inforBook.getChildren().getFirst();
        Pane pane = (Pane) scrollPane.getContent();
        ImageView imgView = (ImageView) pane.getChildren().get(0);
        Label titleLabel = (Label) pane.getChildren().get(1);
        Image img = new Image(volume.getVolumeInfo().getImageLinks().getThumbnail());
        imgView.setImage(img);
        titleLabel.setText(volume.getVolumeInfo().getTitle());
        inforBook.setVisible(true);
        searchResult_AnchorPane.setVisible(false);
        return;
    }
    public void button(){
        test.setVisible(true);
    }



}
