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
/*                    barCharacter = createCharacterBar(0,1);
                    //barCharacter = new Bar(getX() + 1, getY() + 2, getX() - 1 + WIDTH, getY() + HEIGHT);
                    printCharacter(barCharacter);
                    indexX-=2;
                    for (int i = 0; i <= 2; i++) {
                        indexX++;
                        if (session.getGameMapChar(indexY + 1, indexX) != ' ') {
                            Bar barWall = createWallBar(indexX,indexY,0,1);
                            //Bar barWall = new Bar((indexX) * WIDTH, (indexY + 1) * HEIGHT, (indexX + 1) * WIDTH, (indexY + 2) * HEIGHT);
                            if (barCharacter.isColliding(barWall)) {
                                printSomething(barWall, indexX, indexY + 1);
                                //direction = Direction.IDLE;
                                flag = true;
                                break;
                            }
                        }
                    }*/
                    flag = collisionFlag(indexX, indexY, 0, 1);
                    if (flag) {
                        //System.out.println("Nothing");
                        break;
                    }
                    ypos += delta;
                    //System.out.println("Positon changed");
                    break;
                case DOWN:
/*                    barCharacter = createCharacterBar(0,-1);
                    //barCharacter = new Bar(getX() + 1, getY(), getX() - 1 + WIDTH, getY() - 2 + HEIGHT);
                    printCharacter(barCharacter);
                    indexX-=2;
                    for (int i = 0; i <= 2; i++) {
                        indexX++;
                        if (session.getGameMapChar(indexY - 1, indexX) != ' ') {
                            Bar barWall = createWallBar(indexX,indexY,0,-1);
                            //Bar barWall = new Bar((indexX) * WIDTH, (indexY - 1) * HEIGHT, (indexX + 1) * WIDTH, (indexY) * HEIGHT);
                            if (barCharacter.isColliding(barWall)) {
                                printSomething(barWall, indexX, indexY - 1);
                                //direction = Direction.IDLE;
                                flag = true;
                                break;
                            }
                        }
                    }*/
                    flag = collisionFlag(indexX, indexY, 0, -1);
                    if (flag) {
                        //System.out.println("Nothing");
                        break;
                    }
                    ypos -= delta;
                    //System.out.println("Positon changed");
                    break;
                case LEFT:
/*                    barCharacter = createCharacterBar(-1,0);
                    //barCharacter = new Bar(getX() , getY() + 1, getX() - 2 + WIDTH, getY() - 1 + HEIGHT);
                    printCharacter(barCharacter);
                    indexY-=2;
                    for (int i = 0; i <= 2; i++) {
                        indexY++;
                        if (session.getGameMapChar(indexY, indexX - 1) != ' ') {
                            //Bar barWall = createWallBar(indexX,indexY,-1,0);
                            Bar barWall = new Bar((indexX - 1) * WIDTH, (indexY) * HEIGHT, (indexX) * WIDTH, (indexY + 1) * HEIGHT);
                            if (barCharacter.isColliding(barWall)) {
                                printSomething(barWall, indexX - 1, indexY);
                                //direction = Direction.IDLE;
                                flag = true;
                                break;
                            }
                        }
                    }*/
                    flag = collisionFlag(indexX, indexY, -1, 0);
                    if (flag) {
                        //System.out.println("Nothing");
                        break;
                    }
                    xpos -= delta;
                    //System.out.println("Positon changed");
                    break;
                case RIGHT:
/*                    barCharacter = createCharacterBar(1,0);
                    //barCharacter = new Bar(getX() + 2, getY() + 1, getX() + WIDTH, getY() - 1  + HEIGHT);
                    printCharacter(barCharacter);
                    indexY-=2;
                    for (int i = 0; i <= 2; i++) {
                        indexY++;
                        if (session.getGameMapChar(indexY, indexX + 1) != ' ') {
                            Bar barWall = createWallBar(indexX,indexY,1,0);
                            //Bar barWall = new Bar((indexX + 1) * WIDTH, (indexY) * HEIGHT, (indexX + 2) * WIDTH, (indexY + 1) * HEIGHT);
                            if (barCharacter.isColliding(barWall)) {
                                printSomething(barWall, indexX + 1, indexY);
                                //direction = Direction.IDLE;
                                flag = true;
                                break;
                            }
                        }
                    }*/
                    flag = collisionFlag(indexX, indexY, 1, 0);
                    if (flag) {
                        //System.out.println("Nothing");
                        break;
                    }
                    xpos += delta;
                    //System.out.println("Positon changed");
                    break;
                default:
            }
            session.onObjectMove(this, new Point(xpos, ypos));
/*            session.getGameMap()[IndexY()][IndexX()] = ' ';
            pos = new Point(xpos, ypos);
            session.getGameMap()[IndexY()][IndexX()] = 'c';*/
            //System.out.println("Point has changed");
        }
        catch (Exception e) {
            //Maybe ArrayIndexOutOfBoundsException
            System.out.println(e.getMessage());
            System.out.println("Error");
        }
    }

    public boolean plantBomb() {
        if (timeForNextBomb > 0) return false;
        session.addGameObject(new Bomb(getX(), getY(), 5000, session));
        timeForNextBomb = 5000;
        return true;
    }

    private boolean collisionFlag(int indexX, int indexY, int x, int y) {
        boolean flag = false;
        Bar barCharacter = createCharacterBar(x,y);
        //barCharacter = new Bar(getX() + 1, getY() + 2, getX() - 1 + WIDTH, getY() + HEIGHT);
        //printCharacter(barCharacter);
        if(x==0) {
            indexX-=2;
            flag = true;
        }
        else if(y==0) indexY-=2;
        for (int i = 0; i <= 2; i++) {
            if(flag)
                indexX++;
            else
                indexY++;
            if (session.getGameMapChar(indexY + y, indexX + x) != ' ') {
                Bar barWall = createWallBar(indexX,indexY,x,y);
                //Bar barWall = new Bar((indexX) * WIDTH, (indexY + 1) * HEIGHT, (indexX + 1) * WIDTH, (indexY + 2) * HEIGHT);
                if (barCharacter.isColliding(barWall)) {
                    //printSomething(barWall, indexX, indexY + 1);
                    //direction = Direction.IDLE;
                    return true;
                }
            }
        }
        return false;
    }

    private Bar createCharacterBar(int x, int y) {
        return new Bar(getX() + 1 + x, getY() + 1 + y, getX() - 1 + x + WIDTH, getY() - 1 + y + HEIGHT);
    }

    private Bar createWallBar(int indexX, int indexY, int x, int y) {
        return new Bar((indexX + x) * WIDTH, (indexY + y) * HEIGHT,
                (indexX + x + 1) * WIDTH, (indexY + y + 1) * HEIGHT);
    }

    private void printCharacter(Bar barCharacter) {
        int lX = barCharacter.getLeftX();
        int bY = barCharacter.getBottomY();
        int rX = barCharacter.getLeftX() + barCharacter.getWidth();
        int tY = barCharacter.getBottomY() + barCharacter.getHeight();
        System.out.println("Player: ["+IndexY()+"]["+IndexX()+"]=y:"+getY()+" x:"+getX());
        System.out.println("barCharacter:"+lX+" "+bY+" "+rX+" "+tY);
    }

    private void printSomething(Bar barWall, int indexX, int indexY) {
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
