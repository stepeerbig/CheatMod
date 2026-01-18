package com.cheatmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatHelper {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final List<String> chatHistory = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 100;
    
    // Добавление сообщения в историю
    public static void addMessageToHistory(String message) {
        if (message == null || message.trim().isEmpty()) return;
        
        chatHistory.add(0, message.trim());
        
        // Ограничиваем размер истории
        if (chatHistory.size() > MAX_HISTORY_SIZE) {
            chatHistory.remove(chatHistory.size() - 1);
        }
        
        Logging.debug("Добавлено в историю чата: " + message);
    }
    
    // Поиск в истории чата
    public static List<String> searchInHistory(String keyword) {
        List<String> results = new ArrayList<>();
        
        if (keyword == null || keyword.isEmpty()) return results;
        
        String lowerKeyword = keyword.toLowerCase();
        
        for (String message : chatHistory) {
            if (message.toLowerCase().contains(lowerKeyword)) {
                results.add(message);
            }
        }
        
        return results;
    }
    
    // Поиск ссылки в истории
    public static String findLinkInHistory() {
        Pattern urlPattern = Pattern.compile("https?://[\\w\\.\\-\\?=&/%#]+");
        
        for (String message : chatHistory) {
            Matcher matcher = urlPattern.matcher(message);
            if (matcher.find()) {
                String link = matcher.group();
                Logging.info("Найдена ссылка в истории чата: " + link);
                return link;
            }
        }
        
        return null;
    }
    
    // Проверка на сообщение о сортировке
    public static boolean hasSortingCompleteMessage() {
        for (String message : chatHistory) {
            if (message.contains("тип сортировки был успешно изменен") ||
                message.contains("сортировка установлена") ||
                message.contains("sorting changed")) {
                return true;
            }
        }
        
        return false;
    }
    
    // Проверка на сообщение о покупке
    public static boolean hasPurchaseMessage() {
        for (String message : chatHistory) {
            if (message.contains("успешно куплен") ||
                message.contains("покупка завершена") ||
                message.contains("successfully purchased")) {
                return true;
            }
        }
        
        return false;
    }
    
    // Очистка истории
    public static void clearHistory() {
        chatHistory.clear();
        Logging.info("История чата очищена");
    }
    
    // Отправка сообщения в чат
    public static void sendChatMessage(String message) {
        if (mc.player != null && message != null && !message.trim().isEmpty()) {
            mc.player.chat(message.trim());
            DelayHelper.randomDelay(300, 500);
        }
    }
    
    // Получение последних N сообщений
    public static List<String> getRecentMessages(int count) {
        if (count <= 0) return new ArrayList<>();
        
        int actualCount = Math.min(count, chatHistory.size());
        return new ArrayList<>(chatHistory.subList(0, actualCount));
    }
    
    // Проверка на наличие сообщения с ценой
    public static int extractPriceFromRecentMessages() {
        Pattern pricePattern = Pattern.compile("(\\d{1,3}(?:[,\\s]?\\d{3})*(?:\\.\\d+)?)\\s*(?:coin|монет|\\$|₽|€)", Pattern.CASE_INSENSITIVE);
        
        for (String message : chatHistory) {
            Matcher matcher = pricePattern.matcher(message);
            if (matcher.find()) {
                try {
                    String priceStr = matcher.group(1).replaceAll("[,\\s]", "");
                    return Integer.parseInt(priceStr);
                } catch (NumberFormatException e) {
                    // Пропускаем некорректные числа
                }
            }
        }
        
        return 0;
    }
}