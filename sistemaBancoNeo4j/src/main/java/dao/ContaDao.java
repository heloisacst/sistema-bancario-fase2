package dao;

import connection.ContaConnection;
import enums.TipoConta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ClientException;
import org.neo4j.driver.exceptions.NoSuchRecordException;

public class ContaDao {
    Scanner sc = new Scanner(System.in);
    ContaConnection contaCon = new ContaConnection();

    public void administrarConta(){
        System.out.println("***************************************************************");
        System.out.println("\nO que deseja fazer? (Digite o número da opção desejada)");
        System.out.println("(1) Consultar conta");
        System.out.println("(2) Cadastrar conta");
        System.out.println("(3) Atualizar conta");
        System.out.println("(4) Excluir conta");
        System.out.print("(0) VOLTAR tela anterior ");
        System.out.print("---> ");
        int op = sc.nextInt();

        switch (op) {
            case 1: consultarConta();
                break;
            case 2: cadastrarConta();
                break;
            case 3: atualizarConta();
                break;
            case 4: excluirConta();
                break;
            case 0: System.out.println("\n\t...\n");
                break;
            default:
                System.out.println("Opção inválida!");
                break;
        }
    }



    private void cadastrarConta() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        System.out.println("----CADASTRO DE CONTA----");
        System.out.print("Número da conta: ");
        Integer nro_conta = sc.nextInt();
        System.out.print("CPF do cliente: ");
        sc.nextLine();
        String cpfCliente = sc.nextLine();
        System.out.print("Tipo da Conta: ");
        String tipoConta = sc.nextLine();
        LocalDateTime dataAbertura = LocalDateTime.now();

        contaCon.cadastrarConta(nro_conta, 0001, tipoConta, dataAbertura, 0.0, cpfCliente);
        //associarContaCliente(cpfCliente, nro_conta);
    }

    /*public void associarContaCliente(String cpf, int nroConta) {
        try (Session session = contaCon.getSession()) {
            session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (p:Pessoa {cpf: $cpf}), (c:Conta {nroConta: $nroConta}) " +
                                "CREATE (p)-[:POSSUI_CONTA]->(c)",
                        Map.of("cpf", cpf, "nroConta", nroConta)
                );

                if (result.hasNext()) {
                    Record record = result.next();
                    System.out.println("Relacionamento criado com sucesso!");
                } else {
                    System.out.println("Não foi possível criar o relacionamento. Verifique se os nós existem.");
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
    }*/

    private void consultarConta() {
        System.out.print("Informe o número da conta que deseja consultar: ");
        int nro_conta = sc.nextInt();

        contaCon.consultarConta(nro_conta);
    }

    private void excluirConta() {
        System.out.print("Informe o número da conta que deseja excluir: ");
        int nro_conta = sc.nextInt();

        contaCon.excluirConta(nro_conta);
    }

    private void atualizarConta() {
        System.out.println("Informe o nº da conta que deseja atualizar");
        int nro_conta = sc.nextInt();

        try {
            System.out.print("Novo nº de conta: ");
            int novoNroConta = sc.nextInt();
            sc.nextLine();
            System.out.print("Novo tipo de conta (POUPANCA, CONTA_CORRENTE, CONTA_SALARIO): ");
            String novoTipoContaStr = sc.nextLine().toUpperCase().replaceAll("\\s+", "_");

            LocalDateTime novaDataAbertura = LocalDateTime.now();

            contaCon.atualizarConta(nro_conta, novoNroConta, TipoConta.valueOf(novoTipoContaStr), novaDataAbertura, 0.0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao atualizar conta!");
        }
    }

    public void atualizaSaldo(int nroConta, double novoSaldo) {
        try (Session session = contaCon.getSession()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (c:Conta {nroConta: $nroConta}) SET c.saldo = $novoSaldo",
                        Map.of("nroConta", nroConta, "novoSaldo", novoSaldo));
                return null;
            });
            System.out.println("Saldo atualizado com sucesso!");
        } catch (Exception e) {
            System.out.println("Falha ao atualizar o saldo da conta.");
            e.printStackTrace();
        }
    }


    public double retornaSaldo(Integer nroConta) {
        try (Session session = contaCon.getSession()) {
            Record record = session.readTransaction(tx -> tx.run(
                            "MATCH (c:Conta {nroConta: $nroConta}) RETURN c.saldo",
                            Map.of("nroConta", nroConta))
                    .single());

            Value saldoValue = record.get("c.saldo");
            return saldoValue != null ? saldoValue.asDouble() : 0.0;
        } catch (Exception e) {
            System.out.println("Falha ao obter o saldo da conta.");
            e.printStackTrace();
            return 0.0;
        }
    }

    public int retornaNroConta(String cpf) {
        try (Session session = contaCon.getSession()) {
            Record record = session.readTransaction(tx -> tx.run(
                            "MATCH (cliente:Cliente {cpf: $cpf})<-[:PERTENCE_A]-(conta:Conta) RETURN conta.nroConta",
                            Map.of("cpf", cpf))
                    .single());

            Value nroContaValue = record.get("conta.nroConta");
            return nroContaValue != null ? nroContaValue.asInt() : 0;
        } catch (NoSuchRecordException e) {
            System.out.println("Cliente com CPF " + cpf + " não está vinculado a uma Conta.");
            return 0;
        } catch (Exception e) {
            System.out.println("Falha ao obter o número da conta.");
            e.printStackTrace();
            return 0;
        }
    }

}
