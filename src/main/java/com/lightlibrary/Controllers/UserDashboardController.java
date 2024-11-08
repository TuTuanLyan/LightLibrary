package com.lightlibrary.Controllers;

import com.google.api.services.books.v1.model.Volume;
import com.lightlibrary.Models.DatabaseConnection;
import com.lightlibrary.Models.GoogleBooksAPIClient;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.xml.transform.Result;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    @FXML
    private Pane avatarContainer;

    @FXML
    private ImageView avatarImage;

    @FXML
    private AnchorPane individualButtonContainer;
    private static boolean individualBarShown = false;

    @FXML
    private Button dashboardButton;

    @FXML
    private AnchorPane navigationButtonBorder;
    @FXML
    private Pane subButtonBorder;

    @FXML
    private Button issueBookButton;

    @FXML
    private Button returnBookButton;

    @FXML
    private Button supportButton;

    @FXML
    private AnchorPane mainContentContainer;

    @FXML
    private Pane dashboardContent;

    @FXML
    private Pane issueBookContent;

    @FXML
    private Pane returnBookContent;

    @FXML
    private Pane supportContent;

    @FXML
    private Circle searchLoadingDot1;

    @FXML
    private Circle searchLoadingDot2;

    @FXML
    private Circle searchLoadingDot3;

    @FXML
    private ScrollPane dashBoard;

    @FXML
    private HBox HBox1;

    @FXML
    private TextField searchBar;

    @FXML
    private AnchorPane searchResult_AnchorPane;

    @FXML
    private AnchorPane inforBook;

    private String lastInput = "";


    /**
     * Enum representing the active navigation button on the dashboard.
     */
    enum ActiveButton {
        DASHBOARD,
        ISSUE_BOOK,
        RETURN_BOOK,
        SUPPORT
    }

    private static ActiveButton activeButton;

    /**
     * Initializes the User Dashboard controller, setting up event handlers and UI elements.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        individualButtonContainer.setVisible(false);
        Circle avatarClip = new Circle(28, 28, 28);
        avatarContainer.setClip(avatarClip);

        dashboardButton.getStyleClass().add("selected");
        activeButton = ActiveButton.DASHBOARD;

        dashboardButton.setOnAction(e -> {
            handleNavigationButtonBorder(dashboardButton);
            //swapMainContentAnimation(dashboardContent);
            activeButton = ActiveButton.DASHBOARD;
        });
        issueBookButton.setOnAction(e -> {
            handleNavigationButtonBorder(issueBookButton);
            swapMainContentAnimation(issueBookContent);
            activeButton = ActiveButton.ISSUE_BOOK;
        });
        returnBookButton.setOnAction(e -> {
            handleNavigationButtonBorder(returnBookButton);
            swapMainContentAnimation(returnBookContent);
            activeButton = ActiveButton.RETURN_BOOK;
        });
        supportButton.setOnAction(e -> {
            handleNavigationButtonBorder(supportButton);
            swapMainContentAnimation(supportContent);
            activeButton = ActiveButton.SUPPORT;
        });
        AutoLoadTopRead(HBox1);
    }

    /**
     * Handles the visibility of the individual button bar and animates its sliding action.
     *
     * @param event the ActionEvent triggered by clicking the button
     */
    @FXML
    protected void handleIndividualBarAction(ActionEvent event) {
        TranslateTransition individualBarTransition = new TranslateTransition();
        individualBarTransition.setNode(individualButtonContainer);
        individualBarTransition.setDuration(Duration.seconds(0.35));

        if (!individualBarShown) {
            individualButtonContainer.setVisible(true);
            individualBarTransition.setToY(110);
            individualBarShown = true;
        } else {
            individualBarTransition.setToY(-35);
            individualBarTransition.setOnFinished(e -> {
                individualButtonContainer.setVisible(false);
                individualBarShown = false;
            });
        }

        individualBarTransition.play();
    }

    /**
     * Handles the movement of the navigation button border and updates the selected button's style.
     *
     * @param activeButton the button that was clicked and is now active
     */
    private void handleNavigationButtonBorder(Button activeButton) {
        TranslateTransition navigationBarTransition = new TranslateTransition();
        navigationBarTransition.setNode(navigationButtonBorder);
        navigationBarTransition.setDuration(Duration.seconds(0.2));
        navigationBarTransition.setToY(activeButton.getLayoutY() - navigationButtonBorder.getLayoutY());
        navigationBarTransition.play();

        TranslateTransition subButtonBorderTransition = new TranslateTransition();
        subButtonBorderTransition.setNode(subButtonBorder);
        subButtonBorderTransition.setDuration(Duration.seconds(0.2));
        subButtonBorderTransition.setToY(activeButton.getLayoutY() - subButtonBorder.getLayoutY());
        subButtonBorderTransition.play();

        dashboardButton.getStyleClass().remove("selected");
        issueBookButton.getStyleClass().remove("selected");
        returnBookButton.getStyleClass().remove("selected");
        supportButton.getStyleClass().remove("selected");

        activeButton.getStyleClass().add("selected");
    }

    /**
     * Logs the user out and switches the scene back to the Login and Register view.
     *
     * @param event the ActionEvent triggered by clicking the logout button
     * @throws IOException if the Login and Register FXML file cannot be loaded
     */
    @FXML
    protected void handleLogoutAction(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("/com/lightlibrary/Views/LoginAndRegister.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(login, 960, 640));
        stage.show();
    }

    /**
     * Handle animation in main content when swapped.
     *
     * @param newContent is the new content Pane user want to go.
     */
    private void swapMainContentAnimation(Pane newContent) {
        Pane currentContent = switch (activeButton) {
            case DASHBOARD -> issueBookContent;
            case ISSUE_BOOK -> issueBookContent;
            case RETURN_BOOK -> returnBookContent;
            case SUPPORT -> supportContent;
        };

        if (currentContent == newContent) {
            return;
        }

        FadeTransition currentContentTransition = new FadeTransition(Duration.seconds(0.3), currentContent);
        currentContentTransition.setFromValue(1.0);
        currentContentTransition.setToValue(0.0);
        currentContentTransition.play();
        currentContentTransition.setOnFinished(e -> {
            currentContent.setVisible(false);
        });

        newContent.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), newContent);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void loadingAction() {
        animateDot(searchLoadingDot1, 0);
        animateDot(searchLoadingDot2, 200);
        animateDot(searchLoadingDot3, 400);
    }

    private void animateDot(Circle dot, int initialDelay) {
        Timeline movement = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(dot.translateYProperty(), 0)),
                new KeyFrame(Duration.millis(250), new KeyValue(dot.translateYProperty(), -5)),
                new KeyFrame(Duration.millis(500), new KeyValue(dot.translateYProperty(), 0))
        );

        PauseTransition pause = new PauseTransition(Duration.millis(200));

        SequentialTransition fullCycle = new SequentialTransition(movement, pause);
        fullCycle.setCycleCount(SequentialTransition.INDEFINITE);

        PauseTransition initialPause = new PauseTransition(Duration.millis(initialDelay));
        initialPause.setOnFinished(e -> fullCycle.play());

        initialPause.play();
    }

    private String getISBN(Volume.VolumeInfo volumeInfo) {
        if (volumeInfo.getIndustryIdentifiers() != null) {
            for (Volume.VolumeInfo.IndustryIdentifiers identifier : volumeInfo.getIndustryIdentifiers()) {
                if ("ISBN_13".equals(identifier.getType())) {
                    return identifier.getIdentifier();
                }
            }
        }
        return "N/A";
    }

    public void returnSearchResult() {
        handleNavigationButtonBorder(dashboardButton);
        //swapMainContentAnimation(dashboardContent);
        activeButton = ActiveButton.DASHBOARD;
        inforBook.setDisable(true);
        inforBook.setVisible(false);
        searchResult_AnchorPane.setVisible(true);

    }

    public void exitInfoBookAction() {
        handleNavigationButtonBorder(dashboardButton);
        //swapMainContentAnimation(dashboardContent);
        activeButton = ActiveButton.DASHBOARD;
        inforBook.setVisible(false);
        inforBook.setDisable(true);

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
                            "order by readed desc limit 3";
            PreparedStatement preparedStatement = connection.prepareStatement(connectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            int index = 0;
            while (resultSet.next()) {
                AnchorPane anchorPane = (AnchorPane) hBox.getChildren().get(index);

                Image image = new Image(resultSet.getString("thumbnail"));
                ImageView imgView = (ImageView) anchorPane.getChildren().get(0);
                imgView.setX((anchorPane.getLayoutX() / +imgView.getLayoutX()));
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


    public void search() {
        String input = searchBar.getText();

        // Xử lí va chạm, chưa có thay đổi button, lưu để để còn chuyển cảnh button hợp lí

        // Xử lí onClicked với mouse, nếu như lastInput chưa thay đổi, và thay kết quả đang ẩn, thì hiển thị lại
        searchBar.setOnMouseClicked(mouseEvent -> {
            inforBook.setDisable(true);
            inforBook.setVisible(false);
            if (searchBar.getText().equals(lastInput) && !searchResult_AnchorPane.isVisible()) {
                searchResult_AnchorPane.setVisible(true);
            }
            handleNavigationButtonBorder(dashboardButton);
            //swapMainContentAnimation(dashboardContent);
            activeButton = ActiveButton.DASHBOARD;
        });

        // Xử lí Type, nếu như bàn phím được bấm(type) thì tự động ẩn thanh kết quả
        searchBar.setOnKeyTyped(keyEvent -> {
            if (!searchBar.getText().equals(lastInput) && searchResult_AnchorPane.isVisible()) {
                searchResult_AnchorPane.setVisible(false);
            }
        });

        searchBar.setOnKeyPressed(keyEvent -> {
            if (searchBar.getText().equals(lastInput)) {
                if (inforBook.isVisible()) {
                    inforBook.setVisible(false);
                }
            }
        });

        if (input.isEmpty()) {
            searchResult_AnchorPane.setVisible(false);
            return;
        }
        if (!searchBar.getText().equals(lastInput)) {
            lastInput = input;
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
                        if (books.get(i).getVolumeInfo().getAuthors() == null) {
                            s_author += "null";
                        } else {
                            for (int j = 0; j < books.get(i).getVolumeInfo().getAuthors().size(); j++) {
                                s_author += books.get(i).getVolumeInfo().getAuthors().get(j) + ", ";
                            }

                            s_author = s_author.substring(0, s_author.length() - 2);
                        }
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

                        // Chỗ này dùng để hiển thị infoBook nhé, sau khi render InfoBook xong thì bản thân nó sẽ có các hàm kế tiếp

                        //buộc phải để như này nhé, nếu truyền (Volume) books.get(i) vô hàm render sẽ lỗi nha
                        Volume volume = books.get(i);
                        anchorPane.setOnMouseClicked(event -> {
                            System.out.println(inforBook.getChildren().getFirst());
                            RenderBook(inforBook, volume);
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
        } else {
            searchResult_AnchorPane.setVisible(true);
        }
    }

    public void RenderBook(AnchorPane inforBook, Volume volume) {
        //Gọi các phần tử có trong inforBook
        Pane pane = (Pane) inforBook.getChildren().getFirst();
        pane.setStyle("-fx-background-color:lightblue;");
        ImageView imgView = (ImageView) pane.getChildren().get(1);
        Label titleLabel = (Label) pane.getChildren().get(2);
        Label authorLabel = (Label) pane.getChildren().get(3);
        Label publisherLabel = (Label) pane.getChildren().get(4);
        Label publishDateLabel = (Label) pane.getChildren().get(5);
        Button addbutton = (Button) pane.getChildren().get(7);
        Button borrowbutton = (Button) pane.getChildren().get(9);
        Font font = new Font("Arial", 12);                  //cái này để setFont, nếu dùng css thì bỏ đi cũng được


        // Bắt đầu ghi đè thông tin
        //image
        Image image = new Image(volume.getVolumeInfo().getImageLinks().getThumbnail());
        imgView.setFitHeight(image.getHeight() * 1.5);
        imgView.setFitWidth(image.getWidth() * 1.5);
        imgView.setImage(image);

        //title
        titleLabel.setText(volume.getVolumeInfo().getTitle());
        titleLabel.setFont(font);

        //author
        String s_author = "Author: ";
        if (volume.getVolumeInfo().getAuthors() == null) {
            s_author += "null";
        } else {
            for (int j = 0; j < volume.getVolumeInfo().getAuthors().size(); j++) {
                s_author += volume.getVolumeInfo().getAuthors().get(j) + ", ";
            }

            s_author = s_author.substring(0, s_author.length() - 2);
        }
        authorLabel.setText(s_author);
        authorLabel.setFont(font);

        //publisher
        publisherLabel.setText("Publisher: " + volume.getVolumeInfo().getPublisher());
        publisherLabel.setFont(font);

        //publishDate
        publishDateLabel.setText("PublishDate: " + volume.getVolumeInfo().getPublishedDate());
        publishDateLabel.setFont(font);

        // Ban đầu InfoBook bị ẩn, hiển thị nó,đồng thời ẩn thanh kết quả tìm kiếm đi.
        inforBook.setDisable(false);
        inforBook.setVisible(true);
        searchResult_AnchorPane.setVisible(false);

        // Set OnClicked cho AddButton, khi bấm vào thì gọi Func add().

        addbutton.setOnMouseClicked(mouseEvent -> {
            // Lấy data từ thanh số lượng, nếu đang để rỗng thì mặc định sẽ là 1.
            TextField numberTextField = (TextField) pane.getChildren().get(8);
            String number = numberTextField.getText();
            if (number.isEmpty()) number = "1";
            try {
                int num = Integer.parseInt(number);
                add(volume, num);
            } catch (NumberFormatException e) {
                // input không phải số thì cảnh báo
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Wrong input number!");
                alert.showAndWait();
            }
            numberTextField.clear(); // sau khi add xong thì tự động clear thanh input.
        });

        // borrow, hiện tại chưa có gì.
        borrowbutton.setOnMouseClicked(mouseEvent -> {
            TextField numberTextField = (TextField) pane.getChildren().get(8);
            String number = numberTextField.getText();
            // mặc định thời gian hạn mượn sách là 7 ngày nếu không có nhập gì đầu vào cả.
            if (number.isEmpty()) number = "7";
            try {
                int num = Integer.parseInt(number);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("You want to borrow this books due to: " + LocalDate.now().plusDays(num).toString() + "?");
                alert.showAndWait();
                // nếu button.ok được bấm thì mới chạy hàm borrow, còn không thì sẽ không mượn.
                if (alert.getResult() == ButtonType.OK) {
                    borrow(2, volume, num);
                }

            } catch (NumberFormatException e) {
                // input không phải số thì cảnh báo
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Wrong input number!");
                alert.showAndWait();
            }

            numberTextField.clear(); // sau khi add xong thì tự động clear thanh input.
        });
        handleNavigationButtonBorder(issueBookButton);
        swapMainContentAnimation(issueBookContent);
        activeButton = ActiveButton.ISSUE_BOOK;


    }


    /**
     * New func for adding book.
     *
     * @param volume select the book you want to add.
     * @param number fill numbers of book.
     */
    public void add(Volume volume, int number) {
        String isbn = getISBN(volume.getVolumeInfo());
        // Xử lí Author
        String strAuthor = "";
        if (volume.getVolumeInfo().getAuthors() == null) {
            strAuthor += "null";
        } else {
            for (int j = 0; j < volume.getVolumeInfo().getAuthors().size(); j++) {
                strAuthor += volume.getVolumeInfo().getAuthors().get(j) + ", ";
            }

            strAuthor = strAuthor.substring(0, strAuthor.length() - 2);
        }

        // Chỉ dùng để test InfoBook hiện tại, không quan trọng
        System.out.println("Isbn: " + isbn);
        System.out.println("number: " + number);

        Connection connection = DatabaseConnection.getConnection();

        if (connection == null) {
            System.out.println("Something were wrong connectDB is null!");
        }

        try {
            String selectQuery = "SELECT * FROM books WHERE ISBN = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Kiểm tra xem đã có sách có mã isbn này chưa, nếu đã có thì chỉ cần update, nếu chưa có thì insert
            if (resultSet.next()) {
                String addQuery = "UPDATE books SET totalNumber = totalNumber + ? WHERE ISBN = ?";
                PreparedStatement preparedStatementAdd = connection.prepareStatement(addQuery);
                preparedStatementAdd.setInt(1, number);
                preparedStatementAdd.setString(2, isbn);
                int requiredUpdate = preparedStatementAdd.executeUpdate();
                if (requiredUpdate > 0) {
                    System.out.println("Update Successes");
                } else System.out.println("Update Failed");

            } else {
                String addQuery =
                        "INSERT into books (title,author,publisher,publishedDate,thumbnail,ISBN,totalNumber) values (?,?,?,?,?,?,?);";
                PreparedStatement preparedStatementAdd = connection.prepareStatement(addQuery);
                preparedStatementAdd.setString(1, volume.getVolumeInfo().getTitle());
                preparedStatementAdd.setString(2, strAuthor);
                preparedStatementAdd.setString(3, volume.getVolumeInfo().getPublisher());
                preparedStatementAdd.setString(4, volume.getVolumeInfo().getPublishedDate());
                preparedStatementAdd.setString(5, volume.getSelfLink());
                preparedStatementAdd.setString(6, getISBN(volume.getVolumeInfo()));
                preparedStatementAdd.setInt(7, number);
                int requiredInsert = preparedStatementAdd.executeUpdate();
                if (requiredInsert > 0) {
                    System.out.println("Add Successes");
                } else System.out.println("Add Failed");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * borrow books. userID get from class Customer.
     *
     * @param userID  userID.
     * @param volume  book.
     * @param addDate date from currentDate to return.
     */
    public void borrow(int userID, Volume volume, int addDate) {
        String ISBN = getISBN(volume.getVolumeInfo());

        Connection connection = DatabaseConnection.getConnection();

        if (connection == null) {
            System.out.println("Something were wrong connectDB is null!");
        }

        try {
            String selectQuery = "SELECT * FROM transactions WHERE userID = ? AND ISBN = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, ISBN);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Kiểm tra xem đã có sách có mã isbn này chưa, nếu đã có thì chỉ cần update, nếu chưa có thì insert
            if (resultSet.next()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Book is already borrowed!");
                alert.showAndWait();
            } else {
                String addQuery =
                        "INSERT into transactions (userID,ISBN,dueDate) values (?,?,?);";
                PreparedStatement preparedStatementBorrow = connection.prepareStatement(addQuery);
                preparedStatementBorrow.setInt(1, userID);
                preparedStatementBorrow.setString(2, ISBN);
                preparedStatementBorrow.setDate(3, Date.valueOf(LocalDate.now().plusDays(addDate)));
                int requiredInsert = preparedStatementBorrow.executeUpdate();
                if (requiredInsert > 0) {
                    System.out.println("Borrowed Successes");
                } else System.out.println("Borrowed Failed");

            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Foreign Key is null, this book does not exist in Library");

        }
    }
}