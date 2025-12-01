package br.com.sistemalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogService {

    private final String baseFileName;
    private final ObjectMapper objectMapper;
    private static final String CSV_EXTENSION = ".csv";
    private static final String JSONL_EXTENSION = ".json_line";

    /** Inicializa o serviço de log para escrever nos dois formatos. */
    public LogService(String baseFileName) {
        this.baseFileName = baseFileName;
        
        // Configuração do Jackson para serializar objetos Java 8 Time (LocalDateTime)
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Garante que o cabeçalho CSV seja escrito na inicialização, se necessário.
        writeCsvHeaderIfNotExists();
    }

    /** Registra um LogEntry, escrevendo-o simultaneamente em CSV e JSONL. */
    public void log(LogEntry entry) {
        // 1. Escrever no CSV
        writeEntry(entry.toCsvLine(), baseFileName + CSV_EXTENSION, LogEntry.getCsvHeader());

        // 2. Escrever no JSONL
        try {
            // Jackson serializa a entrada em uma linha JSON
            String jsonOutput = objectMapper.writeValueAsString(entry);
            writeEntry(jsonOutput, baseFileName + JSONL_EXTENSION, null); // JSONL não tem cabeçalho
        } catch (IOException e) {
            System.err.println("ERRO de serialização JSON: " + e.getMessage());
        }
    }
    
    private void writeEntry(String content, String fullFileName, String header) {
        File file = new File(fullFileName);
        
        // Escreve o cabeçalho se o arquivo estiver vazio (apenas para CSV)
        if (header != null && (!file.exists() || file.length() == 0)) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                writer.println(header);
            } catch (IOException e) {
                 System.err.println("ERRO ao escrever cabeçalho: " + e.getMessage());
            }
        }
        
        // Anexa o conteúdo principal (garantindo o RNF de arquivo único)
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.println(content);
        } catch (IOException e) {
            System.err.println("ERRO ao escrever log em " + fullFileName + ": " + e.getMessage());
        }
    }
    
    private void writeCsvHeaderIfNotExists() {
        writeEntry("", baseFileName + CSV_EXTENSION, LogEntry.getCsvHeader());
    }
}
