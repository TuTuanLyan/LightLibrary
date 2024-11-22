package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminViewBookController implements Initializable, SyncAction {

    @FXML
    private AnchorPane viewBookRoot;

    @FXML
    private GridPane manageBook;

    @FXML
    private AnchorPane editPane;

    @FXML
    private TextField sortPrice, sortTitle, sortTotalNumber, sortAvailable, sortISBN, sortAuthor;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField editAvailableTextField, editPriceTextField, editTotalTextField;

    private String currentEditingISBN;

    AdminDashboardController parentController;

    public AdminDashboardController getParentController() {
        return parentController;
    }

    @Override
    public void setParentController(AdminDashboardController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void autoUpdate() {
        reLoad();
    }

    @Override
    public void setTheme(boolean darkMode) {
        viewBookRoot.getStylesheets().clear();
        if (darkMode) {
            viewBookRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            viewBookRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSortEvents();
        autoUpdate();
    }

    private void setupSortEvents() {
        sortISBN.setOnKeyTyped(keyEvent -> reLoad());
        sortTitle.setOnKeyTyped(keyEvent -> reLoad());
        sortTotalNumber.setOnKeyTyped(keyEvent -> reLoad());
        sortAuthor.setOnKeyTyped(keyEvent -> reLoad());
        sortPrice.setOnKeyTyped(keyEvent -> reLoad());
        sortAvailable.setOnKeyTyped(keyEvent -> reLoad());
    }

    public void reLoad() {
        manageBook.getChildren().clear();
        manageBook.getRowConstraints().clear();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM books WHERE 1=1");
        Map<Integer, Object> parameters = new HashMap<>();
        int paramIndex = 1;

        try {
            paramIndex = addQueryCondition(sortISBN, "ISBN", queryBuilder, parameters, paramIndex);
            paramIndex = addQueryCondition(sortTitle, "title", queryBuilder, parameters, paramIndex);
            paramIndex = addQueryCondition(sortAuthor, "author", queryBuilder, parameters, paramIndex);
            paramIndex = addQueryCondition(sortTotalNumber, "totalNumber", queryBuilder, parameters, paramIndex, true);
            paramIndex = addQueryCondition(sortAvailable, "availableNumber", queryBuilder, parameters, paramIndex, true);
            paramIndex = addQueryCondition(sortPrice, "price", queryBuilder, parameters, paramIndex, true);
        } catch (NumberFormatException e) {
            showAlert("Invalid input: " + e.getMessage());
            return;
        }

        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(queryBuilder.toString())) {
            for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
                preparedStatement.setObject(entry.getKey(), entry.getValue());
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String ISBN = resultSet.getString("isbn");
                String Title = resultSet.getString("title");
                String Author = resultSet.getString("author");
                int Total = resultSet.getInt("totalNumber");
                int Available = resultSet.getInt("availableNumber");
                double Price = resultSet.getDouble("price");
                addRow(ISBN, Title, Author, Total, Available, Price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int addQueryCondition(TextField field, String column, StringBuilder queryBuilder, Map<Integer, Object> parameters, int paramIndex, boolean isNumeric) {
        String value = field.getText();
        if (!value.isEmpty()) {
            if (isNumeric) {
                try {
                    queryBuilder.append(" AND ").append(column).append(" = ?");
                    parameters.put(paramIndex++, Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(column + " must be a valid number.");
                }
            } else {
                queryBuilder.append(" AND ").append(column).append(" LIKE ?");
                parameters.put(paramIndex++, "%" + value + "%");
            }
        }
        return paramIndex;
    }

    private int addQueryCondition(TextField field, String column, StringBuilder queryBuilder, Map<Integer, Object> parameters, int paramIndex) {
        return addQueryCondition(field, column, queryBuilder, parameters, paramIndex, false);
    }

    private void addRow(String ISBN, String Title, String Author, int totalNumber, int availableNumber, double price) {
        int rowIndex = manageBook.getRowCount();
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(70);
        manageBook.getRowConstraints().add(rowConstraints);

        manageBook.addRow(rowIndex,
                new Label(ISBN),
                new Label(Title),
                new Label(Author),
                new Label(String.valueOf(totalNumber)),
                new Label(String.valueOf(availableNumber)),
                new Label(String.valueOf(price)),
                createEditButton(ISBN, totalNumber, availableNumber, price),
                createDeleteButton(ISBN)
        );
    }

    private Button createEditButton(String ISBN, int totalNumber, int availableNumber, double price) {
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> showEditPane(ISBN, totalNumber, availableNumber, price));
        return editButton;
    }

    private Button createDeleteButton(String ISBN) {
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> removeBook(ISBN));
        return deleteButton;
    }

    private void showEditPane(String ISBN, int totalNumber, int availableNumber, double price) {
        currentEditingISBN = ISBN;
        editTotalTextField.setText(String.valueOf(totalNumber));
        editAvailableTextField.setText(String.valueOf(availableNumber));
        editPriceTextField.setText(String.valueOf(price));
        editPane.setVisible(true);

        confirmButton.setOnAction(event -> confirmEdit());
    }

    @FXML
    private void confirmEdit() {
        try {
            int totalNumber = Integer.parseInt(editTotalTextField.getText());
            int availableNumber = Integer.parseInt(editAvailableTextField.getText());
            double price = Double.parseDouble(editPriceTextField.getText());

            String query = "UPDATE books SET totalNumber = ?, availableNumber = ?, price = ? WHERE ISBN = ?";
            try (Connection connectDB = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
                preparedStatement.setInt(1, totalNumber);
                preparedStatement.setInt(2, availableNumber);
                preparedStatement.setDouble(3, price);
                preparedStatement.setString(4, currentEditingISBN);
                if (preparedStatement.executeUpdate() > 0) {
                    reLoad();
                    editPane.setVisible(false);
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers for total, available, and price.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeBook(String ISBN) {
        String query = "DELETE FROM books WHERE ISBN = ?";
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, ISBN);
            if (preparedStatement.executeUpdate() > 0) {
                reLoad();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}