package connection;

import org.neo4j.driver.Session;

public class RelatorioConnection {
    private static final Neo4jConnectionManager neo4jConnectionManager = new Neo4jConnectionManager();

    public static Session getSession() {
        return neo4jConnectionManager.getDriver().session();
    }

    public static void close() {
        neo4jConnectionManager.close();
    }

    public RelatorioConnection() {
    }

}
