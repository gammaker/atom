package ru.atom.gameserver.model;

import ru.atom.gameserver.network.GameServer;

import java.io.IOException;
import java.io.InputStream;

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
            default: return null;
        }
    }

    private static char nextChar(InputStream stream) {
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
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final GameObject obj = charToObject(nextChar(stream),
                        x * TILE_WIDTH, (HEIGHT - 1 - y) * TILE_HEIGHT, game);
                if (obj != null) game.addGameObject(obj);
            }
        }
    }
}
