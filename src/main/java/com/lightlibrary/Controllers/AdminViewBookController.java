package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdminViewBookController implements Initializable, SyncAction {

    @FXML
    private AnchorPane viewBookRoot;

    @FXML
    private GridPane manageBook;

    @FXML
    private AnchorPane editPane;

    @FXML
    private TextField sortPrice;

    @FXML
    private TextField sortTitle;

    @FXML
    private TextField sortTotalNumber;

    @FXML
    private TextField sortAvailable;

    @FXML
    private TextField sortISBN;

    @FXML
    private TextField sortAuthor;


    AdminDashboardController parentController;

    public AdminDashboardController getParentController() {
        return parentController;
    }

    @Override
    public void setParentController(AdminDashboardController parentController) {
        this.parentController = parentController;
        setTheme(parentController.getAdmin().isDarkMode());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reLoad();
        sortISBN.setOnKeyTyped(keyEvent ->reLoad());
        sortTitle.setOnKeyTyped(keyEvent ->reLoad());
        sortTotalNumber.setOnKeyTyped(keyEvent ->reLoad());
        sortAuthor.setOnKeyTyped(keyEvent ->reLoad());
        sortPrice.setOnKeyTyped(keyEvent ->reLoad());
        sortAvailable.setOnKeyTyped(keyEvent ->reLoad());
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

    public void reLoad() {
        while (manageBook.getChildren().size() > 1) {
            manageBook.getChildren().removeLast();
        }
        manageBook.getRowConstraints().clear();
        String findISBN = sortISBN.getText();
        String findTitle = sortTitle.getText();
        String findAuthor = sortAuthor.getText();
        String findTotal = sortTotalNumber.getText();
        String findAvailable = sortAvailable.getText();
        String findPrice = sortPrice.getText();
        Connection connectDB = DatabaseConnection.getConnection();

        if (connectDB == null) {
            System.out.println("Something were wrong connectDB is null!");
        }

        //done
        if(!sortPrice.getText().isEmpty() && !sortAvailable.getText().isEmpty() && !sortTotalNumber.getText().isEmpty()) {
            int totalNumber = -1;
            int availableNumber = -1;
            double price = -1;
            try {
                totalNumber = Integer.parseInt(findTotal);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing total number!");
            }
            try {
                availableNumber = Integer.parseInt(findAvailable);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing available number!");
            }
            try {
                price = Double.parseDouble(findPrice);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing price!");
            }

            String connectQuery = "SELECT * FROM books WHERE ISBN LIKE ? and title LIKE ? and author LIKE ? and totalNumber LIKE ? and availableNumber LIKE ? and price LIKE ?";
            try {
                PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                preparedStatement.setString(1, "%" + findISBN + "%");
                preparedStatement.setString(2, "%" + findTitle + "%");
                preparedStatement.setString(3, "%" + findAuthor + "%");
                preparedStatement.setInt(4, totalNumber);
                preparedStatement.setInt(5, availableNumber);
                preparedStatement.setDouble(6, price);

                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String ISBN = resultSet.getString("isbn");
                    String Title = resultSet.getString("title");
                    String Author = resultSet.getString("author");
                    int Total = resultSet.getInt("totalNumber");
                    int Available = resultSet.getInt("availableNumber");
                    double Price = resultSet.getDouble("price");

                    add(ISBN, Title, Author, Total, Available, Price);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        //done
        if(!sortPrice.getText().isEmpty() && !sortAvailable.getText().isEmpty() && sortTotalNumber.getText().isEmpty()) {
            int availableNumber = -1;
            double price = -1;
            try {
                availableNumber = Integer.parseInt(findAvailable);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing available number!");
            }
            try {
                price = Double.parseDouble(findPrice);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing price!");
            }

            String connectQuery = "SELECT * FROM books WHERE ISBN LIKE ? and title LIKE ? and author LIKE ? and availableNumber LIKE ? and price LIKE ?";
            try {
                PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                preparedStatement.setString(1, "%" + findISBN + "%");
                preparedStatement.setString(2, "%" + findTitle + "%");
                preparedStatement.setString(3, "%" + findAuthor + "%");
                preparedStatement.setInt(4, availableNumber);
                preparedStatement.setDouble(5, price);

                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String ISBN = resultSet.getString("isbn");
                    String Title = resultSet.getString("title");
                    String Author = resultSet.getString("author");
                    int Total = resultSet.getInt("totalNumber");
                    int Available = resultSet.getInt("availableNumber");
                    double Price = resultSet.getDouble("price");

                    add(ISBN, Title, Author, Total, Available, Price);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        //done
        if(!sortPrice.getText().isEmpty() && sortAvailable.getText().isEmpty() && !sortTotalNumber.getText().isEmpty()) {
            int totalNumber = -1;
            double price = -1;
            try {
                totalNumber = Integer.parseInt(findTotal);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing total number!");
            }
            try {
                price = Double.parseDouble(findPrice);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing price!");
            }

            String connectQuery = "SELECT * FROM books WHERE ISBN LIKE ? and title LIKE ? and author LIKE ? and totalNumber LIKE ? and price LIKE ?";
            try {
                PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                preparedStatement.setString(1, "%" + findISBN + "%");
                preparedStatement.setString(2, "%" + findTitle + "%");
                preparedStatement.setString(3, "%" + findAuthor + "%");
                preparedStatement.setInt(4, totalNumber);
                preparedStatement.setDouble(5, price);

                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String ISBN = resultSet.getString("isbn");
                    String Title = resultSet.getString("title");
                    String Author = resultSet.getString("author");
                    int Total = resultSet.getInt("totalNumber");
                    int Available = resultSet.getInt("availableNumber");
                    double Price = resultSet.getDouble("price");

                    add(ISBN, Title, Author, Total, Available, Price);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        //done
        if(!sortPrice.getText().isEmpty() && sortAvailable.getText().isEmpty() && sortTotalNumber.getText().isEmpty()) {
            double price = -1;
            try {
                price = Double.parseDouble(findPrice);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing price!");
            }

            String connectQuery = "SELECT * FROM books WHERE ISBN LIKE ? and title LIKE ? and author LIKE ? and price LIKE ?";
            try {
                PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                preparedStatement.setString(1, "%" + findISBN + "%");
                preparedStatement.setString(2, "%" + findTitle + "%");
                preparedStatement.setString(3, "%" + findAuthor + "%");
                preparedStatement.setDouble(4, price);

                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String ISBN = resultSet.getString("isbn");
                    String Title = resultSet.getString("title");
                    String Author = resultSet.getString("author");
                    int Total = resultSet.getInt("totalNumber");
                    int Available = resultSet.getInt("availableNumber");
                    double Price = resultSet.getDouble("price");

                    add(ISBN, Title, Author, Total, Available, Price);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        //done
        if(sortPrice.getText().isEmpty() && !sortAvailable.getText().isEmpty() && !sortTotalNumber.getText().isEmpty()) {
            int totalNumber = -1;
            int availableNumber = -1;
            try {
                totalNumber = Integer.parseInt(findTotal);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing total number!");
            }
            try {
                availableNumber = Integer.parseInt(findAvailable);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing available number!");
            }

            String connectQuery = "SELECT * FROM books WHERE ISBN LIKE ? and title LIKE ? and author LIKE ? and totalNumber LIKE ? and availableNumber LIKE ?";
            try {
                PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                preparedStatement.setString(1, "%" + findISBN + "%");
                preparedStatement.setString(2, "%" + findTitle + "%");
                preparedStatement.setString(3, "%" + findAuthor + "%");
                preparedStatement.setInt(4, totalNumber);
                preparedStatement.setInt(5, availableNumber);

                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String ISBN = resultSet.getString("isbn");
                    String Title = resultSet.getString("title");
                    String Author = resultSet.getString("author");
                    int Total = resultSet.getInt("totalNumber");
                    int Available = resultSet.getInt("availableNumber");
                    double Price = resultSet.getDouble("price");

                    add(ISBN, Title, Author, Total, Available, Price);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        //done
        if(sortPrice.getText().isEmpty() && !sortAvailable.getText().isEmpty() && sortTotalNumber.getText().isEmpty()) {
            int availableNumber = -1;

            try {
                availableNumber = Integer.parseInt(findAvailable);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing available number!");
            }

            String connectQuery = "SELECT * FROM books WHERE ISBN LIKE ? and title LIKE ? and author LIKE ?  and availableNumber LIKE ?";
            try {
                PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                preparedStatement.setString(1, "%" + findISBN + "%");
                preparedStatement.setString(2, "%" + findTitle + "%");
                preparedStatement.setString(3, "%" + findAuthor + "%");
                preparedStatement.setInt(4, availableNumber);

                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String ISBN = resultSet.getString("isbn");
                    String Title = resultSet.getString("title");
                    String Author = resultSet.getString("author");
                    int Total = resultSet.getInt("totalNumber");
                    int Available = resultSet.getInt("availableNumber");
                    double Price = resultSet.getDouble("price");

                    add(ISBN, Title, Author, Total, Available, Price);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        //done
        if(sortPrice.getText().isEmpty() && sortAvailable.getText().isEmpty() && !sortTotalNumber.getText().isEmpty()) {
            int totalNumber = -1;
            try {
                totalNumber = Integer.parseInt(findTotal);
            } catch (NumberFormatException e) {
                System.out.println("Something went wrong while parsing total number!");
            }

            String connectQuery = "SELECT * FROM books WHERE ISBN LIKE ? and title LIKE ? and author LIKE ? and totalNumber LIKE ?";
            try {
                PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                preparedStatement.setString(1, "%" + findISBN + "%");
                preparedStatement.setString(2, "%" + findTitle + "%");
                preparedStatement.setString(3, "%" + findAuthor + "%");
                preparedStatement.setInt(4, totalNumber);


                //System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String ISBN = resultSet.getString("isbn");
                    String Title = resultSet.getString("title");
                    String Author = resultSet.getString("author");
                    int Total = resultSet.getInt("totalNumber");
                    int Available = resultSet.getInt("availableNumber");
                    double Price = resultSet.getDouble("price");

                    add(ISBN, Title, Author, Total, Available, Price);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }


        String connectQuery = "SELECT * FROM books WHERE ISBN LIKE ? and title LIKE ? and author LIKE ?";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
            preparedStatement.setString(1, "%" + findISBN + "%");
            preparedStatement.setString(2, "%" + findTitle + "%");
            preparedStatement.setString(3, "%" + findAuthor + "%");


            //System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            /*if(resultSet != null) {
                System.out.println(resultSet);
            }*/
            while (resultSet.next()) {
                String ISBN = resultSet.getString("isbn");
                String Title = resultSet.getString("title");
                String Author = resultSet.getString("author");
                int Total = resultSet.getInt("totalNumber");
                int Available = resultSet.getInt("availableNumber");
                double Price = resultSet.getDouble("price");

                add(ISBN, Title, Author, Total, Available, Price);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }




    }

    public void add(String ISBN,String Title,String Author,int totalNumber,int availableNumber,double price) {
        int index = manageBook.getRowCount();
        // đống label dùng để nhét vào từng row
        Label isbnLabel = new Label(ISBN);
        Label titleLabel = new Label(Title);
        Label authorLabel = new Label(Author);
        Label totalLabel = new Label(Integer.toString(totalNumber));
        Label availableLabel = new Label(Integer.toString(availableNumber));
        Label priceLabel = new Label(Double.toString(price));
        Button editButton = new Button("Edit");
        editButton.setPrefSize(70, 30);
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefSize(70, 30);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(70);
        manageBook.getRowConstraints().add(rowConstraints);
        manageBook.addRow(manageBook.getRowCount()-1, isbnLabel, titleLabel, authorLabel, totalLabel, availableLabel, priceLabel, editButton, deleteButton);

        System.out.println(ISBN + ' ' + Title + ' ' + Author + ' ' + totalNumber + ' ' + availableNumber + ' ' + price);

        deleteButton.setOnAction(e -> {
            remove(ISBN);
        });
        editButton.setOnAction(e -> {
            AtomicBoolean changed = new AtomicBoolean(false);
            editPane.setVisible(true);
            TextField editTotal = (TextField) editPane.getChildren().get(0);
            editTotal.setText(Integer.toString(totalNumber));
            editTotal.setOnKeyTyped(keyEvent -> {
                changed.set(true);
            });
            TextField editAvailable = (TextField) editPane.getChildren().get(1);
            editAvailable.setText(Integer.toString(availableNumber));
            editAvailable.setOnKeyTyped(keyEvent -> {
                changed.set(true);
            });
            TextField editPrice = (TextField) editPane.getChildren().get(2);
            editPrice.setText(Double.toString(price));
            editPrice.setOnKeyTyped(keyEvent -> {
                changed.set(true);
            });
            Button ok = (Button) editPane.getChildren().get(6);


            ok.setOnAction(event -> {
                if (!changed.get()) {
                    System.out.println("Nothing change");
                    editPane.setVisible(false);
                }
                else {
                    int totalNew;
                    int availableNew;
                    double priceNew;
                    try {
                        totalNew = Integer.parseInt(editTotal.getText());
                        if(totalNew < 1) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException n) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter a valid total number!");
                        alert.showAndWait();
                        editTotal.setText(Integer.toString(totalNumber));
                        return;
                    }
                    try {
                        availableNew = Integer.parseInt(editAvailable.getText());
                        if(availableNew < 1) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException n) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter a valid available number!");
                        alert.showAndWait();
                        editAvailable.setText(Integer.toString(availableNumber));
                        return;
                    }
                    try {
                        priceNew = Double.parseDouble(editPrice.getText());
                        if(priceNew < 0) {
                            throw new NumberFormatException();
                        }

                    } catch (NumberFormatException n) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter a valid price!");
                        alert.showAndWait();
                        editPrice.setText(Double.toString(price));
                        return;
                    }
                    edit(ISBN,Integer.parseInt(editTotal.getText()),Integer.parseInt(editAvailable.getText()),Double.parseDouble(editPrice.getText()));
                    editPane.setVisible(false);
                }

            });
        });
    }

    public void remove(String ISBN) {
        Connection connectDB = DatabaseConnection.getConnection();
        String removeQuery = "DELETE FROM books WHERE isbn = ?;";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(removeQuery);
            preparedStatement.setString(1, ISBN);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Book deleted successfully");
            } else System.out.println("Something went wrong");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        reLoad();
    }

    public void edit(String ISBN,int totalNumber,int availableNumber,double price) {
        Connection connectDB = DatabaseConnection.getConnection();
        String editQuery = "UPDATE books SET totalNumber = ?,availableNumber = ?,price =?  where isbn = ?;";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(editQuery);
            preparedStatement.setInt(1, totalNumber);
            preparedStatement.setInt(2, availableNumber);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, ISBN);

            int result = preparedStatement.executeUpdate();
            System.out.println(preparedStatement);
            if (result > 0) {
                System.out.println("Book edited successfully");
            } else System.out.println("Something went wrong");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        reLoad();

    }
}
