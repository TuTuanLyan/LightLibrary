package com.lightlibrary.Models.Chat;

import jakarta.websocket.*;
import java.net.URI;
import java.util.Scanner;

@ClientEndpoint
public class UserClient {

    private static Session session;

    public void connectToServer() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI serverUri = new URI("ws://localhost:8080/ws/chat"); // Đảm bảo đúng URL
            container.connectToServer(this, serverUri);
            System.out.println("Connected to WebSocket server as User.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("User session opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message from server: " + message);
    }

    @OnError
    public void onError(Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Session closed: " + session.getId() + ". Reason: " + reason);
    }

    public void sendMessage(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
                System.out.println("Message sent: " + message);
            } else {
                System.err.println("Session is not open.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Kết nối WebSocket
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(UserClient.class, URI.create("ws://localhost:8080/ws/chat"));

            // Scanner cho đầu vào từ bàn phím
            Scanner scanner = new Scanner(System.in);

            // Đợi kết nối thành công
            while (session == null) {
                System.out.println("Waiting for connection...");
                Thread.sleep(1000);
            }

            // Đăng ký user
            System.out.println("Enter your user name:");
            String userName = scanner.nextLine();
            session.getBasicRemote().sendText("user:" + userName + ":register");

            // Vòng lặp gửi tin nhắn
            while (true) {
                System.out.println("Enter message:");
                String input = scanner.nextLine();
                session.getBasicRemote().sendText("user:" + userName + ":" + input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
