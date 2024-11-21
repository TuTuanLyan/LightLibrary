package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jakarta.websocket.*;
import javafx.scene.layout.AnchorPane;

import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@ClientEndpoint
public class CustomerChatController implements Initializable, SyncAction {

    @FXML
    private AnchorPane chatRoot;
    @FXML
    private TextField ipField;           // TextField để nhập IP server
    @FXML
    private Button connectButton;       // Nút để kết nối tới server WebSocket
    @FXML
    private TextArea messageDisplay;    // TextArea để hiển thị tin nhắn
    @FXML
    private TextField messageInput;     // TextField để nhập tin nhắn
    @FXML
    private Button sendButton;          // Nút gửi tin nhắn

    private Session session;            // Kết nối WebSocket
    private String serverUri;           // Địa chỉ WebSocket của server
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    CustomerDashboardController parentController;

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void setParentController(AdminDashboardController parentController) {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Thiết lập sự kiện cho nút Connect và Send
        connectButton.setOnAction(event -> connectToServer());
        sendButton.setOnAction(event -> sendMessage());
    }

    @FXML
    private void connectToServer() {
        System.out.println("connect button clicked");

        try {
            // Kiểm tra nếu đã kết nối, tránh kết nối lại
            if (session != null && session.isOpen()) {
                System.out.println("Already connected to server.");
                return;
            }

            // Lấy IP server từ TextField
            String serverIp = ipField.getText().trim();

            // Kiểm tra nếu IP trống
            if (serverIp.isEmpty()) {
                showAlert("Error", "IP Address is required", "Please enter a valid server IP address.");
                return;
            }

            // Xây dựng URI kết nối
            serverUri = "ws://" + serverIp + ":8080/ws/chat?role=user";
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI(serverUri);

            // Kết nối tới server
            container.connectToServer(this, uri);

            System.out.println("Connected to WebSocket server at " + serverUri);

            customer = parentController.getCustomer();

            String userName = customer.getUsername();
            System.out.println(userName);
            session.getBasicRemote().sendText("user:" + userName + ":register");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Connection Error", "Unable to connect to server", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    // Khi kết nối WebSocket thành công
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to WebSocket server.");
    }

    // Nhận tin nhắn từ server
    @OnMessage
    public void onMessage(String message) {
        messageDisplay.appendText(message + "\n");
    }

    // Lỗi WebSocket
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    // Kết nối WebSocket bị đóng
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Session closed: " + session.getId() + ". Reason: " + reason);
    }

    // Gửi tin nhắn từ User đến server
    @FXML
    private void sendMessage() {
        try {
            String message = messageInput.getText().trim();
            if (session != null && session.isOpen() && !message.isEmpty()) {
                session.getBasicRemote().sendText(message);
                messageDisplay.appendText("You: " + message + "\n");
                messageInput.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
