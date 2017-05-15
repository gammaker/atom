package ru.atom.gameserver.model;

import java.util.Random;

/**
 * Created by Robin on 12.05.2017.
 */
public class Bonus extends GameObject implements Destructible {

    enum Type {
        SPEED, BOMB, FIRE
    }

    public final Type type;

    public static Type genRandomTypeOrNull() {
        Random rand = new Random();
        switch (rand.nextInt(10)) {
            case 0: return Bonus.Type.BOMB;
            case 1: return Bonus.Type.FIRE;
            case 2: return Bonus.Type.SPEED;
            default: return null;
        }
    }

    public Bonus(int x, int y, Type type, GameSession session) {
        super(x, y, session);
        this.type = type;
    }

    public void destroy() {
        session.onObjectDestroy(this);
        pos = null;
    }

    @Override
    public boolean isDead() {
        return pos == null;
    }

    @Override
    public void addToReplica(StringBuilder sb) {
        sb.append("b(").append(id)
                .append(",").append(getX())
                .append(",").append(getY())
                .append(",").append(type.ordinal())
                .append(")\n");
    }

    @Override
    public char getCharCode() {
        return 'b';
    }
}
