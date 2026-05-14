package it.northwind.gruppo2.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("\n🔥 ERRORE VERO DI HIBERNATE QUI SOTTO: 🔥");
            ex.printStackTrace();
            throw new IllegalStateException("Errore durante l'avvio di Hibernate", ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}

