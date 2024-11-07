package com.lightlibrary.Models;

public class Customer extends User {

    private long coins;

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

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    /**
     *
     */
    public void borrowBook(String isbn) {

    }

    /**
     *
     */
    public void returnBook() {}

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