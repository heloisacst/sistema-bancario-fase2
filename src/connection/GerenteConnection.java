package connection;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import java.util.Map;

public class GerenteConnection {

    private static final String URI = "bolt://localhost:7687"; // Altere conforme necessário
    private static final String USER = "neo4j";
    private static final String PASSWORD = "udesc123"; // Altere conforme necessário

    private static Driver driver;

    static {
        initialize();
    }

    private static void initialize() {
        driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
    }

    public static Session getSession() {
        return driver.session();
    }

    public static void close() {
        driver.close();
    }


    public void consultarDadosGerente(int matricula) {
        try (Session session = getSession()) {
            Record record = session.readTransaction(tx -> tx.run(
                            "MATCH (g:Gerente {matricula: $matricula}) RETURN g",
                            Map.of("matricula", matricula))
                    .single());

            if (record != null) {
                Map<String, Object> gerenteData = record.get("g").asMap();
                System.out.println("Dados do Gerente:");
                System.out.println("Matrícula: " + gerenteData.get("matricula"));
                System.out.println("Nome: " + gerenteData.get("nome"));
                System.out.println("CPF: " + gerenteData.get("cpf"));
            } else {
                System.out.println("Gerente não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao consultar os dados do gerente.");
        }
    }

    public void cadastrarGerente(int matricula, String nome, String cpf) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (u:Gerente {matricula: $matricula, nome: $nome, cpf: $cpf})",
                        Map.of("matricula", matricula, "nome", nome, "cpf", cpf));
                return null;
            });
            System.out.println("Gerente cadastrado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao cadastrar o Gerente");
        }
    }

    public void atualizarDadosGerente(int matricula, String novoNome, String novoCpf) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (g:Gerente {matricula: $matricula}) SET g.nome = $novoNome, g.cpf = $novoCpf",
                        Map.of("matricula", matricula, "novoNome", novoNome, "novoCpf", novoCpf));
                return null;
            });
            System.out.println("Dados do gerente atualizados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao atualizar os dados do gerente.");
        }
    }

    public void excluirGerente(int matricula) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (g:Gerente {matricula: $matricula}) DELETE g",
                        Map.of("matricula", matricula));
                return null;
            });
            System.out.println("Gerente excluído com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao excluir o gerente.");
        }
    }
}
