package ru.atom.gameserver.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionPool {
    private static final Logger log = LogManager.getLogger(ConnectionPool.class);
    private static final int PARALLELISM_LEVEL = 4;

    private static final ConcurrentHashMap<Session, MatchController.Player>
            sessionToPlayerInfo = new ConcurrentHashMap<>();

    private ConnectionPool() {
    }

    public static void send(@NotNull Session session, @NotNull String msg) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(msg);
            } catch (IOException ignored) {
            }
        }
    }

    public static void broadcast(@NotNull String msg) {
        sessionToPlayerInfo.forEachKey(PARALLELISM_LEVEL, session -> send(session, msg));
    }

    public static void shutdown() {
        sessionToPlayerInfo.forEachKey(PARALLELISM_LEVEL, session -> {
            if (session.isOpen()) {
                session.close();
            }
        });
    }

    public static MatchController.Player getPlayerInfo(Session session) {
        return sessionToPlayerInfo.get(session);
    }

    public static Session getSession(String playerName) {
        return sessionToPlayerInfo.entrySet().stream()
                .filter(entry -> entry.getValue().name.equals(playerName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(null);
    }

    public static void add(Session session, long playerToken) {
        MatchController.Player player = MatchController.getPlayerByToken(playerToken);
        if (player == null) throw new RuntimeException(
                "Error! Player must be registered by match maker before connection!");
        if (sessionToPlayerInfo.putIfAbsent(session, player) == null) {
            log.info("{} joined", player.name);
            MatchController.onPlayerConnect(session, playerToken);
        }
    }

    public static void remove(Session session) {
        final MatchController.Player player = sessionToPlayerInfo.get(session);
        if (player == null) return;
        MatchController.onPlayerDisconnect(player);
        sessionToPlayerInfo.remove(session);
    }
}
