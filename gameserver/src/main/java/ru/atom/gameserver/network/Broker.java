package ru.atom.gameserver.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

public class Broker {
    private static final Logger log = LogManager.getLogger(Broker.class);

    private Broker() {
    }

    public static void receive(@NotNull Session session, @NotNull String msg) {
        log.info("RECEIVED: {}", msg);
        final MatchController.Player player = ConnectionPool.getPlayerInfo(session);
        player.match.ticker.addEvent(player.characterId, msg);
    }

    public static void send(@NotNull Session session, @NotNull String msg) {
        log.info("SENDING: {}", msg);
        ConnectionPool.send(session, msg);
    }

    public static void broadcast(@NotNull String msg) {
        log.info("BROADCASTING: {}", msg);
        ConnectionPool.broadcast(msg);
    }

}
