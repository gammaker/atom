package ru.atom.gameserver.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.gameserver.message.Message;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Accumulates all events that come from clients in one tick.
 */
public class TickEventContext {
    private static final Logger log = LogManager.getLogger(TickEventContext.class);

    public final HashMap<Integer, Message.MoveData> moveActions = new HashMap<>();
    public final HashMap<Integer, Message.PlantBombData> plantBombActions = new HashMap<>();
    public final HashSet<Integer> dieActions = new HashSet<>();

    public void addEvent(int characterId, Message message) {
        try {
            switch (message.topic) {
                case MOVE:
                    moveActions.put(characterId, message.moveData());
                    return;

                case PLANT_BOMB:
                    plantBombActions.put(characterId, message.plantBombData());
                    return;

                default:
            }
        } catch (RuntimeException e) {
            log.error("Invalid message data for topic {}: {}.", message.topic, message.data);
            e.printStackTrace();
        }
    }

    public void addDieEvent(int characterId) {
        dieActions.add(characterId);
    }
}
