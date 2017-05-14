package ru.atom.gameserver.model;

import ru.atom.gameserver.geometry.Point;

/**
 * Created by gammaker on 05.03.2017.
 */
public class Character extends GameObject implements Movable, Destructible {
    private static final int SPEED = 64;
    private Direction direction = Direction.IDLE;
    private long timeForNextBomb = 0;

    public Character(int x, int y, GameSession session) {
        super(x, y, session);
    }

    @Override
    public void tick(long elapsed) {
        if (direction != Direction.IDLE) move(elapsed);
        timeForNextBomb -= elapsed;
        if (timeForNextBomb < 0) timeForNextBomb = 0;
    }

    @Override
    public void setMotionDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getMotionDirection() {
        return direction;
    }

    private void move(long elapsed) {
        int xpos = pos.x;
        int ypos = pos.y;
        final int delta = (int) (SPEED * elapsed);
        switch (direction) {
            case UP:
                ypos += delta;
                break;
            case DOWN:
                ypos -= delta;
                break;
            case LEFT:
                xpos -= delta;
                break;
            case RIGHT:
                xpos += delta;
                break;
            default:
        }
        pos = new Point(xpos, ypos);
    }

    public boolean plantBomb() {
        if (timeForNextBomb > 0) return false;
        session.addGameObject(new Bomb(getX(), getY(), 5000, session));
        timeForNextBomb = 5000;
        return true;
    }

    @Override
    public String toJson() {
        StringBuilder result = new StringBuilder();
        result.append("{\"type\":\"Pawn\", \"id\":").append(id)
                .append(", \"position\":{\"x\":").append(getX())
                .append(", \"y\":").append(getY()).append("}}");
        return result.toString();
    }

    public void die() {
        pos = null;
    }

    @Override
    public boolean isDead() {
        return pos == null;
    }
}
