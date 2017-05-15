package ru.atom.gameserver.model;

import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;

/**
 * Created by gammaker on 05.03.2017.
 */
public class Character extends GameObject implements Movable, Destructible {

    private final static int HEIGHT = Level.TILE_HEIGHT;
    private final static int WIDTH = Level.TILE_WIDTH;

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
        int indexY = IndexY();
        int indexX = IndexX();
        boolean flag = false;
        try {
            Bar barCharacter;
            switch (direction) {
                case UP:
                    barCharacter = new Bar(getX() + 1, getY() + 2, getX() - 2 + WIDTH, getY() - 1 + HEIGHT);
                    //printCharacter(barCharacter);
                    for (int i = -1; i <= 1; i++) {
                        indexX+=i;
                        if (GameSession.gameMap[indexY + 1][indexX] != ' ') {
                            Bar barWall = new Bar((indexX) * WIDTH, (indexY + 1) * HEIGHT, (indexX + 1) * WIDTH, (indexY + 2) * HEIGHT);
                            if (barCharacter.isColliding(barWall)) {
                                //printSomething(barWall, indexX, indexY + 1);
                                direction = Direction.IDLE;
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) break;
                    ypos += delta;
                    break;
                case DOWN:
                    barCharacter = new Bar(getX() + 1, getY(), getX() - 2 + WIDTH, getY() - 3 + HEIGHT);
                    //printCharacter(barCharacter);
                    for (int i = -1; i <= 1; i++) {
                        indexX+=i;
                        if (GameSession.gameMap[indexY - 1][indexX] != ' ') {
                            Bar barWall = new Bar((indexX) * WIDTH, (indexY - 1) * HEIGHT, (indexX + 1) * WIDTH, (indexY) * HEIGHT);
                            if (barCharacter.isColliding(barWall)) {
                                //printSomething(barWall, indexX, indexY - 1);
                                direction = Direction.IDLE;
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) break;
                    ypos -= delta;
                    break;
                case LEFT:
                    barCharacter = new Bar(getX(), getY() + 1, getX() - 3 + WIDTH, getY() - 2 + HEIGHT);
                    //printCharacter(barCharacter);
                    for (int i = -1; i <= 1; i++) {
                        indexY += i;
                        if (GameSession.gameMap[indexY][indexX - 1] != ' ') {
                            Bar barWall = new Bar((indexX - 1) * WIDTH, (indexY) * HEIGHT, (indexX) * WIDTH, (indexY + 1) * HEIGHT);
                            if (barCharacter.isColliding(barWall)) {
                                //printSomething(barWall, indexX - 1, indexY);
                                direction = Direction.IDLE;
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) break;
                    xpos -= delta;
                    break;
                case RIGHT:
                    barCharacter = new Bar(getX() + 2, getY() + 1, getX() - 1 + WIDTH, getY() - 2 + HEIGHT);
                    //printCharacter(barCharacter);
                    for (int i = -1; i <= 1; i++) {
                        indexY += i;
                        if (GameSession.gameMap[indexY][indexX + 1] != ' ') {
                            Bar barWall = new Bar((indexX + 1) * WIDTH, (indexY) * HEIGHT, (indexX + 2) * WIDTH, (indexY + 1) * HEIGHT);
                            if (barCharacter.isColliding(barWall)) {
                                //printSomething(barWall, indexX + 1, indexY);
                                direction = Direction.IDLE;
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) break;
                    xpos += delta;
                    break;
                default:
            }
            GameSession.gameMap[IndexY()][IndexX()] = ' ';
            pos = new Point(xpos, ypos);
            GameSession.gameMap[IndexY()][IndexX()] = 'c';
            //System.out.println("Point has changed");
        }
        catch (Exception e) {
            //Maybe ArrayIndexOutOfBoundsException
            //System.out.println(e.getMessage());
        }
    }

    public boolean plantBomb() {
        if (timeForNextBomb > 0) return false;
        session.addGameObject(new Bomb(getX(), getY(), 5000, session));
        timeForNextBomb = 5000;
        return true;
    }

    public int IndexX() {
        return (getX()+Level.TILE_HEIGHT/2)/Level.TILE_WIDTH;
    }

    public int IndexY() {
        return (getY()+Level.TILE_HEIGHT/2)/Level.TILE_HEIGHT;
    }

    public void printCharacter(Bar barCharacter) {
        int lX = barCharacter.getLeftX();
        int bY = barCharacter.getBottomY();
        int rX = barCharacter.getLeftX() + barCharacter.getWidth();
        int tY = barCharacter.getBottomY() + barCharacter.getHeight();
        System.out.println("Player: ["+IndexY()+"]["+IndexX()+"]=y:"+getY()+" x:"+getX());
        System.out.println("barCharacter:"+lX+" "+bY+" "+rX+" "+tY);
    }

    public void printSomething(Bar barWall, int indexX, int indexY) {
        int lX1 = barWall.getLeftX();
        int bY1 = barWall.getBottomY();
        int rX1 = barWall.getLeftX() + barWall.getWidth();
        int tY1 = barWall.getBottomY() + barWall.getHeight();
        System.out.println("Colliding bar[" + indexY + "][" + indexX + "]:" +
                lX1 + " " + bY1 + " " + rX1 + " " + tY1);
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
