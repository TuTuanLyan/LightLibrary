package com.lightlibrary.Models.Chat;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat")
public class ChatServer {
    private static final Map<String, Session> admins = new ConcurrentHashMap<>();
    private static final Map<String, Session> users = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New connection: " + session.getId());
         broadcastOnlineLists();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            // Xử lý đăng ký user hoặc admin
            if (message.startsWith("user:") && message.endsWith(":register")) {
                String username = message.split(":")[1]; // Lấy username từ message
                users.put(username, session); // Thêm vào danh sách user
                System.out.println("User registered: " + username);
                broadcastOnlineLists(); // Gửi danh sách user/admin cập nhật
                return;
            } else if (message.startsWith("admin:") && message.endsWith(":register")) {
                String username = message.split(":")[1];
                admins.put(username, session); // Thêm vào danh sách admin
                System.out.println("Admin registered: " + username);
                broadcastOnlineLists(); // Gửi danh sách user/admin cập nhật
                return;
            }

            // Xử lí sign out
            if (message.startsWith("admin:") && message.endsWith(":unregister")) {
                String username = message.split(":")[1];
                admins.remove(username);
                System.out.println("Admin unregistered: " + username);
                broadcastOnlineLists();
                return;
            } else if (message.startsWith("user:") && message.endsWith(":unregister")) {
                String username = message.split(":")[1];
                users.remove(username);
                System.out.println("User unregistered: " + username);
                broadcastOnlineLists();
                return;
            }

            // Kiểm tra tin nhắn có đúng định dạng không
            if (!message.startsWith("@")) {
                session.getBasicRemote().sendText("Invalid message format. Use @recipient message.");
                return;
            }

            // Phân tách '@recipient' và nội dung tin nhắn
            int spaceIndex = message.indexOf(" ");
            if (spaceIndex == -1) {
                session.getBasicRemote().sendText("Invalid message format. Use @recipient message.");
                return;
            }

            String recipient = message.substring(1, spaceIndex).trim(); // Lấy tên recipient sau ký tự '@'
            String msgContent = message.substring(spaceIndex + 1).trim(); // Nội dung tin nhắn

            boolean isAdmin = admins.containsValue(session);
            String senderType = isAdmin ? "[Admin]" : "[Customer]";
            String senderName = getSenderName(session);

            switch (recipient.toLowerCase()) {
                case "all": // Gửi tin nhắn đến toàn bộ người dùng online
                    broadcastMessageToAll(senderType + " " + senderName + ": " + message, session);
                    break;

                case "admin": // Gửi tin nhắn đến tất cả admin online
                    broadcastMessageToAdmins(senderType + " " + senderName + ": " + message, session);
                    break;

                case "customer": // Gửi tin nhắn đến tất cả user online
                    broadcastMessageToCustomers(senderType + " " + senderName + ": " + message, session);
                    break;

                default: // Gửi tin nhắn riêng
                    sendPrivateMessage(senderName, recipient, message, session, isAdmin);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getBasicRemote().sendText("An error occurred while processing your message.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private String getSenderRole(Session session) {
        if (admins.containsValue(session)) {
            return "Admin";
        } else if (users.containsValue(session)) {
            return "User";
        }
        return "Unknown";
    }

    private String getSenderName(Session session) {
        for (Map.Entry<String, Session> entry : admins.entrySet()) {
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }
        for (Map.Entry<String, Session> entry : users.entrySet()) {
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }
        return "Unknown";
    }

    private void broadcastMessageToAll(String message, Session senderSession) throws IOException {
        if (admins.size() + users.size() == 1) {
            senderSession.getBasicRemote().sendText("[SERVER] no one else is online.");
            return;
        }

        for (Session adminSession : admins.values()) {
            try {
                if (!adminSession.equals(senderSession)) {
                    adminSession.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Session userSession : users.values()) {
            try {
                if (!userSession.equals(senderSession)) {
                    userSession.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        senderSession.getBasicRemote().sendText("[SERVER] message sent.");
    }

    private void broadcastMessageToAdmins(String message, Session senderSession) throws IOException {
        if (admins.isEmpty()) {
            senderSession.getBasicRemote().sendText("[SERVER] no admin online.");
            return;
        } else if (admins.size() == 1 && admins.containsValue(senderSession)) {
            senderSession.getBasicRemote().sendText("[SERVER] no other admin online.");
            return;
        }

        for (Session adminSession : admins.values()) {
            try {
                if (!adminSession.equals(senderSession)) {
                    adminSession.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        senderSession.getBasicRemote().sendText("[SERVER] message sent.");
    }

    private void broadcastMessageToCustomers(String message, Session senderSession) throws IOException {
        if (users.isEmpty()) {
            senderSession.getBasicRemote().sendText("[SERVER] no user online.");
            return;
        } else if (users.size() == 1 && users.containsValue(senderSession)) {
            senderSession.getBasicRemote().sendText("[SERVER] no other customer online.");
            return;
        }

        for (Session userSession : users.values()) {
            try {
                if (!userSession.equals(senderSession)) {
                    userSession.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        senderSession.getBasicRemote().sendText("[SERVER] message sent.");
    }

    private void sendPrivateMessage(String senderName, String recipient, String msgContent, Session senderSession, boolean isSenderAdmin) {
        try {
            if (isSenderAdmin) {
                // Gửi từ admin đến user
                if (users.containsKey(recipient)) {
                    users.get(recipient).getBasicRemote().sendText("[Admin] " + senderName + ": " + msgContent);
                    admins.get(senderName).getBasicRemote().sendText("[SERVER] message sent.");
                } else {
                    senderSession.getBasicRemote().sendText("[SERVER] recipient @" + recipient + " not found.");
                }
            } else {
                // Gửi từ user đến admin
                if (admins.containsKey(recipient)) {
                    admins.get(recipient).getBasicRemote().sendText("[Customer] " + senderName + ": " + msgContent);
                    users.get(senderName).getBasicRemote().sendText("[SERVER] message sent.");
                } else {
                    senderSession.getBasicRemote().sendText("[SERVER] recipient @" + recipient + " not found.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        admins.values().removeIf(s -> s.equals(session));
        users.values().removeIf(s -> s.equals(session));
        System.out.println("Connection closed: " + session.getId());
        broadcastOnlineLists();
    }

    private void broadcastOnlineLists() {
        // Gửi danh sách admin
        String adminList = String.join(",", admins.keySet());
        System.out.println(adminList);
        // Gửi danh sách user
        String userList = String.join(",", users.keySet());
        System.out.println(userList);

        for (Session session : admins.values()) {
            try {
                session.getBasicRemote().sendText("UPDATE_ADMINS:" + adminList);
                session.getBasicRemote().sendText("UPDATE_USERS:" + userList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Session session : users.values()) {
            try {
                session.getBasicRemote().sendText("UPDATE_ADMINS:" + adminList);
                session.getBasicRemote().sendText("UPDATE_USERS:" + userList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
