package com.lightlibrary.Models;

public class Customer extends User {

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

    /**
     *
     */
    public void borrowBook(String isbn) {

    }

    /**
     *
     */
    public void returnBook() {}
}
