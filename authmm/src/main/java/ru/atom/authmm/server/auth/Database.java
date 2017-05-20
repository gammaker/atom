package ru.atom.authmm.server.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.net.URI;


public class Database {
    private static final Logger log = LogManager.getLogger(Database.class);
    /**
     * SessionFactory abstracts
     */
    private static SessionFactory sessionFactory;

    public static Session session() {
        return sessionFactory.openSession();
    }

    private Database() {
    }

    private static void applyEnv(StandardServiceRegistryBuilder builder) throws Exception {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) return;

        if (!databaseUrl.contains("@")) {
            if (!databaseUrl.startsWith("jdbc:postgresql://")) databaseUrl = "jdbc:postgresql://" + databaseUrl;
            builder.applySetting("hibernate.connection.url", databaseUrl);

            final String databaseUser = System.getenv("DATABASE_USER");
            final String databasePassword = System.getenv("DATABASE_PASSWORD");
            if (databaseUser != null) {
                builder.applySetting("hibernate.connection.username", databaseUser);
            }
            if (databasePassword != null) {
                builder.applySetting("hibernate.connection.password", databasePassword);
            }
            return;
        }

        final URI dbUri = new URI(databaseUrl);
        final String[] usernamePassword = dbUri.getUserInfo().split(":");
        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        builder.applySetting("hibernate.connection.username", usernamePassword[0]);
        builder.applySetting("hibernate.connection.password", usernamePassword[1]);
        builder.applySetting("hibernate.connection.url", dbUrl);
    }

    /**
     * This is preferred way to initialize SessionFactory
     */
    public static void setUp() throws Exception {
        // A SessionFactory is set up once for an application!

        // configures settings from hibernate.cfg.xml
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure();

        // override some settings from environment variables
        applyEnv(builder);

        final StandardServiceRegistry registry = builder.build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }

}