package org.udesc.socket.e1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Servidor {
    private static final int PORTA = 12222;
    private static List<ClienteConectado> clientesConectados = new ArrayList<>();
    private static Map<String, Socket> destinatarios = new HashMap<>();
    private static Map<String, String> enderecoIPParaNome = new HashMap<>();

    // Diretório onde os arquivos de log serão armazenados
    private static final String DIRETORIO_LOGS = "logs/";

    public static void main(String[] args) throws IOException {
        // Verifique e crie o diretório de logs se ele não existir
        criarDiretorioLogs();

        var servidor = new ServerSocket(PORTA);
        System.out.println("Servidor iniciado");

        while (true) {
            var socket = servidor.accept();
            System.out.println("Nova conexão estabelecida");

            // Receba o nome de usuário do cliente
            BufferedReader nomeReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String nome = nomeReader.readLine();

            // Se o nome de usuário for nulo ou vazio, tente recuperá-lo com base no endereço IP
            if (nome == null || nome.isEmpty()) {
                String enderecoIPCliente = socket.getInetAddress().getHostAddress();
                if (enderecoIPParaNome.containsKey(enderecoIPCliente)) {
                    nome = enderecoIPParaNome.get(enderecoIPCliente);
                    System.out.println("Nome de usuário recuperado a partir do endereço IP: " + nome);
                } else {
                    System.out.println("Nome de usuário não encontrado para o endereço IP: " + enderecoIPCliente);
                    // Trate o caso em que o nome de usuário não pode ser recuperado
                    // Encerre a conexão com o cliente ou tome medidas apropriadas
                    continue;
                }
            } else {
                // Se o nome de usuário não for nulo, adicione-o ao mapa de informações do cliente
                String enderecoIPCliente = socket.getInetAddress().getHostAddress();
                enderecoIPParaNome.put(enderecoIPCliente, nome);
            }

            System.out.println("Cliente conectado: " + nome);

            // Iniciar thread para lidar com o cliente conectado
            var cliente = new ClienteConectado(socket, nome);
            clientesConectados.add(cliente);
            var threadCliente = new Thread(cliente);
            threadCliente.start();

            // Adicionar o cliente ao mapa de destinatários
            destinatarios.put(cliente.nome, socket);

            // Registrar log de conexão do cliente
            RegistroDeLogs.registrarLogClienteConectado(cliente.nome, socket.getInetAddress().getHostAddress());
        }
    }

    private static class ClienteConectado implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String nome;

        public ClienteConectado(Socket socket, String nome) {
            this.socket = socket;
            this.nome = nome;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    String mensagem = in.readLine();
                    if (mensagem == null) {
                        break;
                    } else {
                        if (mensagem.equals("/users")) {
                            enviarListaUsuarios();
                        } else if (mensagem.startsWith("/send message")) {
                            enviarMensagem(mensagem);
                        } else if (mensagem.startsWith("/send file")) {
                            processarSolicitacaoArquivo(mensagem);
                        } else if (mensagem.startsWith("/request file")) {
                            encaminharSolicitacaoArquivo(mensagem);
                        } else if (mensagem.startsWith("/file accepted")) {
                            encaminharAceitacaoArquivo(mensagem);
                        } else {
                            System.out.println(">>> " + mensagem);
                        }
                    }
                }

                // Remover o cliente desconectado da lista
                clientesConectados.remove(this);
                destinatarios.remove(nome); // Remova o cliente dos destinatários

                // Registrar log de cliente desconectado
                RegistroDeLogs.registrarLogClienteDesconectado(nome, socket.getInetAddress().getHostAddress());

                socket.close();
            } catch (IOException e) {
                System.err.println("Erro ao receber dados: " + e.getMessage());
            }
        }

        private void enviarListaUsuarios() {
            out.println("Usuários conectados:");
            for (ClienteConectado cliente : clientesConectados) {
                if (!cliente.nome.equals(nome)) {
                    out.println(cliente.nome);
                }
            }
        }

        private void enviarMensagem(String mensagem) {
            for (ClienteConectado cliente : clientesConectados) {
                if (!cliente.nome.equals(nome)) {
                    cliente.out.println(nome + ": " + mensagem.substring("/send message ".length()));
                }
            }
        }

        private void processarSolicitacaoArquivo(String mensagem) {
            String[] parts = mensagem.split(" ");
            if (parts.length != 3) {
                System.out.println("Comando /send file deve seguir o formato: /send file [destinatário] [caminho do arquivo]");
                return;
            }

            String destinatario = parts[1];
            String caminhoArquivo = parts[2];

            File arquivo = new File(caminhoArquivo);
            if (!arquivo.exists() || !arquivo.isFile()) {
                System.out.println("Arquivo não encontrado: " + caminhoArquivo);
                return;
            }

            String nomeArquivo = arquivo.getName();
            long tamanhoArquivo = arquivo.length();

            // Verificar se o destinatário está online
            Socket destinatarioSocket = destinatarios.get(destinatario);
            if (destinatarioSocket == null) {
                System.out.println("Destinatário não encontrado ou não está online: " + destinatario);
                return;
            }

            // Solicitar aceitação do arquivo ao destinatário
            try (PrintWriter destinatarioOut = new PrintWriter(destinatarioSocket.getOutputStream(), true)) {
                destinatarioOut.println("/request file " + nome + " " + nomeArquivo + " " + tamanhoArquivo);
            } catch (IOException e) {
                System.err.println("Erro ao solicitar arquivo ao destinatário: " + e.getMessage());
            }
        }

        private void encaminharSolicitacaoArquivo(String mensagem) {
            for (ClienteConectado cliente : clientesConectados) {
                if (cliente.nome.equals(nome)) {
                    continue;
                }

                cliente.out.println(mensagem);
            }
        }

        private void encaminharAceitacaoArquivo(String mensagem) {
            String[] parts = mensagem.split(" ");
            if (parts.length != 4) {
                System.out.println("Comando /file accepted deve seguir o formato: /file accepted [remetente] [nome do arquivo] [tamanho]");
                return;
            }

            String remetente = parts[2];
            String nomeArquivo = parts[3];
            long tamanhoArquivo = Long.parseLong(parts[4]);

            for (ClienteConectado cliente : clientesConectados) {
                if (cliente.nome.equals(remetente)) {
                    cliente.out.println(mensagem);
                }
            }
        }
    }

    // Método para verificar e criar o diretório de logs, se necessário
    private static void criarDiretorioLogs() {
        File diretorioLogs = new File(DIRETORIO_LOGS);
        if (!diretorioLogs.exists()) {
            if (diretorioLogs.mkdirs()) {
                System.out.println("Diretório de logs criado: " + diretorioLogs.getAbsolutePath());
            } else {
                System.err.println("Erro ao criar o diretório de logs.");
            }
        }
    }
}


