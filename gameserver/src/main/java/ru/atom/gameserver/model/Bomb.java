package ru.atom.gameserver.model;

public class Bomb extends GameObject implements Temporary {
    private long leftLifeTimeMs;

    public Bomb(int x, int y, long lifeTimeMs, GameSession session) {
        super(x, y, session);
        leftLifeTimeMs = lifeTimeMs;
    }

    @Override
    public void tick(long elapsed) {
        leftLifeTimeMs -= elapsed;
        if (leftLifeTimeMs < 0) leftLifeTimeMs = 0;
    }

    @Override
    public long getLifetimeMillis() {
        return leftLifeTimeMs;
    }

    @Override
    public boolean isDead() {
        if (leftLifeTimeMs == 0 && pos != null) {
            explode();
            pos = null;
        }
        return pos == null;
    }

    private void explode() {

    }

    @Override
    public void addToReplica(StringBuilder sb) {
        sb.append("B(").append(id)
                .append(",").append(getX())
                .append(",").append(getY())
                .append(")\n");
    }
}
