package connection;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class RelatorioConnection {
    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "12345678";

    private static Driver driver;

    static {
        initialize();
    }

    public RelatorioConnection() {
    }

    private static void initialize() {
        driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
    }

    public static Driver getDriver() {
        return driver;
    }

    public static void close() {
        driver.close();
    }
}
