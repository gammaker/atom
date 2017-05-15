package ru.atom.gameserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.gameserver.model.GameSession;
import ru.atom.gameserver.network.Broker;
import ru.atom.gameserver.network.TickEventContext;
import ru.atom.gameserver.util.JsonHelper;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class GameSessionTicker extends Thread {
    private static final Logger log = LogManager.getLogger(GameSessionTicker.class);
    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;
    private long tickNumber = 0;

    public final GameSession gameSession = new GameSession();

    private TickEventContext eventContext = new TickEventContext();

    public synchronized TickEventContext startNextTick() {
        final TickEventContext result = eventContext;
        eventContext = new TickEventContext();
        return result;
    }

    public synchronized void addEvent(int objectId, String msg) {
        eventContext.addEvent(objectId, msg);
    }

    public synchronized void addDieEvent(int objectId) {
        eventContext.addDieEvent(objectId);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            long started = System.currentTimeMillis();
            act(FRAME_TIME);
            long elapsed = System.currentTimeMillis() - started;
            if (elapsed < FRAME_TIME) {
                //log.info("All tick finish at {} ms", elapsed);
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(FRAME_TIME - elapsed));
            } else {
                log.warn("tick lag {} ms", elapsed - FRAME_TIME);
            }
            //log.info("{}: tick ", tickNumber);
            tickNumber++;
        }
    }

    private void act(long time) {
        final TickEventContext prevTickContext = startNextTick();
        gameSession.tick(time, prevTickContext);
        final String replicaJson = "{\"objects\":" + JsonHelper.toJson(gameSession.getGameObjects()) + "}";
        Broker.broadcast("{\"topic\":\"REPLICA\",\"data\":" + replicaJson + "}");
    }

    public long getTickNumber() {
        return tickNumber;
    }
}
