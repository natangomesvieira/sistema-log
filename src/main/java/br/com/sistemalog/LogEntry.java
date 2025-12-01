package br.com.sistemalog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private final LocalDateTime timestamp;
    private final String operation;
    private final String userName;
    private final boolean isSuccess;
    private final String errorMessage;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Construtor para operações BEM-SUCEDIDAS. */
    public LogEntry(String operation, String userName) {
        this.timestamp = LocalDateTime.now();
        this.operation = operation;
        this.userName = userName;
        this.isSuccess = true;
        this.errorMessage = null;
    }

    /** Construtor para operações que resultaram em FALHA. */
    public LogEntry(String operation, String userName, String errorMessage) {
        this.timestamp = LocalDateTime.now();
        this.operation = operation;
        this.userName = userName;
        this.isSuccess = false;
        this.errorMessage = errorMessage;
    }

    // --- Getters (Jackson usa estes para o JSONL) ---
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getOperation() { return operation; }
    public String getUserName() { return userName; }
    public boolean isSuccess() { return isSuccess; }
    public String getErrorMessage() { return errorMessage; }
    
    // Este getter gera a mensagem formatada e é incluído no JSONL
    public String getCustomLogMessage() { return generateCustomMessage(); }

    // --- Lógica de Formatação de Mensagem ---

    private String generateCustomMessage() {
        String context = String.format("(%s, %s, %s)", 
            timestamp.format(DATE_FORMAT), 
            timestamp.format(TIME_FORMAT), 
            userName
        );

        if (isSuccess) {
            // Padrão: <OPERACAO>: <NOME>, (<DATA>, <HORA>, <USUARIO>)
            return String.format("%s: %s, %s", 
                operation, 
                userName, 
                context
            );
        } else {
            // Padrão: Ocorreu a falha <MENSAGEM> ao realizar a operação <OPERACAO> para o usuário <NOME>, (<DATA>, <HORA>, <USUARIO>).
            return String.format("Ocorreu a falha \"%s\" ao realizar a operação %s para o usuário %s, %s.",
                errorMessage,
                operation,
                userName,
                context
            );
        }
    }

    /** Gera a linha CSV usando ponto e vírgula (;) e campos estruturados. */
    public String toCsvLine() {
        String status = isSuccess ? "SUCESSO" : "FALHA";
        String error = errorMessage != null ? errorMessage.replace("\"", "\"\"") : "";
        String user = userName.replace("\"", "\"\"");

        return String.format("%s;%s;\"%s\";\"%s\";%s;\"%s\"",
            timestamp.format(DATE_FORMAT),
            timestamp.format(TIME_FORMAT),
            user,
            operation.replace("\"", "\"\""),
            status,
            error
        );
    }

    /** Gera o cabeçalho CSV. */
    public static String getCsvHeader() {
        return "Data;Hora;Usuario;Operacao;Status;Mensagem_Erro";
    }
}
