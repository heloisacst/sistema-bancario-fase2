import connection.RelatorioConnection;
import dao.ClienteDao;
import dao.ContaDao;
import dao.GerenteDao;
import dao.RelatorioDao;
import dao.TransacaoDao;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RelatorioConnection relatorioConnection = new RelatorioConnection();
        int op1;
        do {
            System.out.println("O que deseja fazer? (Digite o número da opção desejada)");
            System.out.println("(1)- Administrar Gerentes");
            System.out.println("(2)- Administrar Clientes");
            System.out.println("(3)- Administrar Contas");
            System.out.println("(4)- Realizar Transação");
            System.out.println("(5)- Gerar Relatórios");
            System.out.println("(0)- Sair sistema");

            System.out.print("--> ");
            op1 = sc.nextInt();


            switch (op1) {
                case 1:
                    GerenteDao administrarGerente = new GerenteDao();
                    administrarGerente.administrarGerente();
                    break;
                case 2:
                    ClienteDao administrarCliente = new ClienteDao();
                    administrarCliente.administrarCliente();
                    break;
                case 3:
                    ContaDao administrarConta = new ContaDao();
                    administrarConta.administrarConta();
                    break;
                case 4:
                    TransacaoDao transacaoDao = new TransacaoDao();
                    transacaoDao.escolherTransacao();
                    break;
                case 5:
                    RelatorioDao relatoriosDao = new RelatorioDao(relatorioConnection);
                    relatoriosDao.administrarRelatorio();
                    break;
                case 0: System.out.println("\nSaindo do Sistema...\n");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        }while( op1 != 0);
    }
}
