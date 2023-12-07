package dao;

import connection.TransacaoConnection;
import enums.TipoTransacao;
import model.Transacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class TransacaoDao {
    Scanner sc = new Scanner(System.in);
    Transacao transacao = new Transacao();
    ContaDao contaDao = new ContaDao();
    TransacaoConnection transacaoConnection = new TransacaoConnection();

    public void escolherTransacao() {
        System.out.println("Informe qual operação será realizada: \n");
        System.out.println("(1)- Realizar depósito");
        System.out.println("(2)- Realizar saque");
        System.out.println("(3)- Realizar transferência");
        System.out.println("(4)- Consultar Saldo");
        System.out.println("(5)- Extrato da Conta");
        System.out.print("--> ");
        int op1 = sc.nextInt();

        efetuarTransacao(op1);
    }

    public void efetuarTransacao(int opcaoTransacao) {
        switch (opcaoTransacao) {
            case 1:
                efetuarDeposito();
                break;
            case 2:
                efetuarSaque();
                break;
            case 3:
                efetuarTransferencia();
                break;
            case 4:
                consultarSaldo();
                break;
            case 5:
                gerarExtrato();
                break;
            default:
                System.out.println("Opção inválida!");
                break;
        }
    }

    private void efetuarDeposito() {
        System.out.print("Informe o valor do depósito: R$ ");
        double valorDeposito = sc.nextDouble();
        sc.nextLine(); // Consumir a quebra de linha pendente

        System.out.print("Informe o CPF: ");
        String cpf = sc.nextLine();

        LocalDateTime dth_transacao = LocalDateTime.now();
        String tipoTransacao = TipoTransacao.DEPOSITO.toString();

        transacao.cadastrarTransacao(dth_transacao, valorDeposito, TipoTransacao.DEPOSITO);
        int contaTransacao = contaDao.retornaNroConta(cpf);

        efetivaTransacao(dth_transacao, tipoTransacao, contaTransacao, 0);

        double saldoAtual = contaDao.retornaSaldo(cpf);
        saldoAtual += valorDeposito;
        contaDao.atualizaSaldo(contaTransacao, saldoAtual);

        System.out.println("Depósito realizado com sucesso!");
    }

    private void efetuarSaque() {
        System.out.print("Informe o valor do saque: R$ ");
        double valorSaque = sc.nextDouble();
        sc.nextLine(); // Consumir a quebra de linha pendente

        System.out.print("Informe o CPF: ");
        String cpf = sc.nextLine();

        LocalDateTime dth_transacao = LocalDateTime.now();
        String tipoTransacao = TipoTransacao.SAQUE.toString();

        transacao.cadastrarTransacao(dth_transacao, valorSaque, TipoTransacao.SAQUE);
        int contaTransacao = contaDao.retornaNroConta(cpf);

        double saldoAtual = contaDao.retornaSaldo(cpf);
        saldoAtual -= valorSaque;

        if (saldoAtual < 0) {
            System.out.println("Saldo insuficiente para realizar o saque.");
            return;
        }

        contaDao.atualizaSaldo(contaTransacao, saldoAtual);
        efetivaTransacao(dth_transacao, tipoTransacao, contaTransacao, 0);

        System.out.println("Saque realizado com sucesso!");
    }

    private void efetuarTransferencia() {
        System.out.print("Informe o valor da transferência: R$ ");
        double valorTransferencia = sc.nextDouble();
        sc.nextLine(); // Consumir a quebra de linha pendente

        System.out.print("Informe o CPF: ");
        String cpf = sc.nextLine();
        int contaTransacao = contaDao.retornaNroConta(cpf);

        System.out.print("Informe a conta de destino: ");
        int contaDestino = sc.nextInt();

        LocalDateTime dth_transacao = LocalDateTime.now();
        String tipoTransacao = TipoTransacao.TRANSFERENCIA.toString();

        transacao.cadastrarTransacao(dth_transacao, valorTransferencia, TipoTransacao.TRANSFERENCIA);

        double saldoAtual = contaDao.retornaSaldo(cpf);
        saldoAtual -= valorTransferencia;

        if (saldoAtual < 0) {
            System.out.println("Saldo insuficiente para realizar a transferência.");
            return;
        }

        contaDao.atualizaSaldo(contaTransacao, saldoAtual);
        efetivaTransacao(dth_transacao, tipoTransacao, contaTransacao, contaDestino);

        System.out.println("Transferência realizada com sucesso!");
    }

    private void consultarSaldo() {
        System.out.print("Informe o CPF: ");
        String cpf = sc.nextLine();
        int contaTransacao = contaDao.retornaNroConta(cpf);

        double saldoAtual = contaDao.retornaSaldo(cpf);
        System.out.println("Saldo atual da conta: R$ " + saldoAtual);
    }

    private void gerarExtrato() {
        System.out.print("Informe o CPF: ");
        String cpf = sc.nextLine();
        int contaTransacao = contaDao.retornaNroConta(cpf);

        List<Transacao> extrato = transacaoConnection.consultarExtrato(contaTransacao);

        System.out.println("Extrato da conta:\n");

        for (Transacao transacao : extrato) {
            System.out.println(transacao);
        }
    }

    private void efetivaTransacao(LocalDateTime dth_transacao, String tipoTransacao, int contaTransacao, int contaDestino) {
        transacaoConnection.efetuaTransacao(dth_transacao, transacao.getValor_transacao(), tipoTransacao, contaTransacao, contaDestino);
    }
}
