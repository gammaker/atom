package ru.atom.gameserver.model;

public interface Temporary extends Destructible, Tickable {
    long getLifetimeMillis();
}
