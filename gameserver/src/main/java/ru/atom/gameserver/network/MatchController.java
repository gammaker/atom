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
        GameSessionTicker gameSessionTicker;
        int id; // Player number in session in range [0, 4)
        int characterId; // id of Character being controlled by player
    }

    public static class MatchData {
        GameSessionTicker gameSessionTicker;
        ArrayList<Player> players = new ArrayList<>(4);
    }

    private static final HashMap<Integer, MatchData> matches = new HashMap<>();
    private static final HashMap<Long, Player> tokenToPlayer = new HashMap<>();

    public static void addPlayerToSession(int gameSessionId, int playerId, long token, String playerName) {
        final Player player = new Player();
        player.name = playerName;
        player.id = playerId;
        synchronized (matches) {
            MatchData data;
            if (matches.containsKey(gameSessionId)) {
                data = matches.get(gameSessionId);
            } else {
                data = new MatchData();
                data.gameSessionTicker = new GameSessionTicker();
                matches.put(gameSessionId, data);
            }
            player.gameSessionTicker = data.gameSessionTicker;
            player.characterId = data.gameSessionTicker.gameSession.getCharacterByPlayerId(playerId).id;
            data.players.add(player);
            tokenToPlayer.put(token, player);
        }
    }

    public static Player getPlayerByToken(long token) {
        return tokenToPlayer.get(token);
    }

    public static void onPlayerConnect(Session session, long playerToken) {
        final Player player = getPlayerByToken(playerToken);
        Broker.send(session, Topic.POSSESS, new Message.PossessData(player.characterId));
    }
}
