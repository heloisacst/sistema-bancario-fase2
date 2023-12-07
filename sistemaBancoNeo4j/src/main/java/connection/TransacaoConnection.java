package connection;

import enums.TipoTransacao;
import model.Transacao;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class TransacaoConnection {
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

    public void efetuaTransacao(LocalDateTime dth_transacao, double valor, String tipoTransacao, int contaOrigem, int contaDestino) {
        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (t:Transacao {data_hora: $dth_transacao, valor_transacao: $valor, tipo_transacao: $tipoTransacao, nro_conta_origem: $contaOrigem, nro_conta_destino: $contaDestino})",
                        parameters("dth_transacao", dth_transacao.toString(), "valor", valor, "tipoTransacao", tipoTransacao, "contaOrigem", contaOrigem, "contaDestino", contaDestino));
                return null;
            });
            System.out.println("Transação efetuada com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao efetuar a transação.");
        }
    }

    public static Session getSession() {
        return driver.session();
    }

    public static void close() {
        driver.close();
    }

    public List<Transacao> consultarExtrato(int nroConta) {
        List<Transacao> extrato = new ArrayList<>();

        try (Session session = driver.session()) {
            List<Record> records = session.readTransaction(tx -> tx.run(
                            "MATCH (c:Conta {nroConta: $nroConta})-[:REALIZOU]->(t:Transacao) RETURN t ORDER BY t.dataHora DESC",
                            Map.of("nroConta", nroConta))
                    .list());

            for (Record record : records) {
                Transacao transacao = new Transacao();
                transacao.setValor_transacao(record.get("t").get("valorTransacao").asDouble());
                transacao.setTipoTransacao(TipoTransacao.valueOf(record.get("t").get("tipoTransacao").asString()));
                transacao.setData_hora(record.get("t").get("dataHora").asLocalDateTime());

                // Adicione outros atributos, se necessário

                extrato.add(transacao);
            }
        }

        return extrato;
    }
}
