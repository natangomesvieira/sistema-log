package br.com.sistemalog;

import java.io.*;
import java.util.Properties;

public class LogConfiguration {
    private static final String CONFIG_FILE = "log_config.properties";
    private static final String FORMAT_KEY = "log.format";
    private static final LogFormat DEFAULT_FORMAT = LogFormat.CSV;
    
    private static LogConfiguration instance;

    public static synchronized LogConfiguration getInstance() {
        if (instance == null) {
            instance = new LogConfiguration();
        }
        return instance;
    }
    
    private LogConfiguration() {}

    public LogFormat loadLogFormat() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                props.load(input);
                String formatName = props.getProperty(FORMAT_KEY);
                if (formatName != null) {
                    try {
                        return LogFormat.valueOf(formatName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Formato de log inválido na configuração. Usando padrão.");
                    }
                }
            } catch (IOException ex) {
                System.err.println("Erro ao carregar configuração de log. Usando padrão: " + ex.getMessage());
            }
        }
        return DEFAULT_FORMAT;
    }

    public void saveLogFormat(LogFormat format) {
        Properties props = new Properties();
        props.setProperty(FORMAT_KEY, format.name());
        
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            props.store(output, "Configuração do Tipo de Log");
        } catch (IOException io) {
            System.err.println("Erro ao salvar configuração de log: " + io.getMessage());
        }
    }
}