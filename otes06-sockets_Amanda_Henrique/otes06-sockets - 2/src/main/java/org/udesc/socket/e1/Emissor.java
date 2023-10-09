package org.udesc.socket.e1;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Emissor implements Runnable {
  private Socket socket;
  private String nome;

  public Emissor(Socket socket, String nome) {
    this.socket = socket;
    this.nome = nome;
  }

  public void run() {
    try {
      var teclado = new Scanner(System.in);
      var saida = new PrintWriter(socket.getOutputStream(), true);

      while (true) {
        var texto = teclado.nextLine();

        if (texto.equals("/sair")) {
          break;
        } else if (texto.startsWith("/send message")) {
          // Solicitar destinatário antes de enviar a mensagem
          System.out.print("Digite o nome do destinatário: ");
          String destinatario = teclado.nextLine();
          if (destinatario.isEmpty()) {
            System.out.println("Nome do destinatário não pode estar vazio.");
            continue;
          }
          saida.println("/send message " + " " + texto.substring("/send message ".length()));
        } else if (texto.equals("/users")) {
          // Solicitar lista de usuários conectados
          saida.println(texto);
        } else if (texto.startsWith("/send file")) {
          // Enviar arquivo
          String[] parts = texto.split(" ");
          if (parts.length != 4) {
            System.out.println("Comando /send file deve seguir o formato: /send file [destinatário] [caminho do arquivo]");
            continue;
          }
          String destinatario = parts[2];
          String filePath = parts[3];

          // Solicitar aceitação do arquivo pelo destinatário
          System.out.print("Deseja enviar o arquivo para " + destinatario + "? [y/n]: ");
          String resposta = teclado.nextLine();

          if (resposta.equalsIgnoreCase("y")) {
            enviarArquivo(destinatario, filePath, saida);
          } else {
            System.out.println("Envio de arquivo cancelado.");
          }
        } else {
          System.out.println("Comando inválido. Use /send message, /send file, ou /users.");
        }
      }

      teclado.close();
      saida.close();
      socket.close();
    } catch (IOException e) {
      System.err.println("Erro ao enviar dados: " + e.getMessage());
    }
  }

  private void enviarArquivo(String destinatario, String filePath, PrintWriter saida) throws IOException {
    File arquivo = new File(filePath);
    if (!arquivo.exists()) {
      System.out.println("Arquivo não encontrado: " + filePath);
      return;
    }

    try (FileInputStream fileInputStream = new FileInputStream(arquivo)) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = fileInputStream.read(buffer)) != -1) {
        // Envie os bytes do arquivo para o servidor
        socket.getOutputStream().write(buffer, 0, bytesRead);
      }

      // Informa o servidor que o arquivo foi completamente enviado
      socket.getOutputStream().flush();
      saida.println("/send file " + destinatario + " " + arquivo.getName() + " " + arquivo.length()); // Comunica o nome do arquivo enviado
      System.out.println("Arquivo enviado com sucesso: " + arquivo.getName());
    }
  }
}
