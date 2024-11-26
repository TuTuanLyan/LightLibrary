package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Chat.MainServer;
import com.lightlibrary.Models.Customer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import jakarta.websocket.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.URL;
import java.util.*;

@ClientEndpoint
public class CustomerChatController implements Initializable, SyncAction {

    @FXML
    private AnchorPane chatRoot;
    @FXML
    private TextField ipField;
    @FXML
    private TextField messageInput;
    @FXML
    private Label adminLabel, userLabel;
    @FXML
    private ImageView connectImageView, disconnectImageView, sendImageView;
    @FXML
    private VBox messageDisplay, adminOnlineBox, userOnlineBox;

    private Session session;
    private String serverUri;
    private Customer customer;
    private final Map<String, Label> pendingMessages = new HashMap<>();

    private String lastSender = "";

    CustomerDashboardController parentController;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
        parentController.setCustomerChatController(this);
    }

    @Override
    public void setParentController(AdminDashboardController parentController) {}

    @Override
    public void setTheme(boolean darkMode) {
        chatRoot.getStylesheets().clear();
        if (darkMode) {
            connectImageView.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/Chat/connectDark.jpg")).toExternalForm()));
            disconnectImageView.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/Chat/disconnectDark.jpg")).toExternalForm()));
            sendImageView.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/Chat/sendMessageDark.jpg")).toExternalForm()));
            chatRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/Chat/dark-chat.css")).toExternalForm());
        } else {
            connectImageView.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/Chat/connectLight.jpg")).toExternalForm()));
            disconnectImageView.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/Chat/disconnectLight.jpg")).toExternalForm()));
            sendImageView.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/Chat/sendMessageLight.jpg")).toExternalForm()));
            chatRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/Chat/light-chat.css")).toExternalForm());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messageInput.setOnAction(e -> {
            sendMessage();
        });
    }

    @Override
    public void autoUpdate() {}

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
            if (session != null && session.isOpen()) {
                showAlert("Connection Status", "Already Connected", "You are already connected to the server.");
                return;
            }

            String serverIp = ipField.getText().trim();

            if (serverIp.isEmpty()) {
                serverIp = MainServer.defaultIp;
            }

            serverUri = "ws://" + serverIp + ":8080/ws/chat?role=user";
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI(serverUri);

            container.connectToServer(this, uri);

            setCustomer(parentController.getCustomer());
            String userName = customer.getUsername();
            session.getBasicRemote().sendText("user:" + userName + ":register");

            showInfo("Connection Status", "Connected Successfully", "You have successfully connected to the server.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Connection Error", "Unable to Connect", "Error: " + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to WebSocket server.");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
        Platform.runLater(() -> {
            if (message.startsWith("[SERVER] message sent.")) {
                Optional<String> sentMessage = pendingMessages.keySet().stream().findFirst();
                sentMessage.ifPresent(msg -> {
                    displayMessage("You: " + msg, true, false);
                    pendingMessages.remove(msg);
                });
            } else if (message.startsWith("[SERVER] recipient")) {
                Optional<String> failedMessage = pendingMessages.keySet().stream().findFirst();
                failedMessage.ifPresent(msg -> {
                    displayMessage("You: " + msg, true, true);
                    pendingMessages.remove(msg);
                });
                String error = message.substring("[SERVER] ".length());
                showAlert("Message Error", "Recipient not found", error);
            } else if (message.startsWith("[SERVER] no one else is online.")) {
                Optional<String> failedMessage = pendingMessages.keySet().stream().findFirst();
                failedMessage.ifPresent(msg -> {
                    displayMessage("You: " + msg, true, true);
                    pendingMessages.remove(msg);
                });
                showAlert("Message Error", "no one else is online", "no one else is online.");
            } else if (message.startsWith("[SERVER] no admin online.")) {
                Optional<String> failedMessage = pendingMessages.keySet().stream().findFirst();
                failedMessage.ifPresent(msg -> {
                    displayMessage("You: " + msg, true, true);
                    pendingMessages.remove(msg);
                });
                showAlert("Message Error", "no admin online", "no admin online.");
            } else if (message.startsWith("[SERVER] no other customer online.")) {
                Optional<String> failedMessage = pendingMessages.keySet().stream().findFirst();
                failedMessage.ifPresent(msg -> {
                    displayMessage("You: " + msg, true, true);
                    pendingMessages.remove(msg);
                });
                showAlert("Message Error", "no other customer online", "no other customer online.");
            } else if (message.startsWith("UPDATE_ADMINS")) {
                updateAdminList(new HashSet<>(Arrays.asList(message.substring("UPDATE_ADMINS:".length()).split(","))));
            } else if (message.startsWith("UPDATE_USERS")) {
                updateUserList(new HashSet<>(Arrays.asList(message.substring("UPDATE_USERS:".length()).split(","))));
            } else {
                displayMessage(message, false, false);
            }
        });
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Session closed: " + session.getId() + ". Reason: " + reason);
    }

    @FXML
    private void sendMessage() {
        try {
            String input = messageInput.getText().trim();

            if (input.isEmpty() || !input.contains(" ")) {
                showAlert("Message Error", "Invalid Format", "Please use the format @recipient message.");
                return;
            }
            if (!input.startsWith("@")) {
                showAlert("Message Error", "Invalid Format", "Message must start with @recipient.");
                return;
            }

            if (session != null && session.isOpen()) {
                int spaceIndex = input.indexOf(" ");
                String recipient = input.substring(1, spaceIndex).trim();
                if (recipient.equals(customer.getUsername())) {
                    showAlert("Message Error", "Self-chat", "You cannot send message to yourself.");
                    return;
                }

                session.getBasicRemote().sendText(input);
                Label messageLabel = new Label("You: " + input);
                pendingMessages.put(input, messageLabel);
                messageInput.clear();
            } else {
                showAlert("Message Error", "No Connection", "You are currently not connecting to the server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Message Error", "Unable to Send", "Error: " + e.getMessage());
        }
    }

    public void autoDisconnect() {
        if (session != null && session.isOpen()) {
            disconnectFromServer();
        }
    }

    @FXML
    private void disconnectFromServer() {
        try {
            if (session != null && session.isOpen()) {
                String userName = customer.getUsername();
                session.getBasicRemote().sendText("user:" + userName + ":unregister");
                session.close();
                showInfo("Connection Status", "Disconnected Successfully", "You have been disconnected from the server.");
            } else {
                showAlert("Connection Status", "No connection", "No active connection to disconnect.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Disconnect Error", "Unable to Disconnect", "Error: " + e.getMessage());
        } finally {
            session = null;

            Platform.runLater(() -> {
                updateAdminList(Collections.emptySet());
                updateUserList(Collections.emptySet());
            });
        }
    }

    private void updateAdminList(Set<String> onlineAdmins) {
        Platform.runLater(() -> {
            adminOnlineBox.getChildren().clear();
            onlineAdmins.remove("");
            int index = 0;

            for (String admin : onlineAdmins) {
                Label adminLabel = new Label(admin);
                adminLabel.setPadding(new Insets(5));

                adminLabel.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) { // Double click để copy
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(admin);
                        clipboard.setContent(content);
                        showInfo("Copied", "Admin name copied", "Admin name copied to clipboard.");
                    }
                });

                HBox nameContainer = new HBox(adminLabel);
                nameContainer.setSpacing(5);

                if (admin.equals(customer.getUsername())) {
                    adminLabel.getStyleClass().add("current-user-item");
                    Label mySelf = new Label("Me");
                    mySelf.getStyleClass().add("self-indicator-item");
                    nameContainer.getChildren().add(mySelf);
                } else {
                    String cssClass = (index % 2 == 0) ? "admin-item-even" : "admin-item-odd";
                    adminLabel.getStyleClass().add(cssClass);
                }

                adminOnlineBox.getChildren().add(nameContainer);
                index++;
            }

            adminLabel.setText("Online admin (" + onlineAdmins.size() + ")");
        });
    }

    private void updateUserList(Set<String> onlineUsers) {
        Platform.runLater(() -> {
            userOnlineBox.getChildren().clear();
            onlineUsers.remove("");
            int index = 0;

            for (String user : onlineUsers) {
                Label userLabel = new Label(user);
                userLabel.setPadding(new Insets(5));

                userLabel.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) { // Double click để copy
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(user);
                        clipboard.setContent(content);
                        showInfo("Copied", "Customer name copied", "Customer name copied to clipboard.");
                    }
                });

                HBox nameContainer = new HBox(userLabel);
                nameContainer.setSpacing(5);

                if (user.equals(customer.getUsername())) {
                    userLabel.getStyleClass().add("current-user-item");
                    Label mySelf = new Label("Me");
                    mySelf.getStyleClass().add("self-indicator-item");
                    nameContainer.getChildren().add(mySelf);
                } else {
                    String cssClass = (index % 2 == 0) ? "user-item-even" : "user-item-odd";
                    userLabel.getStyleClass().add(cssClass);
                }

                userOnlineBox.getChildren().add(nameContainer);
                index++;
            }

            userLabel.setText("Online Customer (" + onlineUsers.size() + ")");
        });
    }

    private void displayMessage(String message, boolean isSender, boolean isError) {
        Platform.runLater(() -> {
            // Phân tách tên người gửi và nội dung tin nhắn
            int atIndex = message.indexOf('@');
            int spaceIndex2nd = message.indexOf(' ', atIndex + 1);
            String sender = message.substring(0, atIndex - 2).trim();
            String recipient = message.substring(atIndex, spaceIndex2nd).trim();

            if (sender.equals("You")) {
                sender += " → " + recipient;
                if (!(recipient.equals("@all") || recipient.equals("@admin") || recipient.equals("@customer"))) {
                    sender += " (direct message)";
                }
            } else {
                if (!(recipient.equals("@all") || recipient.equals("@admin") || recipient.equals("@customer"))) {
                    sender += " → You (direct message)";
                } else {
                    sender += " → " + recipient;
                }
            }

            String messageContent = message.substring(spaceIndex2nd + 1);

            // Nếu tên người gửi khác với tin nhắn trước đó, hiển thị Label tên người gửi
            if (!sender.equals(lastSender)) {
                Label senderLabel = new Label(sender);
                if (recipient.equals("@all")) {
                    senderLabel.getStyleClass().add("everyone-message-name-label");
                } else if (recipient.equals("@admin")) {
                    senderLabel.getStyleClass().add("all-admins-message-name-label");
                } else if (recipient.equals("@customer")) {
                    senderLabel.getStyleClass().add("all-users-message-name-label");
                } else {
                    senderLabel.getStyleClass().add("direct-message-name-label");
                }
                lastSender = sender;

                HBox senderContainer = new HBox(senderLabel);
                senderContainer.setFillHeight(false);

                if (sender.startsWith("You")) {
                    senderContainer.getStyleClass().add("my-message-container");
                } else {
                    senderContainer.getStyleClass().add("other-message-container");
                }
                messageDisplay.getChildren().add(senderContainer);
            }

            Label messageLabel = new Label(messageContent);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(messageDisplay.getWidth() * 0.75);

            HBox messageContainer = new HBox(messageLabel);
            messageContainer.setFillHeight(false);

            messageLabel.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // Double click để copy
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(message);
                    clipboard.setContent(content);
                    showInfo("Copied", "Message copied", "Message copied to clipboard.");
                }
            });

            if (isError) {
                messageLabel.getStyleClass().add("error-message");
                messageContainer.getStyleClass().add("my-message-container");
            } else if (isSender) {
                messageLabel.getStyleClass().add("my-message");
                messageContainer.getStyleClass().add("my-message-container");
            } else {
                String messageClass = (messageDisplay.getChildren().size() % 2 == 0) ? "other-message-even" : "other-message-odd";
                messageLabel.getStyleClass().add(messageClass);
                messageContainer.getStyleClass().add("other-message-container");
            }

            if (messageDisplay.getChildren().size() > 200) {
                messageDisplay.getChildren().removeFirst();
            }

            messageDisplay.getChildren().add(messageContainer);
        });
    }
}
