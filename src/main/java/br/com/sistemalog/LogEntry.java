package br.com.sistemalog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonPropertyOrder({ "timestamp", "operation", "userName", "authenticatedUser", "isSuccess", "errorMessage", "customLogMessage" })
public class LogEntry {
    private final LocalDateTime timestamp;
    private final String operation;
    private final String userName;
    private final String authenticatedUser;
    private final boolean isSuccess;
    private final String errorMessage;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public LogEntry(String operation, String userName, String authenticatedUser) {
        this.timestamp = LocalDateTime.now();
        this.operation = operation;
        this.userName = userName;
        this.authenticatedUser = authenticatedUser;
        this.isSuccess = true;
        this.errorMessage = null;
    }

    public LogEntry(String operation, String userName, String authenticatedUser, String errorMessage) {
        this.timestamp = LocalDateTime.now();
        this.operation = operation;
        this.userName = userName;
        this.authenticatedUser = authenticatedUser;
        this.isSuccess = false;
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getOperation() { return operation; }
    public String getUserName() { return userName; }
    public String getAuthenticatedUser() { return authenticatedUser; }
    public boolean isSuccess() { return isSuccess; }
    public String getErrorMessage() { return errorMessage; }
    public String getCustomLogMessage() { return generateCustomMessage(); }

    @JsonIgnore 
    private String generateCustomMessage() {
        String context = String.format("(%s, %s, %s)", 
            timestamp.format(DATE_FORMAT), 
            timestamp.format(TIME_FORMAT), 
            authenticatedUser
        );

        if (isSuccess) {
            return String.format("%s: %s, %s", 
                operation, 
                userName,
                context
            );
        } else {
           return String.format("Ocorreu a falha \"%s\" ao realizar a operação %s para o usuário %s, %s.",
                errorMessage,
                operation,
                userName,
                context
            );
        }
    }

    public String toCsvLine() {
        String status = isSuccess ? "SUCESSO" : "FALHA";
        String error = errorMessage != null ? errorMessage.replace("\"", "\"\"") : "";
        String user = userName.replace("\"", "\"\"");
        String authUser = authenticatedUser.replace("\"", "\"\"");

        return String.format("%s;%s;\"%s\";\"%s\";%s;\"%s\";\"%s\"",
            timestamp.format(DATE_FORMAT),
            timestamp.format(TIME_FORMAT),
            authUser,
            user,
            operation.replace("\"", "\"\""),
            status,
            error
        );
    }

    public static String getCsvHeader() {
        return "Data;Hora;Usuario_Executou;Usuario_Alvo;Operacao;Status;Mensagem_Erro";
    }
}