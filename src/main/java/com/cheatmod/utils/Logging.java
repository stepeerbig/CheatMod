package com.cheatmod.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logging {
    private static final Logger LOGGER = LogManager.getLogger("CheatMod");
    private static final String LOG_FILE = "logs/cheatmod.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void info(String message) {
        String formatted = "[INFO] " + message;
        LOGGER.info(message);
        writeToFile(formatted);
    }
    
    public static void warn(String message) {
        String formatted = "[WARN] " + message;
        LOGGER.warn(message);
        writeToFile(formatted);
    }
    
    public static void error(String message) {
        String formatted = "[ERROR] " + message;
        LOGGER.error(message);
        writeToFile(formatted);
    }
    
    public static void debug(String message) {
        String formatted = "[DEBUG] " + message;
        LOGGER.debug(message);
        writeToFile(formatted);
    }
    
    private static void writeToFile(String message) {
        try {
            File logFile = new File(LOG_FILE);
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(logFile, true)) {
                String timestamp = LocalDateTime.now().format(formatter);
                writer.write(timestamp + " " + message + "\n");
            }
        } catch (IOException e) {
            LOGGER.error("Ошибка записи в лог-файл: " + e.getMessage());
        }
    }
}