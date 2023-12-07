package view;

import dao.ClienteDao;
import dao.GerenteDao;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int op1;
        do {
            System.out.println("O que deseja fazer? (Digite o número da opção desejada)");
            System.out.println("(1)- Administrar Gerentes");
            System.out.println("(2)- Administrar Clientes");
            System.out.println("(3)- Administrar Contas");
            System.out.println("(4)- Administrar Usuários");
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
                /*case 4:
                    usuarioDao.administrarUsuario();
                    break;
                case 5:
                    RelatoriosDao relatoriosDao = new RelatoriosDao();
                    relatoriosDao.gerarRelatorios();
                case 0: System.out.println("\nSaindo do Sistema...\n");
                    break;*/
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        }while( op1 != 0);
    }
}