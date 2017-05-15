package ru.atom.gameserver.model;

import ru.atom.gameserver.network.GameServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by gammaker on 09.05.2017.
 * Map 17 x 13 cells.
 */
public class Level {
    public static final int WIDTH = 17;
    public static final int HEIGHT = 13;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    private static GameObject charToObject(char c, int x, int y, GameSession game) {
        switch (c) {
            case 'w': return new SolidWall(x, y, game);
            case 'x': return new BreakableWall(x, y, game);
            case 'c': return new Character(x, y, game);
            case 'b': return new Bonus(x, y, Bonus.Type.BOMB, game);
            case 'f': return new Bonus(x, y, Bonus.Type.FIRE, game);
            case 's': return new Bonus(x, y, Bonus.Type.SPEED, game);
            default: return null;
        }
    }

    public static char nextChar(InputStream stream) {
        char ch = '\n';
        try {
            while (ch == '\n' || ch == '\r') {
                ch = (char) stream.read();
            }
        } catch (IOException expected) {
            ch = ' ';
        }
        return ch;
    }

    public static void load(String resourceName, GameSession game) {
        final InputStream stream = GameServer.class.getResourceAsStream(resourceName);
        ArrayList<GameObject> characters = new ArrayList<>(4);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final GameObject obj = charToObject(nextChar(stream),
                        x * TILE_WIDTH, (HEIGHT - 1 - y) * TILE_HEIGHT, game);
                if (obj == null) continue;
                if (obj instanceof Character) characters.add(obj);
                else game.addGameObject(obj);
            }
        }
        // Characters must be added to scene after all other objects to be on top of all objects.
        for (GameObject ch : characters) game.addGameObject(ch);
    }
}
