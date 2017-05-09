package ru.atom.gameserver.network;

import org.eclipse.jetty.websocket.api.Session;
import ru.atom.gameserver.GameSessionTicker;
import ru.atom.gameserver.message.Message;
import ru.atom.gameserver.message.Topic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MatchController manages game sessions on this game server.
 */
public class MatchController {
    public static class Player {
        String name;
        long token;
        MatchData match;
        int id; // Player number in session in range [0, 4)
        int characterId; // id of Character being controlled by player

        boolean isConnected() {
            return characterId != -1;
        }
    }

    public static class MatchData {
        GameSessionTicker ticker;
        ArrayList<Player> players = new ArrayList<>(4);

        boolean areAllPlayersConnected() {
            for (Player player : players) {
                if (!player.isConnected()) return false;
            }
            return true;
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
        player.characterId = -1;
        synchronized (matches) {
            MatchData data;
            if (matches.containsKey(gameSessionId)) {
                data = matches.get(gameSessionId);
            } else {
                data = new MatchData();
                data.ticker = new GameSessionTicker();
                matches.put(gameSessionId, data);
            }
            player.match.ticker = data.ticker;
            data.players.add(player);
            tokenToPlayer.put(token, player);
        }
        return true;
    }

    public static Player getPlayerByToken(long token) {
        return tokenToPlayer.get(token);
    }

    public static void onPlayerConnect(Session session, long playerToken) {
        final Player player = getPlayerByToken(playerToken);
        player.characterId = player.match.ticker.gameSession.getCharacterByPlayerId(player.id).id;
        Broker.send(session, Topic.POSSESS, new Message.PossessData(player.characterId));
        if (player.match.areAllPlayersConnected()) {
            player.match.ticker.start();
        }
    }
}
