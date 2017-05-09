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
    public static final String PROTOCOL = "http://";
    public static final String HOST = "localhost";
    public static final String PORT = ":8090";
    public static final String URL = PROTOCOL + HOST + PORT;

    public static Response addPlayer(int gameSessionId, int playerId, long token, String name) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .post(RequestBody.create(mediaType, ""))
                .url(URL + "/mm/addplayer?" +
                        "session=" + gameSessionId + "&player=" + playerId + "&name=" + name + "&token=" + token)
                .build();

        return client.newCall(request).execute();
    }
}
