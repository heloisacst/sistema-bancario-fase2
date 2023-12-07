package connection;

import enums.TipoTransacao;
import model.Transacao;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.neo4j.driver.Values.parameters;

public class TransacaoConnection {

    private static final Neo4jConnectionManager neo4jConnectionManager = new Neo4jConnectionManager();

    public static Session getSession() {
        return neo4jConnectionManager.getDriver().session();
    }

    public static void close() {
        neo4jConnectionManager.close();
    }

    public void efetuaTransacao(LocalDateTime dth_transacao, double valor, String tipoTransacao, int contaOrigem, int contaDestino) {
        String transacaoId = UUID.randomUUID().toString();

        try (Session session = getSession()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (t:Transacao {id: $transacaoId, data_hora: $dth_transacao, valor_transacao: $valor, tipo_transacao: $tipoTransacao})",
                        parameters("transacaoId", transacaoId, "dth_transacao", dth_transacao.toString(), "valor", valor, "tipoTransacao", tipoTransacao));

                tx.run("MATCH (c:Conta {nroConta: $contaOrigem}), (t:Transacao {id: $transacaoId}) " +
                                "CREATE (c)-[:REALIZADA_POR]->(t)",
                        parameters("contaOrigem", contaOrigem, "transacaoId", transacaoId));

                return null;
            });
            System.out.println("Transação efetuada com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao efetuar a transação.");
        }
    }

    public List<Transacao> consultarExtrato(int nroConta) {
        List<Transacao> extrato = new ArrayList<>();

        try (Session session = getSession()) {
            List<Record> records = session.readTransaction(tx -> tx.run(
                            "MATCH (c:Conta {nroConta: $nroConta})-[:REALIZADA_POR]->(t:Transacao) RETURN t ORDER BY t.data_hora DESC",
                            Map.of("nroConta", nroConta))
                    .list());

            for (Record record : records) {
                Value valorTransacaoValue = record.get("t").get("valor_transacao");
                Value tipoTransacaoValue = record.get("t").get("tipo_transacao");
                Value dataHoraValue = record.get("t").get("data_hora");

                double valorTransacao = valorTransacaoValue.isNull() ? 0 : valorTransacaoValue.asDouble();
                TipoTransacao tipoTransacao = tipoTransacaoValue.isNull() ?
                        TipoTransacao.UNKNOWN : TipoTransacao.valueOf(tipoTransacaoValue.asString());
                LocalDateTime dataHora = null;
                if (!dataHoraValue.isNull()) {
                    try {
                        dataHora = LocalDateTime.parse(dataHoraValue.asString(), DateTimeFormatter.ISO_DATE_TIME);
                    } catch (DateTimeParseException e) {
                        System.out.println("Falha ao fazer parse da data e hora: " + dataHoraValue.asString());
                        e.printStackTrace();
                    }
                }

                Transacao transacao = new Transacao();
                transacao.setValor_transacao(valorTransacao);
                transacao.setTipoTransacao(tipoTransacao);
                transacao.setData_hora(dataHora);

                extrato.add(transacao);
            }
        }

        return extrato;
    }

}
