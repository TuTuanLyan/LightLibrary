package com.lightlibrary.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Customer extends User {

    private double coins;
    private String avatarImageUrl;
    private List<Book> listFavouriteBooks;
    private List<Transaction> listTransactions;

    public Customer() {
        super();
    }

    public Customer(int userID, String fullName,
                    String username, String password) {
        super(userID, fullName, username, password);
    }

    public Customer(int userID, String fullName, String username,
                    String password, String phoneNumber, String email) {
        super(userID, fullName, username, password, phoneNumber, email);
    }

    public double getCoins() {
        // Kết nối cơ sở dữ liệu
        Connection connectDB = DatabaseConnection.getConnection();
        if (connectDB == null) {
            System.out.println("Failed to connect to the database.");
            return 0.0; // Giá trị mặc định nếu không kết nối được
        }

        String query = "SELECT coin FROM users WHERE userID = ?";
        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setInt(1, this.userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                this.coins = resultSet.getDouble("coin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.coins;
    }


    public void setCoins(double coins) {
        this.coins = coins;
    }

    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
    }

    public List<Book> getListFavouriteBooks() {
        return listFavouriteBooks;
    }

    public void setListFavouriteBooks(List<Book> listFavouriteBooks) {
        this.listFavouriteBooks = listFavouriteBooks;
    }

    public List<Transaction> getListTransactions() {
        return listTransactions;
    }

    public void setListTransactions(List<Transaction> listTransactions) {
        this.listTransactions = listTransactions;
    }

    public boolean checkPasswordValidation(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        int consecutiveIncreaseCount = 0;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isLetter(c)) {
                hasLetter = true;
                consecutiveIncreaseCount = 0;
            } else if (Character.isDigit(c)) {
                if (i > 0 && Character.isDigit(password.charAt(i - 1))) {
                    if (password.charAt(i) - password.charAt(i - 1) == 1) {
                        consecutiveIncreaseCount++;
                        if (consecutiveIncreaseCount >= 2) {
                            return false;
                        }
                    } else {
                        consecutiveIncreaseCount = 0;
                    }
                }
            }
        }

        return hasLetter;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "userID=" + userID +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

}