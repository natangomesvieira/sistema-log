package br.com.sistemalog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonPropertyOrder({ "operation", "userName", "timestamp", "perfil", "isSuccess", "errorMessage", "customLogMessage" })
public class LogEntry {
    private final LocalDateTime timestamp;
    private final String operation;
    private final String userName;
    private final String perfil;
    private final boolean isSuccess;
    private final String errorMessage;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public LogEntry(String operation, String userName, String perfil) {
        this.timestamp = LocalDateTime.now();
        this.operation = checkNullOrEmpty(operation);
        this.userName = checkNullOrEmpty(userName);
        this.perfil = checkNullOrEmpty(perfil);
        this.isSuccess = true;
        this.errorMessage = null;
    }

    public LogEntry(String operation, String userName, String perfil, String errorMessage) {
        this.timestamp = LocalDateTime.now();
        this.operation = checkNullOrEmpty(operation);
        this.userName = checkNullOrEmpty(userName);
        this.perfil = checkNullOrEmpty(perfil);
        this.isSuccess = false;
        this.errorMessage = errorMessage;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getOperation() { return operation; }
    public String getUserName() { return userName; }
    public String getPerfil() { return perfil; }
    public boolean isSuccess() { return isSuccess; }
    public String getErrorMessage() { return errorMessage; }
    @JsonIgnore
    public String getCustomLogMessage() { return generateCustomMessage(); }

    @JsonIgnore 
    private String generateCustomMessage() {
        String context = String.format("(%s, %s, %s)", 
            timestamp.format(DATE_FORMAT), 
            timestamp.format(TIME_FORMAT), 
            perfil
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
        String authUser = perfil.replace("\"", "\"\"");

        return String.format("%s;%s;\"%s\";\"%s\";%s;\"%s\";\"%s\"",
            operation.replace("\"", "\"\""),
            user,
            timestamp.format(DATE_FORMAT),
            timestamp.format(TIME_FORMAT),
            authUser,  
            status,
            error
        );
    }

    public static String getCsvHeader() {
        return "Operacao;Usuario;Data;Hora;Perfil;Status;Msg_Erro";
    }
    
    private static String checkNullOrEmpty(String value) {
    if (value == null || value.trim().isEmpty()) {
        return "-";
    }
    return value;
}
}