package ru.atom.gameserver.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.gameserver.message.Message;
import ru.atom.gameserver.network.TickEventContext;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private static final Logger log = LogManager.getLogger(GameSession.class);

    private int maxId = 0;

    public int getUniqueId() {
        return ++maxId;
    }

    private List<GameObject> gameObjects = new ArrayList<>();

    public List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    public void addGameObject(GameObject gameObject) {
        log.info(gameObject.getClass().getName() + " " + gameObject.id + " was added to game session.");
        gameObjects.add(gameObject);
    }

    public GameObject getObject(int id) {
        for (GameObject obj : gameObjects) {
            if (obj.id == id) return obj;
        }
        return null;
    }

    public Movable getMovable(int id) {
        GameObject obj = getObject(id);
        if (obj instanceof Movable) return (Movable) getObject(id);
        return null;
    }

    public Tickable getTickable(int id) {
        GameObject obj = getObject(id);
        if (obj instanceof Tickable) return (Tickable) getObject(id);
        return null;
    }

    public Destructible getTemporary(int id) {
        GameObject obj = getObject(id);
        if (obj instanceof Destructible) return (Destructible) getObject(id);
        return null;
    }

    public Character getCharacter(int id) {
        GameObject obj = getObject(id);
        if (obj instanceof Character) return (Character) getObject(id);
        return null;
    }

    public Character getCharacterByPlayerId(int playerId) {
        //Предполагаем, что порядок персонажей в gameObjects
        //совпадает с порядком подключения управляющих ими игроков.
        int charactersToSkip = playerId;
        for (GameObject obj : gameObjects) {
            if (!(obj instanceof Character)) continue;
            if (charactersToSkip-- == 0) return (Character) obj;
        }
        return null;
    }

    public void tick(long elapsed, TickEventContext tickEvents) {
        log.info("tick " + elapsed + " ms.");
        ArrayList<GameObject> livingObjects = new ArrayList<>(gameObjects.size());
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Character) {
                final Character character = (Character) gameObject;

                final Message.MoveData md = tickEvents == null ? null :
                        tickEvents.moveActions.get(character.id);

                //TODO: plant bomb

                if (md != null) character.setMotionDirection(md.direction);
                else character.setMotionDirection(Movable.Direction.IDLE);
            }
            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }
            final boolean deleteCurrentObject =
                    gameObject instanceof Destructible &&
                            ((Destructible) gameObject).isDead();
            if (!deleteCurrentObject) livingObjects.add(gameObject);
        }
        gameObjects = livingObjects;
    }
}
