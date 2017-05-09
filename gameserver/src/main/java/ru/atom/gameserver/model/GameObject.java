package ru.atom.gameserver.model;

import ru.atom.gameserver.geometry.Point;

/**
 * Created by gammaker on 05.03.2017.
 */
public abstract class GameObject {
    public final int id;
    public final GameSession session;

    // position in 1/1000 of pixels
    protected Point pos;

    public GameObject(int x, int y, GameSession session) {
        pos = new Point(x*1000, y*1000);
        id = session.getUniqueId();
        this.session = session;
    }

    public Point getPosition1000() {
        return pos;
    }

    // x coordinate in pixels
    public int getX() {
        return (pos.x + 500) / 1000;
    }

    // y coordinate in pixels
    public int getY() {
        return (pos.y + 500) / 1000;
    }

    public abstract String toJson();
}
