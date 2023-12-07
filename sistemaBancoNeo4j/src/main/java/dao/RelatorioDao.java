package dao;

import connection.ConnectionManager;
import connection.RelatorioConnection;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import java.util.List;
import java.util.Scanner;


public class RelatorioDao {
    Scanner sc = new Scanner(System.in);
    private final RelatorioConnection relatorioConnection;

    private static final ConnectionManager neo4jConnectionManager = new ConnectionManager();

    public static Session getSession() {
        return neo4jConnectionManager.getDriver().session();
    }

    public static void close() {
        neo4jConnectionManager.close();
    }

    public RelatorioDao(RelatorioConnection relatorioConnection) {
        this.relatorioConnection = relatorioConnection;
    }

    public void administrarRelatorio() {
        System.out.println("\nEscolha o relatório que deseja gerar");
        System.out.println("(1) Quantidade de Transações por Cliente");
        System.out.println("(2) Todas as Contas vinculadas aos clientes");
        System.out.print("--> ");
        int op = sc.nextInt();

        try (Session session = getSession()) {
            switch (op) {
                case 1:
                    consultaTransacoes(session);
                    break;
                case 2:
                    mostraContasClientes(session);
                    break;
                default:
                    System.out.println("Operação inválida!");
            }
        }
    }

    private void consultaTransacoes(Session session) {
        List<Record> records = session.readTransaction(tx ->
                tx.run("MATCH (cc:Conta)-[:PERTENCE_A]->(c:Cliente), (cc)-[:REALIZADA_POR]->(t:Transacao) RETURN c.nome AS nome, cc.nroConta AS nroConta, cc.tipoConta AS tipoConta, COUNT(t) AS quantidadeTransacoes ORDER BY quantidadeTransacoes DESC;")
                        .list());

        System.out.println("Relatório de Transações:");
        System.out.printf("%-20s %-15s %-20s %-15s\n", "Nome", "Nro Conta", "Tipo Conta", "Quantidade Transações");
        System.out.println("----------------------------------------------------------------------------------");

        for (Record record : records) {
            String nome = record.get("nome").asString();
            int nroConta = record.get("nroConta").asInt();
            String tipoConta = record.get("tipoConta").asString();
            long quantidadeTransacoes = record.get("quantidadeTransacoes").asLong();

            System.out.printf("%-20s %-15d %-20s %-15d\n", nome, nroConta, tipoConta, quantidadeTransacoes);
            System.out.println();
        }
    }

    private void mostraContasClientes(Session session) {
        List<Record> records = session.readTransaction(tx ->
                tx.run("MATCH (conta:Conta)-[:PERTENCE_A]->(c:Cliente) RETURN c.cpf AS cpf, c.nome AS nomeCliente, conta.nroConta AS nroConta, conta.tipoConta AS tipoConta;")
                        .list());

        System.out.println("Relatório de Contas por cliente:");
        System.out.printf("%-15s %-15s %-20s %-20s\n", "CPF", "Nome Cliente", "Nro Conta", "Tipo Conta");
        System.out.println("---------------------------------------------------------------------");

        for (Record record : records) {
            String cpf = record.get("cpf").asString();
            String nomeCliente = record.get("nomeCliente").asString();
            int nroConta = record.get("nroConta").asInt();
            String tipoConta = record.get("tipoConta").asString();

            System.out.printf("%-15s %-15d %-20s %-20s\n", cpf, nroConta, tipoConta, nomeCliente);
            System.out.println();
        }
    }
}
