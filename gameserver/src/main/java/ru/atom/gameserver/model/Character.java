package ru.atom.gameserver.model;

import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;

/**
 * Created by gammaker on 05.03.2017.
 */
public class Character extends GameObject implements Movable, Destructible {

    private static final int HEIGHT = Level.TILE_HEIGHT - 8;
    private static final int WIDTH = Level.TILE_WIDTH - 8;

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
        if (isDead()) return;
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
        if (pos == null) return;
        int xpos = pos.x;
        int ypos = pos.y;
        final int delta = (int) (speed * elapsed);
        try {
            switch (direction) {
                case UP:
                    if (collisionFlag(0, delta)) break;
                    ypos += delta;
                    break;
                case DOWN:
                    if (collisionFlag(0, -delta)) break;
                    ypos -= delta;
                    break;
                case LEFT:
                    if (collisionFlag(-delta, 0)) break;
                    xpos -= delta;
                    break;
                case RIGHT:
                    if (collisionFlag(delta, 0)) break;
                    xpos += delta;
                    break;
                default:
            }
            if (xpos == pos.x && ypos == pos.y) return;
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

    private boolean collisionFlag(int dx, int dy) {
        final int tileX = (pos.x + dx + 4500) / (1000 * Level.TILE_WIDTH);
        final int tileY = (pos.y + dy + 4500) / (1000 * Level.TILE_HEIGHT);
        final Bar barCharacter = createCharacterBar(dx, dy);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                final char mapChar = session.getGameMapChar(tileY + j, tileX + i);
                if (mapChar == 'w' || mapChar == 'x' || mapChar == 'b') {
                    final Bar tileBar = Level.createTileBar(tileX + i, tileY + j);
                    if (barCharacter.isColliding(tileBar)) {
                        if (mapChar == 'b') {
                            pickBonus(tileX + i, tileY + j);
                            continue;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean intersectsTile(int tileX, int tileY) {
        return Level.createTileBar(tileX, tileY)
                .isColliding(createCharacterBar());
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

    public Bar createCharacterBar(int dx, int dy) {
        return Bar.fromPosAndSize((pos.x + dx + 500) / 1000 + 4,
                (pos.y + dy + 500) / 1000 + 4,
                WIDTH, HEIGHT );
    }

    public Bar createCharacterBar() {
        return Bar.fromPosAndSize(getX() + 4, getY() + 4, WIDTH , HEIGHT);
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
