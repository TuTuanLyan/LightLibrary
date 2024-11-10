package com.lightlibrary.Controllers;

import javafx.fxml.FXML;


import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class ChatClient implements Initializable {
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField inputField;
    private ServerSocket serverSocket;
    private boolean ServerIsRunning = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void sendMessage() {
        if(!ServerIsRunning) {
            inputField.clear();
        }
        String message = inputField.getText();
        try (Socket socket = new Socket("localhost", 12345)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println(message);
            System.out.println("Message sent:" + message);
        }
        catch (IOException e){
            System.out.println("Error sending message" + e.getMessage());
        }
    }
    public void shutDownServer() throws IOException {
        serverSocket.close();
        ServerIsRunning = false;
    }
    public void startServer() throws IOException {
        if(ServerIsRunning) return;
        ServerIsRunning = true;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            serverSocket = new ServerSocket(12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        executorService.submit(() -> {
            try {
                System.out.println("Server đang lắng nghe trên cổng " + 12345);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Kết nối mới từ " + clientSocket.getInetAddress());
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String message = in.readLine();
                    System.out.println("Nhận được từ client: " + message);
                    messageArea.appendText(message + "\n");
                    inputField.clear();
                    clientSocket.close();
                }

            } catch (IOException e) {
                System.out.println("Lỗi xảy ra ở server: " + e.getMessage());
            }
            finally {
                executorService.shutdown();
            }
        });
    }
}
