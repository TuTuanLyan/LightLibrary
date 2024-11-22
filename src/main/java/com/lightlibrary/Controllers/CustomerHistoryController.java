package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class CustomerHistoryController implements Initializable, SyncAction {

    @FXML
    private AnchorPane historyRoot;

    @FXML
    private DatePicker borrowDatePicker;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextField fillAuthorField;

    @FXML
    private TextField fillISBNField;

    @FXML
    private TextField fillTitleField;

    @FXML
    private TextField fillTotalField;

    @FXML
    private GridPane historyTableGrid;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private Label spenrTotalCoinLabel;

    @FXML
    private GridPane titleGrid;

    @FXML
    private Button borrowedButton;

    @FXML
    private Button overdueButton;

    @FXML
    private Button returnedButton;


    public enum Status {
        Borrowed,
        Overdue,
        Returned
    }
    public Status status = Status.Borrowed;


    CustomerDashboardController parentController;

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
        this.status = Status.Borrowed;
        control();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void setTheme(boolean darkMode) {
        historyRoot.getStylesheets().clear();
        if (darkMode) {
            historyRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            historyRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

    private void clearGrid() {
        historyTableGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        historyTableGrid.getRowConstraints().removeIf(row -> historyTableGrid.getRowConstraints().indexOf(row) > 0);
    }

    private String buildQuery() {
        StringBuilder query = new StringBuilder("select t.isbn,b.title,b.author,t.borrowDate,t.dueDate,t.returnDate, case when t.returnDate is not null then datediff(t.returnDate,t.borrowDate) * b.price else datediff(current_date(),t.borrowDate) * b.price end as totalFee from transactions t join books b on t.isbn = b.isbn where t.userID = ?");
        if (status == Status.Overdue) {
            query = new StringBuilder("select t.isbn,b.title,b.author,t.borrowDate,t.dueDate,t.returnDate,datediff(current_date(),t.borrowDate) * b.price as totalFee  from transactions t join books b on t.isbn = b.isbn where t.dueDate < current_date() and t.returnDate is null and t.userID = ?;");
        }
        else if (status == Status.Returned) {
            query = new StringBuilder("select t.isbn,b.title,b.author,t.borrowDate,t.dueDate,t.returnDate,datediff(t.returnDate,t.borrowDate) * b.price as totalFee from transactions t join books b on t.isbn = b.isbn where t.returnDate is not null and t.userID = ? ");
        }
        if (!fillISBNField.getText().isEmpty()) query.append(" AND t.isbn LIKE ?");
        if (!fillTitleField.getText().isEmpty()) query.append(" AND b.title LIKE ?");
        if (!fillAuthorField.getText().isEmpty()) query.append(" AND b.author LIKE ?");
        if (borrowDatePicker.getValue() != null) query.append(" AND t.borrowDate LIKE ?");
        if (dueDatePicker.getValue() != null) query.append(" AND t.dueDate LIKE ?");
        if (returnDatePicker.getValue() != null) query.append(" AND t.returnDate LIKE ?");
        if (!fillTotalField.getText().isEmpty()) query.append(" HAVING totalFee like ?");
        return query.toString();
    }

    private void setQueryParameters(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, parentController.getCustomer().getUserID());
        int index = 2;
        if (!fillISBNField.getText().isEmpty()) preparedStatement.setString(index++, "%" + fillISBNField.getText() + "%");
        if (!fillTitleField.getText().isEmpty()) preparedStatement.setString(index++, "%" + fillTitleField.getText() + "%");
        if (!fillAuthorField.getText().isEmpty()) preparedStatement.setString(index++, "%" + fillAuthorField.getText() + "%");
        if (borrowDatePicker.getValue() != null) preparedStatement.setDate(index++, java.sql.Date.valueOf(borrowDatePicker.getValue()));
        if (dueDatePicker.getValue() != null) preparedStatement.setDate(index++, java.sql.Date.valueOf(dueDatePicker.getValue()));
        if (returnDatePicker.getValue() != null) preparedStatement.setDate(index++, java.sql.Date.valueOf(returnDatePicker.getValue()));
        if (!fillTotalField.getText().isEmpty()) preparedStatement.setString(index++, "%" + fillTotalField.getText() + "%");
    }

    private void addRow(String isbn, String title, String author, String borrowed, String dueDate,String returned,Double totalFee ) {
        Label isbnLabel = new Label(isbn);
        Label titleLabel = new Label(title);
        Label authorLabel = new Label(author);
        Label borrowedLabel = new Label(borrowed);
        Label dueDateLabel = new Label(dueDate);
        Label returnedLabel = new Label(returned);
        Label totalFeeLabel = new Label(String.valueOf(totalFee));


        historyTableGrid.addRow(historyTableGrid.getRowCount(),isbnLabel,titleLabel,authorLabel,borrowedLabel,dueDateLabel,returnedLabel,totalFeeLabel);
        historyTableGrid.getRowConstraints().add(new RowConstraints(70));
    }

    public void reLoad() {
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
                        String returnDate;
                        if (resultSet.getString("returnDate") != null) {
                            returnDate = resultSet.getDate("returnDate").toLocalDate().toString();
                        } else {
                            returnDate = "null";
                        }
                        Double totalFee = resultSet.getDouble("totalFee");
                        System.out.println(isbn + " " + title + " " + author + " " + borrowedDate + " " + dueDate + " " + returnDate + " " + totalFee);
                        Platform.runLater(() -> addRow(isbn,title,author,borrowedDate,dueDate,returnDate,totalFee));
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

    public void control() {
        reLoad();
        borrowedButton.setOnAction(event -> {
            status = Status.Borrowed;
            reLoad();
        });
        overdueButton.setOnAction(event -> {
            status = Status.Overdue;
            reLoad();
        });

        returnedButton.setOnAction(event -> {
            status = Status.Returned;
            reLoad();
        });
        fillISBNField.setOnKeyTyped(event -> {
            //if(fillISBNField.getText().isEmpty()) return;
            reLoad();

        });
        fillTitleField.setOnKeyTyped(event -> {
            //if(fillTitleField.getText().isEmpty()) return;
            reLoad();

        });
        fillAuthorField.setOnKeyTyped(event -> {
            //if(fillAuthorField.getText().isEmpty()) return;
            reLoad();
        });

        fillTotalField.setOnKeyTyped(event -> {
            //if(fillTotalField.getText().isEmpty()) return;
            reLoad();
        });
        borrowDatePicker.setOnAction(event -> reLoad());

        dueDatePicker.setOnAction(event -> reLoad());

        returnDatePicker.setOnAction(event -> reLoad());


    }

}
