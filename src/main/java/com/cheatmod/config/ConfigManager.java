package com.cheatmod.config;

import com.cheatmod.utils.Logging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_DIR = "config/cheatmod/";
    private static final String MARKET_CONFIG = CONFIG_DIR + "market.json";
    private static final String AUCTION_CONFIG = CONFIG_DIR + "auction.json";
    
    private ConfigContainer marketConfig;
    private ConfigContainer auctionConfig;
    private Map<String, ItemConfig> marketItems = new HashMap<>();
    private Map<String, ItemConfig> auctionItems = new HashMap<>();
    
    public ConfigManager() {
        loadMarketConfig();
        loadAuctionConfig();
    }
    
    public boolean loadMarketConfig() {
        try {
            File file = new File(MARKET_CONFIG);
            if (!file.exists()) {
                Logging.warn("Файл market.json не найден. Создаю пустой конфиг.");
                marketConfig = new ConfigContainer();
                saveMarketConfig();
                return false;
            }
            
            try (FileReader reader = new FileReader(file)) {
                marketConfig = gson.fromJson(reader, ConfigContainer.class);
                if (marketConfig == null) {
                    marketConfig = new ConfigContainer();
                }
                
                // Индексируем предметы для быстрого поиска
                marketItems.clear();
                for (ItemConfig item : marketConfig.getItems()) {
                    marketItems.put(item.getDisplayName().toLowerCase(), item);
                }
                
                Logging.info("Загружено " + marketItems.size() + " предметов для Market");
                // Выведем в лог первые несколько предметов для проверки
                int count = 0;
                for (String name : marketItems.keySet()) {
                    Logging.debug("Загружен предмет: " + name);
                    if (count++ > 5) break; // Выводим только первые 6 предметов
                }
                return true;
            }
        } catch (Exception e) {
            Logging.error("Ошибка загрузки market.json: " + e.getMessage());
            marketConfig = new ConfigContainer();
            return false;
        }
    }
    
    public boolean loadAuctionConfig() {
        try {
            File file = new File(AUCTION_CONFIG);
            if (!file.exists()) {
                Logging.info("Файл auction.json не найден. Создаю пустой конфиг.");
                auctionConfig = new ConfigContainer();
                saveAuctionConfig();
                return false;
            }
            
            try (FileReader reader = new FileReader(file)) {
                auctionConfig = gson.fromJson(reader, ConfigContainer.class);
                if (auctionConfig == null) {
                    auctionConfig = new ConfigContainer();
                }
                
                // Индексируем предметы для быстрого поиска
                auctionItems.clear();
                for (ItemConfig item : auctionConfig.getItems()) {
                    auctionItems.put(item.getDisplayName().toLowerCase(), item);
                }
                
                Logging.info("Загружено " + auctionItems.size() + " предметов для Auction");
                return true;
            }
        } catch (Exception e) {
            Logging.error("Ошибка загрузки auction.json: " + e.getMessage());
            auctionConfig = new ConfigContainer();
            return false;
        }
    }
    
    public void saveMarketConfig() {
        try {
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(MARKET_CONFIG)) {
                gson.toJson(marketConfig, writer);
                Logging.info("Конфиг Market сохранен");
            }
        } catch (IOException e) {
            Logging.error("Ошибка сохранения market.json: " + e.getMessage());
        }
    }
    
    public void saveAuctionConfig() {
        try {
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(AUCTION_CONFIG)) {
                gson.toJson(auctionConfig, writer);
                Logging.info("Конфиг Auction сохранен");
            }
        } catch (IOException e) {
            Logging.error("Ошибка сохранения auction.json: " + e.getMessage());
        }
    }
    
    public ItemConfig getMarketItem(String displayName) {
        return marketItems.get(displayName.toLowerCase());
    }
    
    public ItemConfig getAuctionItem(String displayName) {
        return auctionItems.get(displayName.toLowerCase());
    }
    
    public boolean isItemInMarket(String displayName) {
        return marketItems.containsKey(displayName.toLowerCase());
    }
    
    public boolean isItemInAuction(String displayName) {
        return auctionItems.containsKey(displayName.toLowerCase());
    }
    
    public ConfigContainer getMarketConfig() {
        return marketConfig;
    }
    
    public ConfigContainer getAuctionConfig() {
        return auctionConfig;
    }
    
    public void addMarketItem(ItemConfig item) {
        marketConfig.addItem(item);
        marketItems.put(item.getDisplayName().toLowerCase(), item);
        saveMarketConfig();
    }
    
    public void addAuctionItem(ItemConfig item) {
        auctionConfig.addItem(item);
        auctionItems.put(item.getDisplayName().toLowerCase(), item);
        saveAuctionConfig();
    }
}