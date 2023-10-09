package org.udesc.socket.e1;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite seu nome de usuário: ");
        String nome = scanner.nextLine();

        var socket = new Socket("25.46.141.209", 12222);
        System.out.println("Conexão estabelecida com o servidor");

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(nome); // Envia o nome de usuário para o servidor

        new Thread(new Receptor(socket)).start();
        new Thread(new Emissor(socket, nome)).start();
    }
}