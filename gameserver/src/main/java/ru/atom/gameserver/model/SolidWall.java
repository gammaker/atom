package ru.atom.gameserver.model;

/**
 * Created by gammaker on 01.05.2017.
 */
public class SolidWall extends GameObject {
    public SolidWall(int x, int y, GameSession session) {
        super(x, y, session);
    }


    @Override
    public void addToReplica(StringBuilder sb) {
        sb.append("W(").append(id)
                .append(",").append(getX())
                .append(",").append(getY())
                .append(")\n");
    }
}
