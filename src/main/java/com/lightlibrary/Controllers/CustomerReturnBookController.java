package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerReturnBookController implements Initializable, SyncAction {

    @FXML
    private AnchorPane returnBookRoot;

    @FXML
    private TextField fillAuthorTextField;

    @FXML
    private DatePicker fillBorrowDatePicker;

    @FXML
    private DatePicker fillDueDatePicker;

    @FXML
    private TextField fillISBNTextField;

    @FXML
    private TextField fillTitleTextField;

    @FXML
    private GridPane notReturnBookTable;


    @FXML
    private CustomerDashboardController parentController;

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    @Override
    public  void setParentController(AdminDashboardController parentController) {}

    @Override
    public void autoUpdate() {

    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
        sortReturn();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillISBNTextField.setOnKeyTyped(keyEvent -> sortReturn());
        fillAuthorTextField.setOnKeyTyped(keyEvent -> sortReturn());
        fillBorrowDatePicker.setOnKeyTyped(keyEvent -> sortReturn());
        fillBorrowDatePicker.setOnAction(event -> sortReturn());
        fillDueDatePicker.setOnAction(keyEvent -> sortReturn());
    }

    @Override
    public void setTheme(boolean darkMode) {
        returnBookRoot.getStylesheets().clear();
        if (darkMode) {
            returnBookRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            returnBookRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

    public void sortReturn() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Clear the grid before reloading new data
                Platform.runLater(() -> clearGrid());

                String query = buildQuery();
                try (Connection connectDB = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

                    setQueryParameters(preparedStatement);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        String isbn = resultSet.getString("isbn");
                        String title = resultSet.getString("title");
                        String author = resultSet.getString("author");
                        String borrowedDate = resultSet.getDate("borrowDate").toLocalDate().toString();
                        String dueDate = resultSet.getDate("dueDate").toLocalDate().toString();
                        Double priceOnce = resultSet.getDouble("price");
                        Platform.runLater(() -> addRow(isbn,title,author,borrowedDate,dueDate,priceOnce));
                    }

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }
        };

        // Run the task on a background thread
        new Thread(task).start();
    }
    private void clearGrid() {
        notReturnBookTable.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        notReturnBookTable.getRowConstraints().removeIf(row -> notReturnBookTable.getRowConstraints().indexOf(row) > 0);
    }


    private String buildQuery() {
        StringBuilder query = new StringBuilder("select t.isbn,b.title,b.author,t.borrowDate,t.dueDate,b.price from transactions t left join books b on b.isbn = t.isbn where t.userID = ? and t.returnDate is null ");
        if (!fillISBNTextField.getText().isEmpty()) query.append(" AND t.isbn LIKE ?");
        if (!fillTitleTextField.getText().isEmpty()) query.append(" AND b.title LIKE ?");
        if (!fillAuthorTextField.getText().isEmpty()) query.append(" AND b.author LIKE ?");
        if (fillBorrowDatePicker.getValue() != null) query.append(" AND t.borrowDate LIKE ?");
        if (fillDueDatePicker.getValue() != null) query.append(" AND t.dueDate LIKE ?");
        return query.toString();
    }

    private void setQueryParameters(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, parentController.getCustomer().getUserID());
        int index = 2;
        if (!fillISBNTextField.getText().isEmpty()) preparedStatement.setString(index++, "%" + fillISBNTextField.getText() + "%");
        if (!fillTitleTextField.getText().isEmpty()) preparedStatement.setString(index++, "%" + fillTitleTextField.getText() + "%");
        if (!fillAuthorTextField.getText().isEmpty()) preparedStatement.setString(index++, "%" + fillAuthorTextField.getText() + "%");
        if (fillBorrowDatePicker.getValue() != null) preparedStatement.setDate(index++, java.sql.Date.valueOf(fillBorrowDatePicker.getValue()));
        if (fillDueDatePicker.getValue() != null) preparedStatement.setDate(index++, java.sql.Date.valueOf(fillDueDatePicker.getValue()));
    }

    private void addRow(String isbn, String title, String author, String borrowed, String dueDate,Double priceOnce ) {
        Label isbnLabel = new Label(isbn);
        Label titleLabel = new Label(title);
        Label authorLabel = new Label(author);
        Label borrowedLabel = new Label(borrowed);
        Label dueDateLabel = new Label(dueDate);

        long minusDay = 0;
        if (LocalDate.parse(dueDate).isBefore(LocalDate.now())) {
            minusDay = ChronoUnit.DAYS.between(LocalDate.parse(borrowed),LocalDate.parse(dueDate));
        }
        double totalPrice = priceOnce*minusDay;
        Label overDueFeeLabel = new Label(Double.toString(totalPrice));
        Button returnNow = createReturnButton(parentController.getCustomer().getUserID(),isbn);

        notReturnBookTable.addRow(notReturnBookTable.getRowCount(),isbnLabel,titleLabel,authorLabel,borrowedLabel,dueDateLabel,overDueFeeLabel,returnNow);
        notReturnBookTable.getRowConstraints().add(new RowConstraints(70));
    }

    private Button createReturnButton(int userID, String isbn) {
        Button returnButton = new Button("Return?");
        returnButton.setOnAction(event -> returnedBook(userID,isbn));
        return returnButton;

    }

    private void returnedBook (int userID,String isbn) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String query = "update transactions set returnDate = current_date() where userID = ? and isbn = ?";
                try (Connection connectDB = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

                    preparedStatement.setInt(1, userID);
                    preparedStatement.setString(2, isbn);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        Platform.runLater(() -> sortReturn());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        sortReturn();
        new Thread(task).start();
    }
}
