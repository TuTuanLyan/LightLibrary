package com.lightlibrary.Models.Chat;

import com.lightlibrary.Models.User;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat")
public class ChatServer {
    private static final Map<String, Session> admins = new ConcurrentHashMap<>();
    private static final Map<String, Session> users = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            String[] parts = message.split(":", 3);
            String type = parts[0];  // admin/user
            String name = parts[1]; // adminName/userName
            String content = parts[2]; // actual message or command

            // Handle registration
            if ("register".equalsIgnoreCase(content)) {
                if ("admin".equalsIgnoreCase(type)) {
                    admins.put(name, session);
                    session.getBasicRemote().sendText("Registered as admin: " + name);
                } else if ("user".equalsIgnoreCase(type)) {
                    users.put(name, session);
                    session.getBasicRemote().sendText("Registered as user: " + name);
                }
                return;
            }

            // Chat message
            String recipient = content.split(" ", 2)[0]; // target user name
            String msg = content.split(" ", 2)[1];       // actual message

            System.out.println(recipient + " " + msg);

            if ("admin".equalsIgnoreCase(type)) {
                if (users.containsKey(recipient)) {
                    users.get(recipient).getBasicRemote().sendText("Admin " + name + ": " + msg);
                } else {
                    session.getBasicRemote().sendText("User " + recipient + " not found.");
                }
            } else if ("user".equalsIgnoreCase(type)) {
                if (admins.containsKey(recipient)) {
                    admins.get(recipient).getBasicRemote().sendText("User " + name + ": " + msg);
                } else {
                    session.getBasicRemote().sendText("Admin " + recipient + " not found.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        admins.values().removeIf(s -> s.equals(session));
        users.values().removeIf(s -> s.equals(session));
        System.out.println("Connection closed: " + session.getId());
    }
}
