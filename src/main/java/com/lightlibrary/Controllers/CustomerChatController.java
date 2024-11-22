package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Chat.MainServer;
import com.lightlibrary.Models.Customer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import jakarta.websocket.*;
import javafx.scene.layout.AnchorPane;

import java.net.URI;
import java.net.URL;
import java.util.*;

@ClientEndpoint
public class CustomerChatController implements Initializable, SyncAction {

    @FXML
    private AnchorPane chatRoot;
    @FXML
    private TextField ipField;           // TextField để nhập IP server
    @FXML
    private Button connectButton, disconnectButton;       // Nút để kết nối tới server WebSocket
    @FXML
    private TextArea messageDisplay;    // TextArea để hiển thị tin nhắn
    @FXML
    private TextField messageInput;     // TextField để nhập tin nhắn
    @FXML
    private Button sendButton;          // Nút gửi tin nhắn
    @FXML
    private TextArea adminListField, userListField;
    @FXML
    private Label adminLabel, userLabel;

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
    public void setParentController(AdminDashboardController parentController) {}

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
        disconnectButton.setOnAction(event -> disconnectFromServer());
    }

    private void showAlert(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void connectToServer() {
        try {
            // Kiểm tra nếu đã kết nối, tránh kết nối lại
            if (session != null && session.isOpen()) {
                showAlert("Connection Status", "Already Connected", "You are already connected to the server.");
                return;
            }

            // Lấy IP server từ TextField
            String serverIp = ipField.getText().trim();

            // Kiểm tra nếu IP trống
            if (serverIp.isEmpty()) {
                serverIp = MainServer.defaultIp;
            }

            // Xây dựng URI kết nối
            serverUri = "ws://" + serverIp + ":8080/ws/chat?role=user";
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI(serverUri);

            // Kết nối tới server
            container.connectToServer(this, uri);

            customer = parentController.getCustomer();
            String userName = customer.getUsername();
            session.getBasicRemote().sendText("user:" + userName + ":register");

            // Hiển thị thông báo kết nối thành công
            showInfo("Connection Status", "Connected Successfully", "You have successfully connected to the server.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Connection Error", "Unable to Connect", "Error: " + e.getMessage());
        }
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
        Platform.runLater(() -> {
            if (message.startsWith("UPDATE_ADMINS")) {
                // Dữ liệu admin được server gửi dưới dạng: UPDATE_ADMINS:admin1,admin2,admin3
                String adminData = message.substring("UPDATE_ADMINS:".length());
                Set<String> onlineAdmins = new HashSet<>(Arrays.asList(adminData.split(",")));
                updateAdminList(onlineAdmins);
            } else if (message.startsWith("UPDATE_USERS")) {
                // Dữ liệu user được server gửi dưới dạng: UPDATE_USERS:user1,user2,user3
                String userData = message.substring("UPDATE_USERS:".length());
                Set<String> onlineUsers = new HashSet<>(Arrays.asList(userData.split(",")));
                updateUserList(onlineUsers);
            } else {
                // Xử lý các tin nhắn khác
                messageDisplay.appendText(message + "\n");
            }
        });
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
            String input = messageInput.getText().trim();

            // Kiểm tra cú pháp
            if (input.isEmpty() || !input.contains(" ")) {
                showAlert("Message Error", "Invalid Format", "Please use the format @recipient message.");
                return;
            }
            if (!input.startsWith("@")) {
                showAlert("Message Error", "Invalid Format", "Message must start with @recipient.");
                return;
            }

            // Gửi tin nhắn
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(input);
                messageDisplay.appendText("You: " + input + "\n");
                messageInput.clear();
            } else {
                showAlert("Message Error", "Connection Closed", "Cannot send message because the connection is closed. Please press connect button at the bottom right of the screen.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Message Error", "Unable to Send", "Error: " + e.getMessage());
        }
    }

    @FXML
    private void disconnectFromServer() {
        try {
            if (session != null && session.isOpen()) {
                session.close(); // Đóng kết nối WebSocket
                showInfo("Connection Status", "Disconnected from the server", "You have successfully connected to the server.");
            } else {
                showAlert("Connection Status", "No connection", "No active connection to disconnect.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Disconnect Error", "Unable to Disconnect", "Error: " + e.getMessage());
        } finally {
            session = null; // Đặt session về null

            // Cập nhật danh sách admin và user thành rỗng
            Platform.runLater(() -> {
                updateAdminList(Collections.emptySet());
                updateUserList(Collections.emptySet());
            });
        }
    }

    private void updateAdminList(Set<String> onlineAdmins) {
        // Hiển thị danh sách admin online
        Platform.runLater(() -> {
            String adminList = String.join("\n", onlineAdmins);
            adminListField.setText(adminList);
            adminLabel.setText("Online admin (" + onlineAdmins.size() + ")");
        });
    }

    private void updateUserList(Set<String> onlineUsers) {
        Platform.runLater(() -> {
            // Hiển thị danh sách user online
            String userList = String.join("\n", onlineUsers);
            userListField.setText(userList);
            userLabel.setText("Online user (" + onlineUsers.size() + ")");
        });
    }
}
