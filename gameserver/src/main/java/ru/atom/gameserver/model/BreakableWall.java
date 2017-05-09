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
    public String toJson() {
        StringBuilder result = new StringBuilder();
        result.append("{\"type\":\"Wood\", \"id\":").append(id)
                .append(", \"position\":{\"x\":").append(getX() / Level.TILE_WIDTH)
                .append(", \"y\":").append(getY() / Level.TILE_HEIGHT).append("}}");
        return result.toString();
    }
}
