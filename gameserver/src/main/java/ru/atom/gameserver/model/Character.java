package ru.atom.gameserver.model;

import ru.atom.gameserver.geometry.Point;

/**
 * Created by gammaker on 05.03.2017.
 */
public class Character extends GameObject implements Movable {
    private static int SPEED = 5;
    private Direction direction = Direction.IDLE;
    private long timeForNextBomb = 0;

    public Character(int x, int y, GameSession session) {
        super(x, y, session);
    }

    @Override
    public void tick(long elapsed) {
        move(elapsed);
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
        switch (direction) {
            case UP:
                ypos += SPEED * elapsed;
                break;
            case DOWN:
                ypos -= SPEED * elapsed;
                break;
            case LEFT:
                xpos -= SPEED * elapsed;
                break;
            case RIGHT:
                xpos += SPEED * elapsed;
                break;
            default:
        }
        pos = new Point(xpos, ypos);
    }

    public boolean plantBomb() {
        if (timeForNextBomb > 0) return false;
        session.addGameObject(new Bomb(pos.x, pos.y, 5000, session));
        return true;
    }
}
