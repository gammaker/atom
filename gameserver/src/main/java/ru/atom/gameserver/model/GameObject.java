package ru.atom.gameserver.model;

import ru.atom.gameserver.geometry.Point;

/**
 * Created by gammaker on 05.03.2017.
 */
public abstract class GameObject {
    public final int id;
    public final GameSession session;
    protected Point pos;

    public GameObject(int x, int y, GameSession session) {
        pos = new Point(x, y);
        id = session.getUniqueId();
        this.session = session;
    }

    public Point getPosition() { return pos; }
}
