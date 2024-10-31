package com.lightlibrary.Models;

public class User {
    protected int userID;
    protected String fullName;
    protected String username;
    protected String password;
    protected String phoneNumber;
    protected String email;
    protected Role role;

    public enum Role {
        CUSTOMER,
        ADMIN
    }

    public User() {

    }

    public User(int userID, String fullName,
                String username, String password) {
        this.userID = userID;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
    }

    public User(int userID, String fullName, String username,
                String password, String phoneNumber, String email) {
        this.userID = userID;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int ID) {
        this.userID = ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean checkEmailValidation(String email) {
        int atIndex = email.indexOf('@');
        return atIndex > 0 && email.indexOf('.', atIndex) > atIndex + 1;
    }

    public boolean checkPhoneNumberValidation(String phoneNumber) {
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
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
