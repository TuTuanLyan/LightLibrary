package com.lightlibrary.Models;

import com.google.api.services.books.Books;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;

import java.io.IOException;
import java.util.List;

public class GoogleBooksAPIClient {

    private static final String APPLICATION_NAME = "Library App";
    private static final String API_KEY = "AIzaSyCn5VoTHp_ueNeviSYb32sc44xAMaYb7e0"; // Thay bằng API Key của bạn

    // Hàm gọi API và tìm kiếm sách
    public List<Volume> searchBooks(String query) throws IOException {
        // Tạo đối tượng Books với API key
        Books books = new Books.Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
                new com.google.api.client.json.jackson2.JacksonFactory(), null)
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
                .build();

        // Gửi yêu cầu tìm kiếm từ khóa
        Books.Volumes.List volumesList = books.volumes().list(query);
        Volumes volumes = volumesList.execute();

        // Trả về danh sách Volume
        return volumes.getItems();
    }

    // Hàm hiển thị thông tin chi tiết về sách
    public void printBookInfo(List<Volume> volumes) {
        if (volumes != null && !volumes.isEmpty()) {
            for (Volume volume : volumes) {
                Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
                System.out.println("Title: " + volumeInfo.getTitle());
                System.out.println("Authors: " + volumeInfo.getAuthors());
                System.out.println("Publisher: " + volumeInfo.getPublisher());
                System.out.println("Published Date: " + volumeInfo.getPublishedDate());
                System.out.println("ISBN: " + getISBN(volumeInfo));
                System.out.println("Thumbnail: " + volumeInfo.getImageLinks().getThumbnail());
                System.out.println("------------------------------");
            }
        } else {
            System.out.println("No books found.");
        }
    }

    // Hàm lấy ISBN của cuốn sách
    private String getISBN(Volume.VolumeInfo volumeInfo) {
        if (volumeInfo.getIndustryIdentifiers() != null) {
            for (Volume.VolumeInfo.IndustryIdentifiers identifier : volumeInfo.getIndustryIdentifiers()) {
                if ("ISBN_13".equals(identifier.getType())) {
                    return identifier.getIdentifier();
                }
            }
        }
        return "N/A";
    }

    public static void main(String[] args) {
        GoogleBooksAPIClient googleBooksAPIClient = new GoogleBooksAPIClient();
        try {
            // Tìm kiếm sách với từ khóa
            List<Volume> books = googleBooksAPIClient.searchBooks("Java Programming");

            // Hiển thị thông tin sách
            googleBooksAPIClient.printBookInfo(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}