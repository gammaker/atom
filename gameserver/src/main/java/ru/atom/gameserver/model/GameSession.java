package ru.atom.gameserver.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.gameserver.geometry.Point;
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
    private StringBuilder replica = new StringBuilder();

    public List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    private char[][] gameMap = new char[Level.HEIGHT][Level.WIDTH];

    public char getGameMapChar(int y, int x) {
        if (y < 0 || y >= Level.HEIGHT || x < 0 || x >= Level.WIDTH) return ' ';
        return gameMap[y][x];
    }

    public void onObjectDestroy(GameObject obj) {
        if (obj.IndexX() >= 0 && obj.IndexX() < Level.WIDTH &&
                obj.IndexY() >= 0 & obj.IndexY() < Level.HEIGHT) {
            if (gameMap[obj.IndexY()][obj.IndexX()] == obj.getCharCode()) {
                gameMap[obj.IndexY()][obj.IndexX()] = ' ';
            }
        }
    }

    public void onObjectDestroy(int id) {
        GameObject obj = getObject(id);
        onObjectDestroy(obj);
    }

    public void onObjectMove(GameObject obj, Point newPos) {
        final char c = gameMap[obj.IndexY()][obj.IndexX()];
        if (c == obj.getCharCode()) {
            gameMap[obj.IndexY()][obj.IndexX()] = ' ';
            gameMap[(newPos.y + 500) / (1000 * Level.TILE_WIDTH)][(newPos.x + 500) / (1000 * Level.TILE_HEIGHT)] = c;
        }

        replica.append("M(").append(obj.id)
                .append(",").append((newPos.x + 500) / 1000)
                .append(",").append((newPos.y + 500) / 1000)
                .append(")\n");
    }

    public void onObjectMove(int id, Point newPos) {
        onObjectMove(getObject(id), newPos);
    }

    public void addGameObject(GameObject gameObject) {
        log.info("{} {} was added to game session.", gameObject.getClass().getName(), gameObject.id);
        newGameObjects.add(gameObject);
        if (gameObject instanceof Fire) return;
        gameMap[gameObject.IndexY()][gameObject.IndexX()] = gameObject.getCharCode();
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

    private static boolean objectIsDead(GameObject obj) {
        if (!(obj instanceof Destructible)) return false;
        return ((Destructible) obj).isDead();
    }

    private void processCharacterActions(Character character, TickEventContext tickEvents) {
        Movable.Direction direction = null;
        if (tickEvents != null) direction = tickEvents.moveActions.get(character.id);
        if (direction == null) direction = Movable.Direction.IDLE;
        character.setMotionDirection(direction);

        if (tickEvents == null) return;

        if (tickEvents.plantBombActions.contains(character.id)) {
            character.plantBomb();
        }

        if (tickEvents.dieActions.contains(character.id)) {
            character.die();
        }
    }

    public StringBuilder tick(long elapsed, TickEventContext tickEvents) {
        ArrayList<GameObject> livingObjects = new ArrayList<>(gameObjects.size());
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Character) {
                processCharacterActions((Character) gameObject, tickEvents);
            }

            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }

            if (!objectIsDead(gameObject)) livingObjects.add(gameObject);
            else replica.append("D(").append(gameObject.id).append(")\n");
        }
        for (GameObject gameObject : newGameObjects) {
            livingObjects.add(gameObject);
            gameObject.addToReplica(replica);
        }
        newGameObjects.clear();
        gameObjects = livingObjects;

        final StringBuilder curReplica = replica;
        replica = new StringBuilder();
        return curReplica;
    }
}
