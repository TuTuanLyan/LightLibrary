package com.lightlibrary.Controllers;

import com.google.api.services.books.v1.model.Volume;
import com.lightlibrary.Models.DatabaseConnection;
import com.lightlibrary.Models.GoogleBooksAPIClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MainPage implements Initializable {
    @FXML
    private HBox HBox1; //using for loading top read

    @FXML
    private TextField searchBar;
    @FXML

    private AnchorPane searchResult_AnchorPane;

    @FXML
    private AnchorPane inforBook;

    /**
     * This use for loading auto top readed books and searching
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AutoLoadTopRead(HBox1);
        if (!searchBar.getText().isEmpty()) {
            search();
        }
    }

    public void AutoLoadTopRead(HBox hBox) {
        Connection connection = DatabaseConnection.getConnection();

        if (connection == null) {
            System.out.println("Something were wrong connectDB is null!");
        }

        try {
            String connectQuery =
                    "select count(b.title and t.isbn) as readed, b.title, t.isbn,b.thumbnail from transactions t " +
                            "join books b on t.isbn = b.isbn " +
                            "group by t.isbn " +
                            "order by readed desc";
            PreparedStatement preparedStatement = connection.prepareStatement(connectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            int index = 0;
            while (resultSet.next()) {
                AnchorPane anchorPane = (AnchorPane) hBox.getChildren().get(index);

                Image image = new Image(resultSet.getString("thumbnail"));
                ImageView imgView = (ImageView) anchorPane.getChildren().get(0);

                imgView.setImage(image);

                Label titleLabel = (Label) anchorPane.getChildren().get(1);

                titleLabel.setText(resultSet.getString("title"));
                // phần set vị trí, tùy vào fxml của các anh, các anh sẽ tự điều chỉnh vị trí của imageView và title cho phù hợp
                index++;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void search() {
        searchBar.setOnMouseClicked(mouseEvent -> {
            inforBook.setDisable(true);
            inforBook.setVisible(false);
        });
        String input = searchBar.getText();
        if (input.isEmpty()) {
            searchResult_AnchorPane.setVisible(false);
            return;
        }
        GoogleBooksAPIClient googleBooksAPIClient = new GoogleBooksAPIClient();
        try {
            // Tìm kiếm sách với từ khóa
            List<Volume> books = googleBooksAPIClient.searchBooks(input);

            ScrollPane searchResult_ScrollPane = (ScrollPane) searchResult_AnchorPane.getChildren().getFirst();
            VBox searchResult_VBox = (VBox) searchResult_ScrollPane.getContent();

            // câu lệnh này để xóa các kết quả đã được gọi về từ trước, clear lại để hiển thị lượt kết quả mới
            searchResult_VBox.getChildren().clear();
            for (int i = 0; i < books.size(); i++) {
                // Tạo 1 AnchorPane lưu kết quả trả về từ apis và nhét vào ScrollPane
                {
                    //title
                    Label title = new Label("Name: " + books.get(i).getVolumeInfo().getTitle());
                    title.setFont(new Font("Arial", 12));

                    //author
                    String s_author = "Author: ";
                    for (int j = 0; j < books.get(i).getVolumeInfo().getAuthors().size(); j++) {
                        s_author += books.get(i).getVolumeInfo().getAuthors().get(j) + ", ";
                    }
                    s_author = s_author.substring(0, s_author.length() - 2);
                    Label author = new Label(s_author);
                    author.setFont(new Font("Arial", 12));

                    //img
                    Image img = new Image(books.get(i).getVolumeInfo().getImageLinks().getThumbnail());
                    ImageView imgView = new ImageView(img);

                    //anchorpane, anh chỉ cần biết đây là setup vị trí cho img, title và author là được
                    AnchorPane anchorPane = new AnchorPane();
                    anchorPane.setStyle("-fx-background-color:lightblue;");
                    anchorPane.setPrefSize(searchResult_VBox.getPrefWidth(), imgView.getFitHeight());

                    AnchorPane.setLeftAnchor(imgView, 10.0);
                    AnchorPane.setTopAnchor(imgView, 10.0);

                    AnchorPane.setLeftAnchor(title, img.getWidth() + 30.0);
                    AnchorPane.setTopAnchor(title, 30.0);


                    AnchorPane.setLeftAnchor(author, img.getWidth() + 30.0);
                    AnchorPane.setTopAnchor(author, 30 + title.getPrefHeight() + 30);

                    anchorPane.getChildren().addAll(imgView, title, author);

                    searchResult_VBox.getChildren().add(anchorPane);
                    //buộc phải để như này nhé, nếu truyền (Volume) books.get(i) vô hàm render sẽ lỗi nha
                    Volume volume = books.get(i);
                    anchorPane.setOnMouseClicked(event -> {
                        System.out.println(inforBook.getChildren().getFirst());
                        RenderBook(inforBook,volume);
                    });
                }
            }
            searchResult_AnchorPane.setVisible(true);
            System.out.println(inforBook);
            // Hiển thị thông tin sách, không cần thiết lắm nhưng tôi vẫn giữ
            googleBooksAPIClient.printBookInfo(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public void RenderBook(AnchorPane inforBook,Volume volume) {
        //Gọi các phần tử có trong inforBook
        Pane pane = (Pane) inforBook.getChildren().getFirst();
        pane.setStyle("-fx-background-color:lightblue;");
        ImageView imgView = (ImageView) pane.getChildren().get(1);
        Label titleLabel = (Label) pane.getChildren().get(2);
        Label authorLabel = (Label) pane.getChildren().get(3);
        Label publisherLabel = (Label) pane.getChildren().get(4);
        Label publishDateLabel = (Label) pane.getChildren().get(5);
        Font font = new Font("Arial", 12);


        // Bắt đầu ghi đè thông tin
        //image
        Image image = new Image(volume.getVolumeInfo().getImageLinks().getThumbnail());
        imgView.setFitHeight(image.getHeight()*1.5);
        imgView.setFitWidth(image.getWidth()*1.5);
        imgView.setImage(image);

        //title
        titleLabel.setText(volume.getVolumeInfo().getTitle());
        titleLabel.setFont(font);

        //author
        String s_author = "Author: ";
        for (int j = 0; j < volume.getVolumeInfo().getAuthors().size(); j++) {
            s_author += volume.getVolumeInfo().getAuthors().get(j) + ", ";
        }
        s_author = s_author.substring(0, s_author.length() - 2);
        authorLabel.setText(s_author);
        authorLabel.setFont(font);

        //publisher
        publisherLabel.setText("Publisher: " + volume.getVolumeInfo().getPublisher());
        publisherLabel.setFont(font);

        //publishDate
        publishDateLabel.setText("PublishDate: " + volume.getVolumeInfo().getPublishedDate());
        publishDateLabel.setFont(font);

        inforBook.setDisable(false);
        inforBook.setVisible(true);
        searchResult_AnchorPane.setVisible(false);
    }

    public void returnSearchResult() {
        inforBook.setDisable(true);
        inforBook.setVisible(false);
        searchResult_AnchorPane.setVisible(true);
    }
    public void exitInforBookAction() {
        inforBook.setVisible(false);
        inforBook.setDisable(true);
    }


}
