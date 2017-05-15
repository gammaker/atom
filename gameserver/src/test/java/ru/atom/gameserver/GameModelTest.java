package ru.atom.gameserver;

import org.junit.Assert;
import org.junit.Test;
import ru.atom.gameserver.geometry.Point;
import ru.atom.gameserver.model.GameObject;
import ru.atom.gameserver.model.GameSession;
import ru.atom.gameserver.model.Movable;
import ru.atom.gameserver.model.Temporary;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameModelTest {
    @Test
    public void gameIsCreated() {
        GameSession gameSession = TestGameSessionCreator.createGameSession();
        Assert.assertNotNull(gameSession);
    }

    @Test
    public void gameObjectsAreInstantiated() {
        GameSession gameSession = TestGameSessionCreator.createGameSession();
        List<GameObject> gameObjects = gameSession.getGameObjects();
        Assert.assertNotNull(gameObjects);
        Assert.assertFalse(gameObjects.size() == 0);
    }

    /**
     * Checks that Movable GameObjects-s move
     * Collisions are ignored
     */
    @Test
    public void movement() {
        GameSession gameSession = TestGameSessionCreator.createGameSession();
        List<GameObject> gameObjects = gameSession.getGameObjects();

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Movable) {
                final Movable movable = (Movable) gameObject;
                final Point firstPosition = gameObject.getPosition();

                movable.setMotionDirection(Movable.Direction.UP);
                movable.tick(1000);
                Point currentPosition = gameObject.getPosition();
                Assert.assertTrue(currentPosition.y > firstPosition.y);

                movable.setMotionDirection(Movable.Direction.DOWN);
                movable.tick(1000);
                currentPosition = gameObject.getPosition();
                Assert.assertTrue(currentPosition.x == firstPosition.x);

                movable.setMotionDirection(Movable.Direction.RIGHT);
                movable.tick(1000);
                currentPosition = gameObject.getPosition();
                Assert.assertTrue(currentPosition.x > firstPosition.x);

                movable.setMotionDirection(Movable.Direction.LEFT);
                movable.tick(1000);
                currentPosition = gameObject.getPosition();
                Assert.assertTrue(currentPosition.y == firstPosition.y);

                movable.setMotionDirection(Movable.Direction.IDLE);
                movable.tick(1000);
                currentPosition = gameObject.getPosition();
                Assert.assertTrue(currentPosition.y == firstPosition.y);
            }
        }
    }

    /**
     * Test checks that all temporary objects live at least for some time and are dead after very long time
     */
    @Test
    public void ticking() {
        GameSession gameSession = TestGameSessionCreator.createGameSession();
        List<Temporary> temporaries = gameSession.getGameObjects().stream()
                .filter(o -> o instanceof Temporary)
                .map(o -> (Temporary) o).collect(Collectors.toList());

        Assert.assertFalse(temporaries.isEmpty());

        final long maxLifeTime = temporaries.stream().max(
                Comparator.comparingLong(Temporary::getLifetimeMillis)).get().getLifetimeMillis();
        final long minLifeTime = temporaries.stream().min(
                Comparator.comparingLong(Temporary::getLifetimeMillis)).get().getLifetimeMillis();

        gameSession.tick(minLifeTime - 1, null);
        List<Temporary> temporariesAfterSmallTime = gameSession.getGameObjects().stream()
                .filter(o -> o instanceof Temporary)
                .map(o -> (Temporary) o).collect(Collectors.toList());
        Assert.assertTrue(temporaries.containsAll(temporariesAfterSmallTime));
        Assert.assertTrue(temporariesAfterSmallTime.containsAll(temporaries));

        gameSession.tick(maxLifeTime + 1, null);
        gameSession.tick(1000, null);
        temporaries = gameSession.getGameObjects().stream()
                .filter(o -> o instanceof Temporary)
                .map(o -> (Temporary) o).collect(Collectors.toList());
        Assert.assertTrue(temporaries.isEmpty());
    }
}