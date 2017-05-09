package ru.atom.gameserver.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/mm")
public class MatchControllerResource {
    private static final Logger log = LogManager.getLogger(MatchControllerResource.class);

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Path("/addplayer")
    public Response addPlayer(@QueryParam("session") int gameSessionId,
                              @QueryParam("player") int playerId,
                              @QueryParam("token") long token,
                              @QueryParam("name") @NotNull String playerName) {
        final boolean success = MatchController.addPlayerToSession(gameSessionId, playerId, token, playerName);
        if (!success) return Response.status(Response.Status.BAD_REQUEST).build();
        return Response.ok().build();
    }
}
