package ru.atom.gameserver.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class EventHandler extends WebSocketAdapter {
    private static final Logger log = LogManager.getLogger(EventHandler.class);

    Session session = null;
    Long token = null;

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        this.session = session;
        log.info("Socket Connected: {}", session);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        log.info("Received TEXT message: {}.", message);

        if (message.equals("HB")) return;

        if (token == null && message.startsWith("Token ")) {
            try {
                token = Long.parseLong(message.substring(6));
                log.info("Got token {}.", token);
                ConnectionPool.add(session, token);
            } catch (NumberFormatException e) {
                log.error("Invalid token provided: {}.", message.substring(6));
                session.close();
            }
            return;
        }
        Broker.receive(session, message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        log.info("Socket Closed: [{}] {}", statusCode, reason);
        if (session.isOpen()) {
            session.close();
        }
        ConnectionPool.remove(session);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}
