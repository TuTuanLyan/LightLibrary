package com.lightlibrary.Models;

import java.time.LocalDate;

public class Transaction {

    private int transactionID;
    private int userID;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double borrowFee;
    private double overdueFee;
    private double totalPrice;

    public Transaction() {

    }

    public Transaction(Transaction transaction) {
        this.transactionID = transaction.getTransactionID();
        this.userID = transaction.getUserID();
        this.isbn = transaction.getIsbn();
        this.borrowDate = transaction.getBorrowDate();
        this.dueDate = transaction.getDueDate();
        this.returnDate = transaction.getReturnDate();
        this.borrowFee = transaction.getBorrowFee();
        this.overdueFee = transaction.getOverdueFee();
        this.totalPrice = transaction.getTotalPrice();
    }

    public Transaction(int transactionID, int userID,
                       String isbn, LocalDate borrowDate,
                       LocalDate dueDate) {
        this.transactionID = transactionID;
        this.userID = userID;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getBorrowFee() {
        return borrowFee;
    }

    public void setBorrowFee(double borrowFee) {
        this.borrowFee = borrowFee;
    }

    public double getOverdueFee() {
        return overdueFee;
    }

    public void setOverdueFee(double overdueFee) {
        this.overdueFee = overdueFee;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}