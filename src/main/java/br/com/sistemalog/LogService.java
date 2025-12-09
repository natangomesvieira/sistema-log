package br.com.sistemalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class LogService {

    private final String baseFileName;
    private final ObjectMapper objectMapper;
    private final LogConfiguration config = LogConfiguration.getInstance(); 

    public LogService(String baseFileName) {
        this.baseFileName = baseFileName;
        
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void setLogFormat(LogFormat format) {
        config.saveLogFormat(format);
    }

    public void log(LogEntry entry) {
        LogFormat currentFormat = config.loadLogFormat();
        String content;
        String fullFileName = baseFileName + currentFormat.getExtension();
        String header = null;

        switch (currentFormat) {
            case CSV:
                content = entry.toCsvLine();
                header = LogEntry.getCsvHeader(); 
                break;
            case JSONL:
                try {
                    content = objectMapper.writeValueAsString(entry);
                } catch (IOException e) {
                    throw new RuntimeException("Falha na serialização JSON do LogEntry: " + e.getMessage(), e);
                }
                break;
            default:
                throw new UnsupportedOperationException("Formato de log não suportado: " + currentFormat);
        }
        
        writeEntry(content, fullFileName, header);
    }
    
    private void writeEntry(String content, String fullFileName, String header) {
        File file = new File(fullFileName);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            
            if (header != null && (!file.exists() || file.length() == 0)) {
                writer.println(header);
            }
           
            writer.println(content);
            
        } catch (IOException e) {
            throw new RuntimeException("ERRO ao escrever log em " + fullFileName + ": " + e.getMessage(), e);
        }
    }
}