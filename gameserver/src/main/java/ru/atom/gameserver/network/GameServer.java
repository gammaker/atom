package ru.atom.gameserver.network;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.InputStream;

public class GameServer {
    private static ContextHandler createGameClientContext() {
        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        ResourceHandler handler = new ResourceHandler();
        handler.setWelcomeFiles(new String[]{"index.html"});

        handler.setResourceBase("bomberman/frontend/src/main/webapp");
        context.setHandler(handler);
        return context;
    }

    private static ContextHandler createGameServerContext() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
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
        connector.setPort(8090);
        server.addConnector(connector);

        ContextHandlerCollection contexts = new ContextHandlerCollection();

        contexts.setHandlers(new Handler[] {
                createGameClientContext(),
                createGameServerContext()
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
