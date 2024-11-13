package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import com.lightlibrary.Models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatClient {
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField inputField;
    @FXML
    private Button buttonStart;
    private Customer customer;

    public void sendMessage() {
        String message = inputField.getText();
        if(message.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid message");
            alert.showAndWait();
            return;
        }
        try (Socket socket = new Socket("localhost", 12345)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println(message);
            System.out.println("Message sent:" + message);
        }
        catch (IOException e){
            System.out.println("Error sending message" + e.getMessage());

        }
    }
    public void startServer() throws IOException {

        this.customer = CustomerDashboardController.getCustomer();
        messageArea.setVisible(true);
        inputField.setVisible(true);
        buttonStart.setVisible(false);
        System.out.println(customer.getFullName());
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("Server is running...");
                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Kết nối mới từ " + clientSocket.getInetAddress());
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String message = in.readLine();
                    //System.out.println("Nhận được từ client: " + message);
                    messageArea.appendText( "<" + customer.getFullName() + ">[" + customer.getRole() +"]"+ message + "\n");
                    inputField.clear();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Server have been stopped.");
            }
        });
        serverThread.setDaemon(true);  // Đặt thread là daemon
        serverThread.start();
    }
}
