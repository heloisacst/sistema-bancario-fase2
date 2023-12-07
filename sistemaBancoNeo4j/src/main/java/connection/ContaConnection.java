package connection;

import enums.TipoConta;
import model.Conta;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import java.util.Map;

public class ContaConnection {


    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "12345678";



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

    public void cadastrarConta(Integer nro_conta, Integer agencia, String tipo_conta, LocalDateTime data_abertura, Double saldo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (u:Conta {nroConta: $nroConta, agencia: $agencia, tipoConta: $tipoConta, dataAbertura: $dataAbertura, saldo: $saldo})",
                        Map.of("nroConta", nro_conta, "agencia", agencia, "tipoConta", tipo_conta, "dataAbertura", data_abertura.format(formatter), "saldo", saldo));
                return null;
            });
            System.out.println("Conta cadastrada com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao cadastrar Conta");
        }
    }
    public void consultarConta(int nro_conta) {
        try (Session session = getSession()) {
            Record record = session.readTransaction(tx -> tx.run(
                            "MATCH (c:Conta {nroConta: $nroConta}) RETURN c",
                            Map.of("nroConta", nro_conta))
                    .single());

            if (record != null) {
                Map<String, Object> contaData = record.get("c").asMap();
                System.out.println("Dados da Conta:");
                System.out.println("Número da Conta: " + contaData.get("nroConta"));
                System.out.println("Agência: " + contaData.get("agencia"));
                System.out.println("Tipo de Conta: " + contaData.get("tipoConta"));

                Object dataAberturaObj = contaData.get("dataAbertura");
                if (dataAberturaObj instanceof String) {
                    String dataAberturaStr = (String) dataAberturaObj;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    LocalDateTime dataAbertura = LocalDateTime.parse(dataAberturaStr, formatter);
                    DateTimeFormatter meuFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String dataAberturaFormatada = meuFormatter.format(dataAbertura);
                    System.out.println("Data de Abertura: " + dataAberturaFormatada);
                } else {
                    System.out.println("Data de Abertura: Formato inválido");
                }

                System.out.println("Saldo: " + contaData.get("saldo"));
            } else {
                System.out.println("Conta não encontrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao consultar os dados da conta.");
        }
    }

    public void excluirConta(int nroConta) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (c:Conta {nroConta: $nroConta}) DETACH DELETE c",
                        Map.of("nroConta", nroConta));
                return null;
            });
            System.out.println("Conta excluída com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao excluir a Conta");
        }
    }

    public void atualizarConta(int nroConta, int novoNroConta, TipoConta novoTipoConta, LocalDateTime novaDataAbertura, double novoSaldo) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (c:Conta {nroConta: $nroConta}) SET c.nroConta = $novoNroConta, c.tipoConta = $novoTipoConta, c.dataAbertura = $novaDataAbertura, c.saldo = $novoSaldo",
                        Map.of("nroConta", nroConta, "novoNroConta", novoNroConta, "novoTipoConta", novoTipoConta.toString(), "novaDataAbertura", novaDataAbertura.toString(), "novoSaldo", novoSaldo));
                return null;
            });
            System.out.println("Dados da conta atualizados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao atualizar os dados da conta.");
        }
    }////// ta dando exceção: depois que atualiza uma conta, para consultar a conta atualizada.

}
