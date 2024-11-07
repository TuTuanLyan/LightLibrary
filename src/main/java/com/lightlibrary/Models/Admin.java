package com.lightlibrary.Models;

public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(int userID, String fullName,
                 String username, String password) {
        super(userID, fullName, username, password);
    }

    public Admin(int userID, String fullName, String username,
                 String password, String phoneNumber, String email) {
        super(userID, fullName, username, password, phoneNumber, email);
    }
}