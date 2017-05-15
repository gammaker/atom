package ru.atom.gameserver.model;

/**
 * Created by gammaker on 05.03.2017.
 */
public class BreakableWall extends GameObject implements Destructible {
    public BreakableWall(int x, int y, GameSession session) {
        super(x, y, session);
    }

    public void destroy() {
        pos = null;
    }

    @Override
    public boolean isDead() {
        return pos == null;
    }

    @Override
    public void addToReplica(StringBuilder sb) {
        sb.append("x(").append(id)
                .append(",").append(getX())
                .append(",").append(getY())
                .append(")\n");
    }

    @Override
    public char getCharCode() {
        return 'x';
    }
}
