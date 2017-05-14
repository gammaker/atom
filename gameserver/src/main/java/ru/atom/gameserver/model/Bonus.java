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
    public String toJson() {
        StringBuilder result = new StringBuilder();
        result.append("{\"type\":\"").append(type)
                .append("\", \"id\":").append(id)
                .append(", \"position\":{\"x\":").append(getX() / Level.TILE_WIDTH)
                .append(", \"y\":").append(getY() / Level.TILE_HEIGHT).append("}}");
        return result.toString();
    }
}
