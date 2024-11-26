package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminHomeController implements Initializable, SyncAction {

    @FXML
    private AnchorPane homeRoot;

    @FXML
    private GridPane listBookGridPane;

    @FXML
    private GridPane listUserGridPane;

    @FXML
    private ImageView requiredBookImage;

    @FXML
    private ImageView totalBookImage;

    @FXML
    private ImageView totalUserImage;

    @FXML
    private ImageView transactionsImage;

    @FXML
    private Label totalBookLabel;

    @FXML
    private Label totalUserLabel;

    @FXML
    private Label transactionsLabel;

    @FXML
    private Pane paneHaveGraph;

    @FXML
    private Label requiredBookLabel;

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
        graphController();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(this::loadViewBook);
        executorService.execute(this::loadViewUser);
        executorService.submit(() -> loadTotalOverviewLabel(totalBookLabel, "books"));
        executorService.submit(() -> loadTotalOverviewLabel(totalUserLabel, "users"));
        executorService.submit(() -> loadTotalOverviewLabel(transactionsLabel, "transactions"));
        executorService.submit(() -> loadTotalOverviewLabel(requiredBookLabel, "requiredBooks"));
        executorService.shutdown();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        autoUpdate();
    }

    @Override
    public void setTheme(boolean darkMode) {
        homeRoot.getStylesheets().clear();
        if (darkMode) {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
            totalBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-book-issue.png")).toExternalForm()));
            totalUserImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-user.png")).toExternalForm()));
            transactionsImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-transaction.png")).toExternalForm()));
            requiredBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-borrowed-book.png")).toExternalForm()));
        } else {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
            totalBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-book-issue.png")).toExternalForm()));
            totalUserImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-user.png")).toExternalForm()));
            transactionsImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-transaction.png")).toExternalForm()));
            requiredBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-borrowed-book.png")).toExternalForm()));
        }
    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {
    }

    @FXML
    public void viewAndEditBook(ActionEvent event) {
        parentController.goToViewBookPage();
        ;
    }

    @FXML
    public void viewAndEditUser(ActionEvent event) {
        parentController.goToUserManagementPage();
    }

    public void loadTotalOverviewLabel(Label label, String table) {
        Connection connection = DatabaseConnection.getConnection();
        String queryTotalBooks = "select count(*) as total from " + table;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryTotalBooks);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String total = resultSet.getString("total");
                Platform.runLater(() -> label.setText(total));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadViewBook() {
        listBookGridPane.getChildren().clear();
        listBookGridPane.getRowConstraints().clear();

        Connection connection = DatabaseConnection.getConnection();
        String queryConnect = "SELECT * FROM books";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryConnect);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                String ISBN = resultSet.getString("ISBN");
                String title = resultSet.getString("TITLE");
                String author = resultSet.getString("AUTHOR");
                int available = resultSet.getInt("availableNumber");
                Platform.runLater(() ->{
                    listBookGridPane.addRow(listBookGridPane.getRowCount(),
                            new Label(ISBN),
                            new Label(title),
                            new Label(author),
                            new Label(Integer.toString(available)));
                    RowConstraints curConstraints = new RowConstraints();
                    curConstraints.setMinHeight(70);
                    listBookGridPane.getRowConstraints().add(curConstraints);
                });
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadViewUser() {
        listUserGridPane.getChildren().clear();
        listUserGridPane.getRowConstraints().clear();

        Connection connection = DatabaseConnection.getConnection();
        String queryConnect = "select " +
                "u.*," +
                "case " +
                "when " +
                "count(t.userID) > 0 then count(t.userID) " +
                "else 0 " +
                "end as totalBorrowed " +
                "from users u left join transactions t  on u.userID = t.userID " +
                "group by u.userID " +
                "order by u.userID;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryConnect);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String fullName = resultSet.getString("fullName");
                String userName = resultSet.getString("userName");
                int totalBorrowed = resultSet.getInt("totalBorrowed");

                Platform.runLater(() -> {
                    listUserGridPane.addRow(listUserGridPane.getRowCount(),
                            new Label(Integer.toString(userID)),
                            new Label(userName),
                            new Label(fullName),
                            new Label(Integer.toString(totalBorrowed)));
                    RowConstraints curConstraints = new RowConstraints();
                    curConstraints.setMinHeight(70);
                    listUserGridPane.getRowConstraints().add(curConstraints);
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void graphController() {
        CategoryAxis xAxis = new CategoryAxis(); // Trục phân loại (category)
        NumberAxis yAxis = new NumberAxis(); // Trục số (number)
        xAxis.setLabel("Days");
        yAxis.setLabel("Numbers");
        BarChart<String, Number> graph = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> borrowTotals = new XYChart.Series<>();
        borrowTotals.setName("Borrowed");

        XYChart.Series<String, Number> returnTotals = new XYChart.Series<>();
        returnTotals.setName("Returned");

        Connection connection = DatabaseConnection.getConnection();

        // Query for borrowed books
        String queryConnect1 = "select t.borrowDate, count(t.borrowDate) as borrowPerDay from transactions t " +
                "where t.borrowDate between current_date() - 6 and current_date() group by t.borrowDate order by t.borrowDate asc";
        try {
            Statement preparedStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = preparedStatement.executeQuery(queryConnect1);
            int index = 6;
            while (index >= 0) {
                LocalDate localDate = LocalDate.now().minusDays(index);
                boolean found = false;
                resultSet.beforeFirst(); // Reset ResultSet to start from the beginning each time
                while (resultSet.next()) {
                    LocalDate borrowDate = resultSet.getDate("borrowDate").toLocalDate();
                    if (borrowDate.equals(localDate)) {
                        int total = resultSet.getInt("borrowPerDay");
                        XYChart.Data<String, Number> temporaryBar = new XYChart.Data<>(localDate.toString(), total);
                        // Update borrowTotals on UI thread
                        Platform.runLater(() -> borrowTotals.getData().add(temporaryBar));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Platform.runLater(() -> borrowTotals.getData().add(new XYChart.Data<>(localDate.toString(), 0)));
                }
                index--;
            }
        } catch (SQLException e) {
            System.out.println("cannot create connection");
            e.printStackTrace();
        }

        // Query for returned books
        String queryConnect2 = "select t.returnDate, count(t.returnDate) as returnPerDay from transactions t " +
                "where t.returnDate between current_date() - 6 and current_date() group by t.returnDate order by t.returnDate asc";
        try {
            Statement preparedStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = preparedStatement.executeQuery(queryConnect2);
            int index = 6;
            while (index >= 0) {
                LocalDate localDate = LocalDate.now().minusDays(index);
                boolean found = false;
                resultSet.beforeFirst(); // Reset ResultSet to start from the beginning each time
                while (resultSet.next()) {
                    LocalDate returnDate = resultSet.getDate("returnDate").toLocalDate();
                    if (returnDate.equals(localDate)) {
                        int total = resultSet.getInt("returnPerDay");
                        // Update returnTotals on UI thread
                        Platform.runLater(() -> returnTotals.getData().add(new XYChart.Data<>(localDate.toString(), total)));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Platform.runLater(() -> returnTotals.getData().add(new XYChart.Data<>(localDate.toString(), 0)));
                }
                index--;
            }
        } catch (SQLException e) {
            System.out.println("cannot create connection");
            e.printStackTrace();
        }

        // Ensure the graph is updated on the JavaFX Application Thread
        Platform.runLater(() -> {
            if (paneHaveGraph != null) {
                graph.getData().addAll(Arrays.asList(borrowTotals, returnTotals));
                graph.setPrefWidth(paneHaveGraph.getPrefWidth());
                graph.setPrefHeight(paneHaveGraph.getPrefHeight());
                if (paneHaveGraph.getChildren() != null) {
                    paneHaveGraph.getChildren().add(graph);
                } else {
                    System.out.println("paneHaveGraph is null");
                }
            } else {
                System.out.println("Graph or paneHaveGraph is null");
            }
        });
    }

}
