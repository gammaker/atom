package ru.atom.gameserver.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.gameserver.model.Movable;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Accumulates all events that come from clients in one tick.
 */
public class TickEventContext {
    private static final Logger log = LogManager.getLogger(TickEventContext.class);

    public final HashMap<Integer, Movable.Direction> moveActions = new HashMap<>();
    public final HashSet<Integer> plantBombActions = new HashSet<>();
    public final HashSet<Integer> dieActions = new HashSet<>();

    public void addEvent(int characterId, String msg) {
        switch (msg) {
            case "MD":
                moveActions.put(characterId, Movable.Direction.DOWN);
                return;

            case "ML":
                moveActions.put(characterId, Movable.Direction.LEFT);
                return;

            case "MR":
                moveActions.put(characterId, Movable.Direction.RIGHT);
                return;

            case "MU":
                moveActions.put(characterId, Movable.Direction.UP);
                return;

            case "PB":
                plantBombActions.add(characterId);
                return;

            default:
                log.error("Invalid message: {}", msg);
        }
    }

    public void addDieEvent(int characterId) {
        dieActions.add(characterId);
    }
}
