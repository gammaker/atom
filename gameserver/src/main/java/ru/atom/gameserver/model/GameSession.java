package ru.atom.gameserver.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private List<GameObject> newGameObjects = new ArrayList<>();

    public List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    public static char[][] gameMap = new char[Level.HEIGHT][Level.WIDTH];

    public void addGameObject(GameObject gameObject) {
        log.info("{} {} was added to game session.", gameObject.getClass().getName(), gameObject.id);
        newGameObjects.add(gameObject);
    }

    public GameObject getObject(int id) {
        for (GameObject obj : gameObjects) {
            if (obj.id == id) return obj;
        }
        for (GameObject obj : newGameObjects) {
            if (obj.id == id) return obj;
        }
        return null;
    }

    public Character getCharacterByPlayerId(int playerId) {
        //Предполагаем, что порядок персонажей в gameObjects
        //совпадает с порядком подключения управляющих ими игроков.
        int charactersToSkip = playerId;
        for (GameObject obj : gameObjects) {
            if (!(obj instanceof Character)) continue;
            if (charactersToSkip-- == 0) return (Character)obj;
        }
        for (GameObject obj : newGameObjects) {
            if (!(obj instanceof Character)) continue;
            if (charactersToSkip-- == 0) return (Character)obj;
        }
        return null;
    }


    public StringBuilder tick(long elapsed, TickEventContext tickEvents) {
        ArrayList<GameObject> livingObjects = new ArrayList<>(gameObjects.size());
        StringBuilder replica = new StringBuilder();
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Character) {
                final Character character = (Character) gameObject;

                Movable.Direction direction = null;
                if (tickEvents != null) direction = tickEvents.moveActions.get(character.id);
                if (direction == null) direction = Movable.Direction.IDLE;
                character.setMotionDirection(direction);

                if (tickEvents.plantBombActions.contains(character.id)) {
                    character.plantBomb();
                }

                if (tickEvents.dieActions.contains(character.id)) {
                    character.die();
                }
            }
            final boolean deleteCurrentObject = gameObject instanceof Destructible
                    && ((Destructible) gameObject).isDead();

            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }

            if (!deleteCurrentObject) livingObjects.add(gameObject);
            else replica.append("D(").append(gameObject.id).append(")\n");
        }
        for (GameObject gameObject : newGameObjects) {
            livingObjects.add(gameObject);
            gameObject.addToReplica(replica);
        }
        newGameObjects.clear();
        gameObjects = livingObjects;
        return replica;
    }
}
