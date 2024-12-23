package com.lightlibrary.Models;

import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.BooksRequestInitializer;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.Volumes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksAPIClient {

    public static List<Volume> searchBooks(String query) throws IOException {
        Books books = new Books.Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
                new com.google.api.client.json.gson.GsonFactory(), null)
                .setApplicationName(LibraryEnvironment.APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(LibraryEnvironment.API_KEY))
                .build();

        Books.Volumes.List volumesList = books.volumes().list(query);
        volumesList.setMaxResults(30L);
        Volumes volumes = volumesList.execute();

        List<Volume> validBooks = new ArrayList<>();
        for (Volume volume : volumes.getItems()) {
            Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();

            String isbn = getISBN(volumeInfo);
            if (isbn != null) {
                validBooks.add(volume);
            }
        }

        return validBooks;
    }

    public static String getISBN(Volume.VolumeInfo volumeInfo) {
        if (volumeInfo.getIndustryIdentifiers() != null) {
            for (Volume.VolumeInfo.IndustryIdentifiers identifier : volumeInfo.getIndustryIdentifiers()) {
                if ("ISBN_13".equals(identifier.getType()) || "ISBN_10".equals(identifier.getType())) {
                    return identifier.getIdentifier();
                }
            }
        }
        return null;
    }
}