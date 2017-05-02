package ru.atom.gameserver.model;

/**
 * GameObjects, that can die.
 */
public interface Destructible {
    /**
     * Checks if gameObject is dead. If it becomes dead, executes death actions
     *
     * @return true if GameObject is dead
     */
    boolean isDead();
}
