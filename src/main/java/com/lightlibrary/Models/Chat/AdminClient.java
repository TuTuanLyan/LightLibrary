package com.lightlibrary.Models.Chat;

import jakarta.websocket.*;
import java.net.URI;
import java.util.Scanner;

@ClientEndpoint
public class AdminClient {

    private static Session session;

    public void connectToServer() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI serverUri = new URI("ws://localhost:8080/ws/chat?role=admin"); // Thêm tham số role=admin
            container.connectToServer(this, serverUri);
            System.out.println("Connected to WebSocket server as Admin.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Admin session opened: " + session.getId());
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
            container.connectToServer(AdminClient.class, URI.create("ws://10.10.69.203:8080/ws/chat?role=admin"));

            // Scanner cho đầu vào từ bàn phím
            Scanner scanner = new Scanner(System.in);

            // Đợi kết nối thành công trước khi gửi tin nhắn
            while (session == null) {
                System.out.println("Waiting for connection...");
                Thread.sleep(1000);
            }

            // Đăng ký admin
            System.out.println("Enter your admin name:");
            String adminName = scanner.nextLine();
            session.getBasicRemote().sendText("admin:" + adminName + ":register");

            // Vòng lặp gửi tin nhắn
            while (true) {
                System.out.println("Enter message (format: recipient message):");
                String input = scanner.nextLine();
                session.getBasicRemote().sendText("admin:" + adminName + ":" + input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
