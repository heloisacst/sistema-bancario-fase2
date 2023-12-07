package connection;

import org.neo4j.driver.Session;

public class RelatorioConnection {
    private static final ConnectionManager neo4jConnectionManager = new ConnectionManager();

    public static Session getSession() {
        return neo4jConnectionManager.getDriver().session();
    }

    public static void close() {
        neo4jConnectionManager.close();
    }

    public RelatorioConnection() {
    }

}
