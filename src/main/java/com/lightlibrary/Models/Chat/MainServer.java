package com.lightlibrary.Models.Chat;

import org.glassfish.tyrus.server.Server;

public class MainServer {
    public static String defaultIp = "192.168.13.147";

    public static void main(String[] args) {
        Server server = new Server(defaultIp, 8080, "/ws", null, ChatServer.class);

        try {
            server.start();
            System.out.println("WebSocket server started");
            System.in.read(); // Giữ server chạy
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
