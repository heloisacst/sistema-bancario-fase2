package dao;

import connection.GerenteConnection;
import model.Gerente;
import java.util.Scanner;

public class GerenteDao {
    Scanner sc = new Scanner(System.in);
    Gerente gerente = new Gerente();
    GerenteConnection connection = new GerenteConnection();

    public void administrarGerente(){
        System.out.println("***************************************************************");
        System.out.println("\nO que deseja fazer? (Digite o número da opção desejada)");
        System.out.println("(1) Consultar gerente");
        System.out.println("(2) Cadastrar um gerente");
        System.out.println("(3) Atualizar dados do gerente");
        System.out.println("(4) Excluir gerente");
        System.out.print("(0) VOLTAR tela anterior ");
        System.out.print("---> ");
        int op = sc.nextInt();

        switch (op) {
            case 1: consultarGerente();
                break;
            case 2: cadastrarGerente();
                break;
            case 3: atualizarGerente();
                break;
            case 4: excluirGerente();
                break;
            case 0: System.out.println("\n\t...\n");
                break;
            default:
                System.out.println("Opção inválida!");
                break;
        }
    }

    private void cadastrarGerente() {
        System.out.println("----CADASTRO DE GERENTE----");
        System.out.print("Informe o número da matrícula do gerente: ");
        int matricula = sc.nextInt();
        System.out.print("Informe o nome do gerente: ");
        sc.nextLine();
        String nome_gerente = sc.nextLine();
        System.out.print("Informe o CPF do gerente: ");
        String cpf = sc.nextLine();

        gerente.cadastrarGerente(matricula, nome_gerente, cpf);

        connection.cadastrarGerente(gerente.getMatricula(), gerente.getNome_gerente(), gerente.getCpf());
    }

    private void atualizarGerente() {
        System.out.println("----ATUALIZAÇÃO DE DADOS DE GERENTE----");
        System.out.print("Informe o número da matrícula do gerente: ");
        int matricula = sc.nextInt();
        sc.nextLine();
        System.out.print("Informe o nome do gerente: ");
        String novoNome = sc.nextLine();
        System.out.print("Informe o CPF do gerente: ");
        String novoCpf = sc.nextLine();

        connection.atualizarDadosGerente(matricula, novoNome, novoCpf);
    }

    private void consultarGerente() {
        System.out.print("Informe a matrícula do gerente que deseja consultar: ");
        int matricula = sc.nextInt();

        connection.consultarDadosGerente(matricula);
    }

    private void excluirGerente(){
        System.out.print("Informe a matrícula do gerente que deseja excluir: ");
        int matricula = sc.nextInt();

        connection.excluirGerente(matricula);
    }

}
