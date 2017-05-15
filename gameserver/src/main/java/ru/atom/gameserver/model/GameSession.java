package ru.atom.gameserver.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.gameserver.geometry.Point;
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

    private char [][] gameMap = new char[Level.HEIGHT][Level.WIDTH];

    public void initializeGameMap(char [][] gameMap) {
        this.gameMap = gameMap;
    }

    public char[][] getGameMap() {
        return gameMap;
    }

    public void onObjectDestroy(GameObject obj) {
        gameMap[obj.IndexX()][obj.IndexY()] = ' ';
    }

    public void onObjectDestroy(int id) {
        GameObject obj = getObject(id);
        onObjectDestroy(obj);
    }

    public void onObjectMove(Character obj, Point newPos) {
        gameMap[obj.IndexY()][obj.IndexX()] = ' ';
        obj.pos = newPos;
        gameMap[obj.IndexY()][obj.IndexX()] = 'c';
    }

    public void onObjectMove(int id, Point newPos) {
        GameObject obj = getObject(id);
        if(obj instanceof Character) onObjectMove((Character)obj, newPos);
    }

    public void addGameObject(GameObject gameObject) {
        log.info("{} {} was added to game session.", gameObject.getClass().getName(), gameObject.id);
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
        if (obj instanceof Movable) return (Movable)getObject(id);
        return null;
    }

    public Tickable getTickable(int id) {
        GameObject obj = getObject(id);
        if (obj instanceof Tickable) return (Tickable)getObject(id);
        return null;
    }

    public Destructible getTemporary(int id) {
        GameObject obj = getObject(id);
        if (obj instanceof Destructible) return (Destructible)getObject(id);
        return null;
    }

    public Character getCharacter(int id) {
        GameObject obj = getObject(id);
        if (obj instanceof Character) return (Character)getObject(id);
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
        return null;
    }

    public void tick(long elapsed, TickEventContext tickEvents) {
        ArrayList<GameObject> livingObjects = new ArrayList<>(gameObjects.size());
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Character) {
                final Character character = (Character) gameObject;

                final Message.MoveData md = tickEvents == null ? null :
                        tickEvents.moveActions.get(character.id);

                if (md != null) character.setMotionDirection(md.direction);
                else character.setMotionDirection(Movable.Direction.IDLE);

                if (tickEvents.dieActions.contains(character.id))
                    character.die();
            }
            final boolean deleteCurrentObject = gameObject instanceof Destructible
                    && ((Destructible) gameObject).isDead();
            if (!deleteCurrentObject) livingObjects.add(gameObject);
        }
        for (GameObject gameObject : livingObjects) {
            if (gameObject instanceof Character) {
                final Character character = (Character) gameObject;

                final Message.PlantBombData pbd = tickEvents == null ? null :
                        tickEvents.plantBombActions.get(character.id);

                if (pbd != null) character.plantBomb();
            }
            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }
        }
        gameObjects = livingObjects;
    }
}
