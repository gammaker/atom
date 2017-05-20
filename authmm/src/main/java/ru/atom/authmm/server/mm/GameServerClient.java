package ru.atom.authmm.server.mm;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;

import java.io.IOException;

/**
 * Created by gammaker on 09.05.2017.
 */
public class GameServerClient {
    private static final OkHttpClient client = new OkHttpClient();
    public static final String LOCAL_URL;

    static {
        final String gameserverUrlEnv = System.getenv("GAME_SERVER_URL");
        if (gameserverUrlEnv != null) {
            LOCAL_URL = "http://" + gameserverUrlEnv;
        } else {
            final boolean singleServer = System.getenv("SINGLE_SERVER") != null;
            String port = System.getenv("PORT");
            if (port == null) {
                if (singleServer) port = "8080";
                else port = "8090";
            }
            String path = "";
            if (singleServer) path = "/game";
            LOCAL_URL = "http://localhost:" + port + path;
        }
    }

    public static Response addPlayer(int gameSessionId, int playerId, long token, String name) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .post(RequestBody.create(mediaType, ""))
                .url(LOCAL_URL + "/mm/addplayer?" +
                        "session=" + gameSessionId + "&player=" + playerId + "&name=" + name + "&token=" + token)
                .build();

        return client.newCall(request).execute();
    }
}
