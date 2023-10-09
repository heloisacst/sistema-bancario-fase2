package org.udesc.socket.e1;

import java.io.*;
import java.net.Socket;

public class Receptor implements Runnable {
  private Socket socket;

  public Receptor(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    try {
      var entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      while (true) {
        String mensagem = entrada.readLine();
        if (mensagem == null) {
          break;
        }

        if (mensagem.startsWith("/send file")) {
          receberArquivo(mensagem);
        } else {
          System.out.println(">>> " + mensagem);
        }
      }

      entrada.close();
      socket.close();
    } catch (IOException e) {
      System.err.println("Erro ao receber dados: " + e.getMessage());
    }
  }

  private void receberArquivo(String mensagem) throws IOException {
    String[] parts = mensagem.split(" ");
    if (parts.length != 5) {
      System.out.println("Comando /send file deve seguir o formato: /send file [remetente] [nome do arquivo] [tamanho]");
      return;
    }

    String remetente = parts[2];
    String nomeArquivo = parts[3];
    long tamanhoArquivo = Long.parseLong(parts[4]);

    // Solicitar ao usuário se deseja aceitar o arquivo
    System.out.println("Você recebeu um arquivo de " + remetente + ": " + nomeArquivo + " (" + tamanhoArquivo + " bytes)");
    System.out.print("Deseja aceitar o arquivo? (Sim ou Não): ");

    try {
      var teclado = new BufferedReader(new InputStreamReader(System.in));
      String resposta = teclado.readLine();

      if (resposta.equalsIgnoreCase("Sim")) {
        // Aceitar o arquivo e salvar em um local específico
        receberArquivo(nomeArquivo, tamanhoArquivo);
      } else {
        // Recusar o arquivo
        System.out.println("Arquivo recusado.");
      }
    } catch (IOException e) {
      System.err.println("Erro ao ler a resposta do usuário: " + e.getMessage());
    }
  }

  private void receberArquivo(String nomeArquivo, long tamanhoArquivo) {
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(nomeArquivo);
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

      byte[] buffer = new byte[1024];
      int bytesRead;
      long totalBytesRead = 0;

      System.out.println("Recebendo arquivo...");

      while (totalBytesRead < tamanhoArquivo && (bytesRead = socket.getInputStream().read(buffer)) != -1) {
        bufferedOutputStream.write(buffer, 0, bytesRead);
        totalBytesRead += bytesRead;

        // Exibir progresso (opcional)
        double percentComplete = (double) totalBytesRead / tamanhoArquivo * 100;
        System.out.printf("\rProgresso: %.2f%%", percentComplete);
      }

      bufferedOutputStream.flush();
      bufferedOutputStream.close();
      fileOutputStream.close();

      System.out.println("\nArquivo recebido e salvo com sucesso: " + nomeArquivo);
    } catch (IOException e) {
      System.err.println("Erro ao receber o arquivo: " + e.getMessage());
    }
  }
}

