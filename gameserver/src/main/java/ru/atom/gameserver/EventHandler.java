package ru.atom.gameserver;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import ru.atom.gameserver.network.ConnectionPool;

public class EventHandler extends WebSocketAdapter {
    Session session = null;
    Long token = null;

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        this.session = session;
        System.out.println("Socket Connected: " + session);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        if (token == null && message.startsWith("Token ")) {
            token = Long.parseLong(message.substring(6));
            ConnectionPool.getInstance().add(session, token);
            return;
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}
