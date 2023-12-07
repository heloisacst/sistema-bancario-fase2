package connection;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ClientException;

import java.util.Map;

public class ClienteConnection {

    private static final Neo4jConnectionManager neo4jConnectionManager = new Neo4jConnectionManager();

    public static Session getSession() {
        return neo4jConnectionManager.getDriver().session();
    }

    public static void close() {
        neo4jConnectionManager.close();
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

    public void criarRelacaoContaCliente(String cpf, int nroConta) {
        try (Session session = getSession()) {
            String query = "MATCH (c:Cliente {cpf: $cpf}), (conta:Conta {nroConta: $nroConta}) " +
                    "CREATE (c)-[:POSSUI_CONTA]->(conta), (conta)-[:PERTENCE_AO_CLIENTE]->(c)";
            session.run(query, Values.parameters("cpf", cpf, "nroConta", nroConta));
        }
    }

    public void associarClienteConta(String cpf, int nroConta) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (p:Pessoa {cpf: $cpf}), (c:Conta {nroConta: $nroConta}) " +
                                "CREATE (p)-[:POSSUI_CONTA]->(c)",
                        Map.of("cpf", cpf, "nroConta", nroConta)
                );

                if (result.hasNext()) {
                    Record record = result.next();
                    System.out.println("Cliente associado à conta com sucesso!");
                } else {
                    System.out.println("Não foi possível associar o cliente à conta. Verifique se os nós existem.");
                }

                return null;
            });
        } catch (ClientException ce) {
            System.out.println("Erro no Neo4j: " + ce.getMessage());
            ce.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erro desconhecido: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
