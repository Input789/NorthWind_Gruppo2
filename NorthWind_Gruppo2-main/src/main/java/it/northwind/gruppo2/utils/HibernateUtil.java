package it.northwind.gruppo2.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class HibernateUtil {

    private static final String DATABASE_NAME = "northwind.db";
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            String databaseUrl = resolveDatabaseUrl();
            configuration.setProperty("connection.url", databaseUrl);
            configuration.setProperty("hibernate.connection.url", databaseUrl);
            configuration.setProperty("jakarta.persistence.jdbc.url", databaseUrl);
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("\nERRORE DI HIBERNATE:");
            ex.printStackTrace();
            throw new IllegalStateException("Errore durante l'avvio di Hibernate", ex);
        }
    }

    private static String resolveDatabaseUrl() {
        try {
            Path appDataDirectory = Path.of(System.getProperty("user.home"), ".northwind-gruppo2");
            Files.createDirectories(appDataDirectory);

            Path runtimeDatabase = appDataDirectory.resolve(DATABASE_NAME);
            if (Files.notExists(runtimeDatabase)) {
                copyBundledDatabase(runtimeDatabase);
                System.out.println("Database iniziale copiato in: " + runtimeDatabase);
            } else {
                System.out.println("Database runtime rilevato in: " + runtimeDatabase);
            }

            return "jdbc:sqlite:" + runtimeDatabase.toAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile preparare il database runtime.", e);
        }
    }

    private static void copyBundledDatabase(Path destination) throws IOException {
        try (InputStream inputStream = HibernateUtil.class.getClassLoader().getResourceAsStream(DATABASE_NAME)) {
            if (inputStream == null) {
                throw new IllegalStateException("File " + DATABASE_NAME + " non trovato nel classpath.");
            }

            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
