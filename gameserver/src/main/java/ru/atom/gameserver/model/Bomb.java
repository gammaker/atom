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

    private void killObjectsInTile(int tileX, int tileY) {
        for (GameObject obj : session.getGameObjects()) {
            if (!(obj instanceof Destructible)) continue;
            if (((Destructible) obj).isDead()) continue;
            if (obj instanceof BreakableWall) {
                if (obj.IndexX() != tileX || obj.IndexY() != tileY) continue;
                ((BreakableWall) obj).destroy();
            }
            else if (obj instanceof Character) {
                Character character = (Character) obj;
                if (!character.intersectsTile(tileX, tileY)) continue;
                character.die();
            }
        }
    }

    private void placeFire(int tileX, int tileY) {
        killObjectsInTile(tileX, tileY);
        session.addGameObject(new Fire(tileX * Level.TILE_WIDTH,
                tileY * Level.TILE_HEIGHT, 1000, session));
    }

    private void explode() {
        final int tileX = IndexX();
        final int tileY = IndexY();
        placeFire(tileX, tileY);
        for (int x = tileX - 1; x >= tileX - strength; x--) {
            if (session.getGameMapChar(tileY, x) == 'w') break;
            placeFire(x, tileY);
        }
        for (int x = tileX + 1; x <= tileX + strength; x++) {
            if (session.getGameMapChar(tileY, x) == 'w') break;
            placeFire(x, tileY);
        }
        for (int y = tileY - 1; y >= tileY - strength; y--) {
            if (session.getGameMapChar(y, tileX) == 'w') break;
            placeFire(tileX, y);
        }
        for (int y = tileY + 1; y <= tileY + strength; y++) {
            if (session.getGameMapChar(y, tileX) == 'w') break;
            placeFire(tileX, y);
        }
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
