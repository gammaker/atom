package ru.atom.gameserver.model;

/**
 * Created by Robin on 12.05.2017.
 */
public class Bonus extends GameObject implements Destructible {

    enum Type {
        SPEED, BOMB, FIRE
    }

    private Type type;

    public Bonus(int x, int y, Type type, GameSession session) {
        super(x, y, session);
        this.type = type;
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
        sb.append("b(").append(id)
                .append(",").append(getX())
                .append(",").append(getY())
                .append(",").append(type.ordinal())
                .append(")\n");
    }
}
