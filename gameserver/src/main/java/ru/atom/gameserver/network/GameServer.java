package ru.atom.gameserver.network;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class GameServer {
    public static final boolean SINGLE_SERVER = System.getenv("SINGLE_SERVER") != null;

    public static ContextHandler createGameClientContext(String path) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        ResourceHandler handler = new ResourceHandler();
        handler.setWelcomeFiles(new String[]{"index.html"});

        handler.setResourceBase("bomberman/frontend/src/main/webapp");
        context.setHandler(handler);
        return context;
    }

    public static ContextHandler createGameServerContext(String path) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(path);
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.atom.gameserver.network"
        );
        return context;
    }

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);

        final String gameserverPortEnv = System.getenv("GAME_SERVER_PORT");
        final int port = gameserverPortEnv == null ? 8090 : Integer.parseInt(gameserverPortEnv);

        connector.setPort(port);
        server.addConnector(connector);

        ContextHandlerCollection contexts = new ContextHandlerCollection();

        contexts.setHandlers(new Handler[] {
                createGameClientContext("/"),
                createGameServerContext("/")
        });

        server.setHandler(contexts);

        try {
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
