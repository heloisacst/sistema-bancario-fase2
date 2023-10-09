package org.udesc.socket.e1;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class RegistroDeLogs {
    // Diretório onde os arquivos de log serão armazenados
    private static final String DIRETORIO_LOGS = "logs/";

    // Implementar o registro de log de clientes conectados
    public static void registrarLogClienteConectado(String nome, String enderecoIP) {
        try {
            criarDiretorioLogs();

            FileWriter arquivoLog = new FileWriter(DIRETORIO_LOGS + "log_clientes_conectados.txt", true);
            PrintWriter gravadorLog = new PrintWriter(new BufferedWriter(arquivoLog));
            gravadorLog.println("Cliente: " + nome + " | Endereço IP: " + enderecoIP + " | Data/Hora: " + getCurrentTimestamp());
            gravadorLog.close();
        } catch (IOException e) {
            System.err.println("Erro ao registrar log de cliente conectado: " + e.getMessage());
        }
    }

    // Implementar o registro de log de clientes desconectados
    public static void registrarLogClienteDesconectado(String nome, String enderecoIP) {
        try {
            criarDiretorioLogs();

            FileWriter arquivoLog = new FileWriter(DIRETORIO_LOGS + "log_clientes_desconectados.txt", true);
            PrintWriter gravadorLog = new PrintWriter(new BufferedWriter(arquivoLog));
            gravadorLog.println("Cliente: " + nome + " | Endereço IP: " + enderecoIP + " | Data/Hora: " + getCurrentTimestamp());
            gravadorLog.close();
        } catch (IOException e) {
            System.err.println("Erro ao registrar log de cliente desconectado: " + e.getMessage());
        }
    }

    // Implementar método para obter o timestamp atual
    private static String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date data = new Date();
        return dateFormat.format(data);
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