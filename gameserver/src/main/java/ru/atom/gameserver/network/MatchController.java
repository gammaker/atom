package ru.atom.gameserver.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import ru.atom.gameserver.GameSessionTicker;
import ru.atom.gameserver.model.Character;
import ru.atom.gameserver.model.Level;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MatchController manages game sessions on this game server.
 */
public class MatchController {
    private static final Logger log = LogManager.getLogger(MatchControllerResource.class);
    public static final int PLAYERS_PER_GAME = 4;

    public static class Player {
        Session session;
        String name;
        long token;
        MatchData match;
        int id; // Player number in session in range [0, 4)
        int characterId; // id of Character being controlled by player

        boolean isConnected() {
            return session != null;
        }
    }

    public static class MatchData {
        GameSessionTicker ticker;
        ArrayList<Player> players = new ArrayList<>(PLAYERS_PER_GAME);
        int id;
        boolean isStarted = false;

        boolean areAllPlayersConnected() {
            if (players.size() < PLAYERS_PER_GAME) return false;
            for (Player player : players) {
                if (!player.isConnected()) return false;
            }
            return true;
        }

        public void broadcast(String msg) {
            //log.info("BROADCASTING (game {}): {}", id, msg);
            for (Player player : players) ConnectionPool.send(player.session, msg);
        }
    }

    private static final HashMap<Integer, MatchData> matches = new HashMap<>();
    private static final HashMap<Long, Player> tokenToPlayer = new HashMap<>();

    public static boolean addPlayerToSession(int gameSessionId, int playerId, long token, String playerName) {
        if (tokenToPlayer.containsKey(token)) return false;

        final Player player = new Player();
        player.name = playerName;
        player.id = playerId;
        player.token = token;
        synchronized (matches) {
            MatchData data;
            if (matches.containsKey(gameSessionId)) {
                data = matches.get(gameSessionId);
                if (data.players.size() >= PLAYERS_PER_GAME) {
                    log.error("Game session {} is full. Cannot connect new player.", gameSessionId);
                    return false;
                }
            } else {
                data = new MatchData();
                data.ticker = new GameSessionTicker(data);
                data.id = gameSessionId;
                Level.load("/map.txt", data.ticker.gameSession);
                matches.put(gameSessionId, data);
            }
            player.match = data;
            final Character character = player.match.ticker.gameSession.getCharacterByPlayerId(player.id);
            if (character == null) {
                log.error("Invalid player id {} of player {}.", player.id, player.name);
                return false;
            }
            player.characterId = character.id;
            data.players.add(player);
            tokenToPlayer.put(token, player);
        }
        log.info("EXPECT PLAYER {} with token {} to connect to MATCH {}, with INDEX {}.",
                playerName, token, playerId, gameSessionId);
        return true;
    }

    public static Player getPlayerByToken(long token) {
        return tokenToPlayer.get(token);
    }

    public static void onPlayerConnect(Session session, long playerToken) {
        final Player player = getPlayerByToken(playerToken);
        if (player == null) {
            log.error("Invalid connect: player with token {} not found!", playerToken);
            return;
        }
        if (player.isConnected()) {
            log.warn("Player {} is already connected!", player.name);
            return;
        }
        player.session = session;
        Broker.send(session, "Possess(" + player.characterId + ")");
        log.info("CONNECTED PLAYER {}, to MATCH {}, INDEX {}!", player.name, player.match.id, player.id);
        synchronized (player.match) {
            if (player.match.areAllPlayersConnected()) {
                player.match.isStarted = true;
                player.match.ticker.start();
                log.info("STARTING MATCH {} with PLAYERS [{}, {}, {}, {}]!", player.match.id,
                        player.match.players.get(0).name,
                        player.match.players.get(1).name,
                        player.match.players.get(2).name,
                        player.match.players.get(3).name);
            }
        }
    }

    public static void onPlayerDisconnect(Player player) {
        synchronized (player.match) {
            if (player.match.isStarted) {
                player.match.players.remove(player);
                if (player.isConnected()) player.match.ticker.addDieEvent(player.characterId);
                tokenToPlayer.remove(player.token);
                log.info("DISCONNECTED PLAYER {} from running MATCH {}! Killing its pawn.",
                        player.name, player.match.id);
            } else {
                player.session = null;
                log.info("DISCONNECTED PLAYER {} from waiting MATCH {}! Waiting for reconnect...",
                        player.name, player.match.id);
            }
            if (player.match.players.isEmpty() && player.match.ticker.isAlive()) {
                player.match.ticker.interrupt();
                log.info("STOPPING MATCH {}!", player.match.id);
                synchronized (matches) {
                    matches.remove(player.match.id);
                }
            }
        }
    }
}
