package dao;

import connection.ClienteConnection;
import model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ClienteDao {
    Scanner sc = new Scanner(System.in);
    public Cliente cliente = new Cliente();
    ClienteConnection connection = new ClienteConnection();

    public void administrarCliente(){
        System.out.println("***************************************************************");
        System.out.println("\nO que deseja fazer? (Digite o número da opção desejada)");
        System.out.println("(1) Consultar cliente");
        System.out.println("(2) Cadastrar um cliente");
        System.out.println("(3) Atualizar dados de cliente");
        System.out.println("(4) Excluir um cliente");
        System.out.print("---> ");
        int op = sc.nextInt();

        switch (op) {
           case 1: consultarCliente();
                break;
            case 2: cadastrarCliente();
                break;
            case 3: atualizarCliente();
                break;
            case 4: excluirCliente();
                break;
            default:
                System.out.println("Opção inválida!");
                break;
        }

    }

    private void cadastrarCliente(){
        System.out.println("----CADASTRO DE CLIENTE----");
        sc.nextLine();
        System.out.print("Número do CPF: ");
        String cpf = sc.nextLine();
        System.out.print("Nome completo: ");
        String nome = sc.nextLine();
        System.out.print("Telefone: ");
        String telefone = sc.nextLine();
        System.out.print("E-mail: ");
        String email = sc.nextLine();
        cpf = cpf.replaceAll("[^0-9]", "");

        cliente.cadastrarCliente(cpf, nome, telefone, email);
        connection.cadastrarCliente(cliente.getCpf(), cliente.getNome(), cliente.getTelefone(), cliente.getEmail());

    }

    private void atualizarCliente(){
        System.out.print("Informe o CPF do cliente que deseja atualizar: ");
        sc.nextLine();
        String cpf = sc.nextLine();
        System.out.print("Novo nome completo: ");
        String newNome = sc.nextLine();
        System.out.print("Novo telefone: ");
        String newTelefone = sc.nextLine();
        System.out.print("Novo e-mail: ");
        String newEmail = sc.nextLine();
        cpf = cpf.replaceAll("[^0-9]", "");

        cliente.cadastrarCliente(cpf, newNome, newTelefone, newEmail);
        connection.atualizarDadosCliente(cpf, newNome, newTelefone, newEmail);
    }

    public void consultarCliente() {
        sc.nextLine();
        System.out.println("Informe o CPF do cliente que deseja consultar");
        String cpf = sc.nextLine();
        connection.consultarDadosCliente(cpf);
    }


    private void excluirCliente(){
        System.out.println("Informe o CPF do cliente que deseja excluir");
        sc.nextLine();
        String cpf = sc.nextLine();

        try (Connection connection = conexao.getConnection()) {
            String sql = "SELECT * FROM cliente WHERE CPF = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, cpf);
            retorno = preparedStatement.executeQuery();

            if (retorno.next()) {
                String delete = "DELETE FROM cliente WHERE CPF = ?";
                preparedStatement = connection.prepareStatement(delete);
                preparedStatement.setString(1, cpf);
                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0){
                    System.out.println("Cliente excluído com sucesso!");
                } else {
                    System.out.println("Falha ao excluir cliente!");
                }
            } else {
                System.out.println("Cliente não encontrado!");
                excluirCliente();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*public String retornaTipoUsuario(String cpf) {
        String tipoUsuario = null;
        ResultSet retornoCliente = null;
        ResultSet retornoGerente = null;

        try (Connection connection = conexao.getConnection()) {
            String consultaCliente = "SELECT * FROM cliente WHERE CPF = ?";
            PreparedStatement queryCliente = connection.prepareStatement(consultaCliente);
            queryCliente.setString(1, cpf);
            retornoCliente = queryCliente.executeQuery();

            String consultaGerente = "SELECT * FROM gerente WHERE cpf_gerente = ?";
            PreparedStatement queryGerente = connection.prepareStatement(consultaGerente);
            queryGerente.setString(1, cpf);
            retornoGerente = queryGerente.executeQuery();

            if (retornoCliente.next()) {
                tipoUsuario = "CLIENTE";
            } else if (retornoGerente.next()) {
                tipoUsuario = "GERENTE";
            } else {
                System.out.println("CPF não encontrado na base de dados!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tipoUsuario;
    }*/
}
