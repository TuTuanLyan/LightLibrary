package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import java.util.Objects;

import com.lightlibrary.Models.Customer;
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

public class CustomerChatController implements SyncAction {
    @FXML
    private AnchorPane chatRoot;
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField inputField;
    @FXML
    private Button buttonStart, buttonConnect, buttonOpenIP, buttonConnectIP;
    @FXML
    private TextField ipField;

    private Customer customer;
    private String serverIPAddress;
    private ServerSocket serverSocket;

    CustomerDashboardController parentController;

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    @Override
    public  void setParentController(AdminDashboardController parentController) {}

    @Override
    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
        customer = parentController.getCustomer();
    }

    public void initialize() {
        ipField.setVisible(false);
        buttonConnect.setVisible(false);
    }

    @Override
    public void setTheme(boolean darkMode) {
        chatRoot.getStylesheets().clear();
        if (darkMode) {
            chatRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            chatRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

    // Khi nhấn "Open a new IP address"
    @FXML
    private void handleOpenIPAddress() {
        try {
            serverSocket = new ServerSocket(0); // Tạo một serverSocket với port ngẫu nhiên
            serverIPAddress = serverSocket.getInetAddress().getHostAddress();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Server IP Address");
            alert.setHeaderText("Your chat server is ready!");
            alert.setContentText("Share this IP: " + serverIPAddress);
            alert.showAndWait();

            // Bắt đầu server chat
            startServer();
        } catch (IOException e) {
            showError("Failed to open IP address", e.getMessage());
        }
    }

    // Khi nhấn "Connect to an existing IP address"
    @FXML
    private void handleConnectIPAddress() {
        ipField.setVisible(true);
        buttonConnect.setVisible(true);
    }

    // Kết nối tới server từ địa chỉ IP người dùng nhập
    @FXML
    private void handleConnectToServer() {
        String ip = ipField.getText().trim();
        if (ip.isEmpty()) {
            showError("Invalid IP", "Please enter a valid IP address.");
            return;
        }

        try {
            connectToServer(ip);
        } catch (IOException e) {
            showError("Failed to connect", "Unable to connect to IP: " + ip);
        }
    }

    // Hàm gửi tin nhắn
    @FXML
    private void sendMessage() {
        String messageContent = inputField.getText().trim();
        if (messageContent.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid message");
            alert.showAndWait();
            return;
        }

        String formattedMessage = "<" + customer.getFullName() + ">[" + customer.getRole() + "] " + messageContent;

        try (Socket socket = new Socket("localhost", 12345)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(formattedMessage);

            // Hiển thị tin nhắn vừa gửi trên giao diện người dùng
            messageArea.appendText(formattedMessage + "\n");
            inputField.clear(); // Xóa trường nhập sau khi gửi
        } catch (IOException e) {
            showError("Failed to send message", e.getMessage());
        }
    }

    // Hiển thị thông báo lỗi
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Khởi động server khi chọn "Open a new IP address"
    private void startServer() {
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("Server is running at " + serverSocket.getInetAddress().getHostAddress());

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                }
            } catch (IOException e) {
                System.out.println("Server stopped: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    // Xử lý khi máy chủ nhận kết nối
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String message = in.readLine();
            if (message != null) {
                // Gửi lại tin nhắn đến client
                out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        }
    }

    // Kết nối tới server từ địa chỉ IP đã chọn
    private void connectToServer(String ip) throws IOException {
        Socket socket = new Socket(ip, 12345); // Giả sử port cố định là 12345
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection Successful");
        alert.setHeaderText(null);
        alert.setContentText("Connected to chat server at IP: " + ip);
        alert.showAndWait();

        // Bắt đầu lắng nghe tin nhắn từ server
        startListening(socket);
    }

    // Lắng nghe tin nhắn từ server
    private void startListening(Socket socket) {
        Thread clientThread = new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    messageArea.appendText(message + "\n");
                }
            } catch (IOException e) {
                System.out.println("Disconnected: " + e.getMessage());
            }
        });
        clientThread.setDaemon(true);
        clientThread.start();
    }
}
