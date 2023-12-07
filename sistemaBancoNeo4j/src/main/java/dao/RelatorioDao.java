package dao;

import connection.RelatorioConnection;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.util.List;
import java.util.Scanner;


public class RelatorioDao {
    Scanner sc = new Scanner(System.in);
    private final RelatorioConnection relatorioConnection;

    public RelatorioDao(RelatorioConnection relatorioConnection) {
        this.relatorioConnection = relatorioConnection;
    }

    public void administrarRelatorio() {
        System.out.println("\nEscolha o relatório que deseja gerar");
        System.out.println("(1) Quantidade de Transações por Cliente");
        System.out.println("(2) Todas as Contas vinculadas aos clientes");
        System.out.print("--> ");
        int op = sc.nextInt();

        try (Session session = relatorioConnection.getDriver().session()) {
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
                tx.run("MATCH (c:Cliente)-[:REALIZOU]->(t:Transacao)<-[:ORIGEM]-(cc:Conta) " +
                                "RETURN c.nome AS nome, cc.nro_conta AS nroConta, cc.tipo_conta AS tipoConta, COUNT(t) AS quantidadeTransacoes " +
                                "ORDER BY quantidadeTransacoes DESC")
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
        }
    }

    private void mostraContasClientes(Session session) {
        List<Record> records = session.readTransaction(tx ->
                tx.run("MATCH (c:Cliente)-[:POSSUI]->(cc:Conta) " +
                                "RETURN c.cpf AS cpf, cc.nro_conta AS nroConta, cc.tipo_conta AS tipoConta, c.nome AS nomeCliente " +
                                "ORDER BY c.cpf")
                        .list());

        System.out.println("Relatório de Contas por cliente:");
        System.out.printf("%-15s %-15s %-20s %-20s\n", "CPF", "Nro Conta", "Tipo Conta", "Cliente");
        System.out.println("---------------------------------------------------------------------");

        for (Record record : records) {
            String cpf = record.get("cpf").asString();
            int nroConta = record.get("nroConta").asInt();
            String tipoConta = record.get("tipoConta").asString();
            String nomeCliente = record.get("nomeCliente").asString();

            System.out.printf("%-15s %-15d %-20s %-20s\n", cpf, nroConta, tipoConta, nomeCliente);
        }
    }
}
