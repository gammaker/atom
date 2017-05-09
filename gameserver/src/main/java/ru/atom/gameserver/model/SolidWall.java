package ru.atom.gameserver.model;

/**
 * Created by gammaker on 01.05.2017.
 */
public class SolidWall extends GameObject {
    public SolidWall(int x, int y, GameSession session) {
        super(x, y, session);
    }


    @Override
    public String toJson() {
        StringBuilder result = new StringBuilder();
        result.append("{\"type\":\"Wall\", \"id\":").append(id)
                .append(", \"position\":{\"x\":").append(getX() / Level.TILE_WIDTH)
                .append(", \"y\":").append(getY() / Level.TILE_HEIGHT).append("}}");
        return result.toString();
    }
}
