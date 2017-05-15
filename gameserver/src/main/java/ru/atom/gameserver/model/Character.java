package ru.atom.gameserver.model;

import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;

/**
 * Created by gammaker on 05.03.2017.
 */
public class Character extends GameObject implements Movable, Destructible {

    private static final int HEIGHT = Level.TILE_HEIGHT;
    private static final int WIDTH = Level.TILE_WIDTH;

    private Direction direction = Direction.IDLE;
    private long timeForNextBomb = 0;

    private static int INITIAL_SPEED = 128;
    private static int INITIAL_BOMB_DELAY = 2500;
    private static int INITIAL_BOMB_STRENGTH = 1;
    private static int BONUS_BOMB_STRENGTH = 5;

    private int speed = INITIAL_SPEED;
    private long bombDelay = INITIAL_BOMB_DELAY;
    private int bombStrength = INITIAL_BOMB_STRENGTH;

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
        final int delta = (int) (speed * elapsed);
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
        session.addGameObject(new Bomb(IndexX() * Level.TILE_WIDTH + Level.TILE_WIDTH / 4,
                IndexY() * Level.TILE_HEIGHT - Level.TILE_HEIGHT / 4, 2500, bombStrength, session));
        timeForNextBomb = bombDelay;
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
            final char mapChar = session.getGameMapChar(indexY + y, indexX + x);
            if (mapChar == 'w' || mapChar == 'x' || mapChar == 'b') {
                Bar barWall = createWallBar(indexX, indexY, x, y);
                if (barCharacter.isColliding(barWall)) {
                    if (mapChar == 'b') {
                        pickBonus(indexX, indexY);
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void pickBonus(int tileX, int tileY) {
        for (GameObject obj : session.getGameObjects()) {
            if (!(obj instanceof Bonus)) continue;
            if (obj.IndexX() != tileX || obj.IndexY() != tileY) continue;
            final Bonus bonus = (Bonus) obj;
            switch (bonus.type) {
                case SPEED:
                    speed = INITIAL_SPEED * 2;
                    break;

                case BOMB:
                    bombDelay = INITIAL_BOMB_DELAY / 2;
                    break;

                case FIRE:
                    bombStrength = BONUS_BOMB_STRENGTH;
                    break;
            }
            bonus.destroy();
        }
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
        session.onObjectDestroy(this);
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
