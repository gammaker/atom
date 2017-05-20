package ru.atom.authmm.server.auth;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.atom.gameserver.network.GameServer;


public class AuthMmServer {
    private static Server jettyServer;
    static final boolean SINGLE_SERVER = System.getenv("SINGLE_SERVER") != null;

    public static void serverRun() throws Exception {
        Database.setUp();

        final ContextHandlerCollection contexts = new ContextHandlerCollection();

        if (SINGLE_SERVER) {
            contexts.setHandlers(new Handler[]{
                    createAuthContext(),
                    createMmContext(),
                    createResourceContext(),
                    GameServer.createGameServerContext("game"),
                    GameServer.createGameClientContext("game")
            });
        } else {
            contexts.setHandlers(new Handler[]{
                    createAuthContext(),
                    createMmContext(),
                    createResourceContext()
            });
        }

        final String portEnv = System.getenv("PORT");
        final int port = portEnv == null ? 8080 : Integer.parseInt(portEnv);

        jettyServer = new Server(port);
        jettyServer.setHandler(contexts);

        jettyServer.start();
    }

    public static void serverStop() throws Exception {
        jettyServer.stop();
    }

    public static void main(String[] args) throws Exception {
        serverRun();
    }

    private static ServletContextHandler createAuthContext() {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/auth");
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.atom.authmm.server.auth"
        );

        return context;
    }

    private static ServletContextHandler createMmContext() {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/mm");
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.atom.authmm.server.mm"
        );

        return context;
    }

    private static ContextHandler createResourceContext() {
        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        ResourceHandler handler = new ResourceHandler();
        handler.setWelcomeFiles(new String[]{"index.html"});

        String serverRoot = AuthMmServer.class.getResource("/static").toString();
        handler.setResourceBase(serverRoot);
        context.setHandler(handler);
        return context;
    }

}
