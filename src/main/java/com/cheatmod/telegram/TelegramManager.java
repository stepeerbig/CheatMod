package com.cheatmod.telegram;

import com.cheatmod.utils.Logging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramManager {
    private String botToken;
    private String chatId;
    private boolean enabled = false;
    
    private static final String CONFIG_FILE = "config/cheatmod/telegram.json";
    private static final Gson gson = new Gson();
    
    public TelegramManager() {
        loadConfig();
    }
    
    public void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            // Создаем дефолтный конфиг
            Logging.info("Файл конфигурации Telegram не найден, создаю новый");
            botToken = "7813109373:AAHrdMxnKylyBPZIEKGNfSq2B7ZhYw36tQE";
            chatId = "-5180834846";
            saveConfig();
            enabled = true;
            Logging.info("Конфигурация Telegram создана с дефолтными значениями");
            return;
        }
        
        try (Reader reader = new FileReader(configFile)) {
            JsonObject config = gson.fromJson(reader, JsonObject.class);
            botToken = config.get("botToken").getAsString();
            chatId = config.get("chatId").getAsString();
            enabled = config.get("enabled").getAsBoolean();
            Logging.info("Конфигурация Telegram загружена. enabled=" + enabled + ", botToken=" + 
                        (botToken != null ? "установлен" : "отсутствует") + ", chatId=" + chatId);
        } catch (Exception e) {
            Logging.error("Ошибка загрузки конфига Telegram: " + e.getMessage());
            enabled = false;
        }
    }
    
    public void saveConfig() {
        JsonObject config = new JsonObject();
        config.addProperty("botToken", botToken);
        config.addProperty("chatId", chatId);
        config.addProperty("enabled", enabled);
        
        try {
            File configDir = new File("config/cheatmod");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            try (Writer writer = new FileWriter(CONFIG_FILE)) {
                gson.toJson(config, writer);
                Logging.info("Конфигурация Telegram сохранена");
            }
        } catch (Exception e) {
            Logging.error("Ошибка сохранения конфига Telegram: " + e.getMessage());
        }
    }
    
    public boolean sendLink(String link) {
        if (!enabled) {
            Logging.warn("Telegram отключен в настройках");
            return false;
        }
        if (botToken == null || botToken.isEmpty()) {
            Logging.warn("Токен бота не установлен");
            return false;
        }
        if (chatId == null || chatId.isEmpty()) {
            Logging.warn("Chat ID не установлен");
            return false;
        }
        
        String message = "🛒 *Новая покупка на Market!*\n" +
                        "🔗 Ссылка: " + link + "\n" +
                        "⏰ Время: " + java.time.LocalTime.now();
        
        Logging.info("Отправляю сообщение в Telegram: " + message);
        return sendMessage(message);
    }
    
    public boolean sendMessage(String text) {
        try {
            String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("chat_id", chatId);
            requestBody.addProperty("text", text);
            requestBody.addProperty("parse_mode", "Markdown");
            
            String jsonInputString = gson.toJson(requestBody);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Logging.info("Сообщение отправлено в Telegram");
                return true;
            } else {
                Logging.error("Ошибка Telegram API: " + responseCode);
                // Читаем тело ошибки
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String line;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    Logging.error("Тело ошибки Telegram: " + errorResponse.toString());
                }
                return false;
            }
        } catch (Exception e) {
            Logging.error("Ошибка отправки в Telegram: " + e.getMessage());
            return false;
        }
    }
    
    // Геттеры и сеттеры
    public String getBotToken() {
        return botToken;
    }
    
    public void setBotToken(String botToken) {
        this.botToken = botToken;
        saveConfig();
    }
    
    public String getChatId() {
        return chatId;
    }
    
    public void setChatId(String chatId) {
        this.chatId = chatId;
        saveConfig();
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        saveConfig();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}