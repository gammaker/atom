package ru.atom.gameserver.model;

public interface Movable extends Tickable {
    /**
     * Sets current direction of entity.
     */
    void setMotionDirection(Direction direction);

    Direction getMotionDirection();

    enum Direction {
        UP, DOWN, RIGHT, LEFT, IDLE
    }
}
