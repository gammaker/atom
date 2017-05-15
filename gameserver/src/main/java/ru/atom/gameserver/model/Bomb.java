package ru.atom.gameserver.model;

public class Bomb extends GameObject implements Temporary {
    private long leftLifeTimeMs;
    private int strength;

    public Bomb(int x, int y, long lifeTimeMs, int strength, GameSession session) {
        super(x, y, session);
        leftLifeTimeMs = lifeTimeMs;
        this.strength = strength;
    }

    @Override
    public void tick(long elapsed) {
        leftLifeTimeMs -= elapsed;
        if (leftLifeTimeMs <= 0 && pos != null) {
            explode();
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

    private void explode() {
        //TODO создать огни, расходящиеся в 4 стороны на strength клеток
    }

    @Override
    public void addToReplica(StringBuilder sb) {
        sb.append("B(").append(id)
                .append(",").append(getX())
                .append(",").append(getY())
                .append(")\n");
    }

    @Override
    public char getCharCode() {
        return 'B';
    }
}
