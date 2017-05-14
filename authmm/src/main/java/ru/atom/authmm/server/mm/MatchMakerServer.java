package ru.atom.authmm.server.mm;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.atom.authmm.server.auth.CrossBrowserFilter;
import ru.atom.authmm.server.auth.Database;

import javax.servlet.DispatcherType;
import java.util.EnumSet;


public class MatchMakerServer {
    private static Server jettyServer;

    public static void serverStop() throws Exception {
        if (jettyServer != null) jettyServer.stop();
    }

    public static void serverRun() throws Exception {
        Database.setUp();

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/mm/*");

        jettyServer = new Server(8081);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "com.sun.jersey.spi.container.ContainerResponseFilters",
                CrossBrowserFilter.class.getCanonicalName()
        );

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.atom.authmm.server.mm"
        );

        jettyServer.start();
    }

    public static void main(String[] args) throws Exception {
        serverRun();
    }
}