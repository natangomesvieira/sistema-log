package br.com.sistemalog;

public enum LogFormat {
    CSV(".csv"), 
    JSONL(".json_line"); 

    private final String extension;

    LogFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
