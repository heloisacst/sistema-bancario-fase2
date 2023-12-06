package connection;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import java.util.Map;

public class ClienteConnection {

    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "udesc123";

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

    public void consultarDadosCliente(String cpf) {
        try (Session session = getSession()) {
            Record record = session.readTransaction(tx -> tx.run(
                            "MATCH (g:Cliente {cpf: $cpf}) RETURN g",
                            Map.of("cpf", cpf))
                    .single());

            if (record != null) {
                Map<String, Object> clienteData = record.get("g").asMap();
                System.out.println();
                System.out.println("Dados do Cliente:");
                System.out.println("CPF: " + clienteData.get("cpf"));
                System.out.println("Nome: " + clienteData.get("nome"));
                System.out.println("E-mail: " + clienteData.get("email"));
                System.out.println("Telefone: " + clienteData.get("telefone"));

            } else {
                System.out.println("Cliente não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao consultar os dados do cliente.");
        }
    }

    public void cadastrarCliente(String cpf, String nome, String telefone, String email) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (u:Cliente {cpf: $cpf, nome: $nome, telefone: $telefone, email: $email})",
                        Map.of("cpf", cpf, "nome", nome, "telefone", telefone, "email", email));
                return null;
            });
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao cadastrar o Cliente");
        }
    }

    public void atualizarDadosCliente(String cpf, String novoNome, String novoTelefone, String novoEmail) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (g:Cliente {cpf: $cpf}) SET g.nome = $novoNome, g.telefone = $novoTelefone, g.email = $novoEmail",
                        Map.of("cpf", cpf, "novoNome", novoNome, "novoTelefone", novoTelefone, "novoEmail", novoEmail));
                return null;
            });
            System.out.println("Dados do cliente atualizados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao atualizar os dados do cliente.");
        }
    }

    public void excluirCliente(String cpf) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (g:Cliente {cpf: $cpf}) DELETE g",
                        Map.of("cpf", cpf));
                return null;
            });
            System.out.println("Cliente excluído com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao excluir o cliente.");
        }
    }
}
