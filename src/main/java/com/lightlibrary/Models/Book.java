package com.lightlibrary.Models;

public class Book {

    private int totalNumber;
    private int availableNumber;
    private String title;
    private String author;
    private String publisher;
    private String publishedDate;
    private String isbn;
    private String thumbnail;
    private String description;
    private double price;

    public Book() {

    }

    public Book(Book book) {
        this.totalNumber = book.getTotalNumber();
        this.availableNumber = book.getAvailableNumber();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.publishedDate = book.getPublishedDate();
        this.isbn = book.getIsbn();
        this.thumbnail = book.getThumbnail();
        this.description = book.getDescription();
        this.price = book.getPrice();
    }

    public Book(int totalNumber, String title, String author) {
        this.totalNumber = totalNumber;
        this.title = title;
        this.author = author;
    }

    public Book(int totalNumber, String title,
                String author, String publisher, String publishedDate) {
        this.totalNumber = totalNumber;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
    }

    public Book(int totalNumber, String title,
                String author, String publisher,
                String publishedDate, String isbn) {
        this.totalNumber = totalNumber;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public int getAvailableNumber() {
        return availableNumber;
    }

    public void setAvailableNumber(int availableNumber) {
        this.availableNumber = availableNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "Book{" +
                "totalNumber=" + totalNumber +
                ", availableNumber=" + availableNumber +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", isbn='" + isbn + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
