package ru.atom.gameserver.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import ru.atom.gameserver.message.Message;
import ru.atom.gameserver.message.Topic;
import ru.atom.gameserver.util.JsonHelper;

public class Broker {
    private static final Logger log = LogManager.getLogger(Broker.class);

    private Broker() {
    }

    public static void receive(@NotNull Session session, @NotNull String msg) {
        log.info("RECEIVED: " + msg);
        final Message message = JsonHelper.fromJson(msg, Message.class);
        final MatchController.Player player = ConnectionPool.getPlayerInfo(session);
        player.match.ticker.addEvent(player.characterId, message);
    }

    public static void send(@NotNull Session session, @NotNull Topic topic, @NotNull Object object) {
        final String message = JsonHelper.toJson(new Message(topic, JsonHelper.toJson(object)));
        ConnectionPool.send(session, message);
    }

    public static void broadcast(@NotNull Topic topic, @NotNull String jsonData) {
        final String message = JsonHelper.toJson(new Message(topic, jsonData));
        ConnectionPool.broadcast(message);
    }

}
