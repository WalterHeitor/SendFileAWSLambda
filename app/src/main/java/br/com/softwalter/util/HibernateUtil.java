package br.com.softwalter.util;

import br.com.softwalter.entity.Contract;
import br.com.softwalter.entity.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            try {
                String userName = System.getenv("DATABASE_USERNAME");
                String password = System.getenv("DATABASE_PASSWORD");
                String dataBaseName = System.getenv("DATABASE_NAME");
                String schema = System.getenv("DATABASE_SCHEMA");
                String port = System.getenv("DATABASE_PORT");
                String host = System.getenv("DATABASE_HOST");

                Configuration configuration = new Configuration();

                // Configurações do banco de dados PostgreSQL
                configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://"+host+":"+port+"/" + dataBaseName+ "?currentSchema=" + schema);
                configuration.setProperty("hibernate.connection.username", userName);
                configuration.setProperty("hibernate.connection.password", password);
                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

                // Mapeamento de classes de entidade
                configuration.addAnnotatedClass(br.com.softwalter.entity.Person.class);
                configuration.addAnnotatedClass(br.com.softwalter.entity.Contract.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Erro ao construir a fábrica de sessões do Hibernate.", e);
            }
        }
        return sessionFactory;
    }

    public static Session getSession() {
        return getSessionFactory().openSession();
    }

    public static void closeSession() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;  // Reseta a fábrica para permitir a criação de uma nova instância
        }
    }
}
