package ru.atom.gameserver.model;

/**
 * Created by gammaker on 15.05.2017.
 */
public class Fire extends GameObject implements Temporary {
    private long leftLifeTimeMs;

    public Fire(int x, int y, long lifeTimeMs, GameSession session) {
        super(x, y, session);
        leftLifeTimeMs = lifeTimeMs;
    }

    @Override
    public void tick(long elapsed) {
        leftLifeTimeMs -= elapsed;
        if (leftLifeTimeMs <= 0 && pos != null) {
            session.onObjectDestroy(this);
            pos = null;
        }
    }

    @Override
    public long getLifetimeMillis() {
        return leftLifeTimeMs;
    }

    @Override
    public boolean isDead() {
        return pos == null;
    }

    @Override
    public void addToReplica(StringBuilder sb) {
        sb.append("f(").append(id)
                .append(",").append(getX())
                .append(",").append(getY())
                .append(")\n");
    }

    @Override
    public char getCharCode() {
        return 'f';
    }
}
