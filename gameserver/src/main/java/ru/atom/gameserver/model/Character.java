package ru.atom.gameserver.model;

import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;

/**
 * Created by gammaker on 05.03.2017.
 */
public class Character extends GameObject implements Movable, Destructible {

    private static final int HEIGHT = Level.TILE_HEIGHT;
    private static final int WIDTH = Level.TILE_WIDTH;

    private static final int SPEED = 128;
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
        int indexY = IndexY();
        int indexX = IndexX();
        try {
            switch (direction) {
                case UP:
                    if (collisionFlag(indexX, indexY, 0, 1, delta)) break;
                    ypos += delta;
                    break;
                case DOWN:
                    if (collisionFlag(indexX, indexY, 0, -1, delta)) break;
                    ypos -= delta;
                    break;
                case LEFT:
                    if (collisionFlag(indexX, indexY, -1, 0, delta)) break;
                    xpos -= delta;
                    break;
                case RIGHT:
                    if (collisionFlag(indexX, indexY, 1, 0, delta)) break;
                    xpos += delta;
                    break;
                default:
            }
            final Point newPos = new Point(xpos, ypos);
            session.onObjectMove(this, newPos);
            pos = newPos;
        }
        catch (Exception e) {
            //Maybe ArrayIndexOutOfBoundsException
            System.out.println(e.getMessage());
        }
    }

    public boolean plantBomb() {
        if (timeForNextBomb > 0) return false;
        session.addGameObject(new Bomb(IndexX() * Level.TILE_WIDTH + Level.TILE_WIDTH / 2,
                IndexY() * Level.TILE_HEIGHT - Level.TILE_HEIGHT / 2, 2500, 1, session));
        timeForNextBomb = 2500;
        return true;
    }

    private boolean collisionFlag(int indexX, int indexY, int x, int y, int delta) {
        boolean flag = false;
        Bar barCharacter = createCharacterBar(x, y, delta);
        if(x == 0) {
            indexX -= 2;
            flag = true;
        }
        else if(y == 0) indexY -= 2;
        for (int i = 0; i <= 2; i++) {
            if(flag) indexX++;
            else indexY++;
            if (session.getGameMapChar(indexY + y, indexX + x) != ' ') {
                Bar barWall = createWallBar(indexX, indexY, x, y);
                if (barCharacter.isColliding(barWall)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Bar createCharacterBar(int x, int y, int delta) {
        return new Bar((pos.x + delta + 500) / 1000 + x,
                (pos.y + delta + 500) / 1000 + y,
                (pos.x - delta + 500) / 1000 + x + WIDTH,
                (pos.y -delta + 500) / 1000 + y + HEIGHT);
    }

    private Bar createWallBar(int indexX, int indexY, int x, int y) {
        return new Bar((indexX + x) * WIDTH, (indexY + y) * HEIGHT,
                (indexX + x + 1) * WIDTH, (indexY + y + 1) * HEIGHT);
    }

    @Override
    public void addToReplica(StringBuilder sb) {
        sb.append("c(").append(id)
                .append(",").append(getX())
                .append(",").append(getY())
                .append(")\n");
    }

    public void die() {
        pos = null;
    }

    @Override
    public boolean isDead() {
        return pos == null;
    }

    @Override
    public char getCharCode() {
        return 'c';
    }
}
