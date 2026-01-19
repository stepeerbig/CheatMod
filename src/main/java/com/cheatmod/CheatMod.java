package com.cheatmod;

import com.cheatmod.config.ConfigManager;
import com.cheatmod.events.KeyInputHandler;
import com.cheatmod.telegram.TelegramManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("cheatmod")
public class CheatMod {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "cheatmod";
    public static CheatMod INSTANCE;
    
    public static ConfigManager configManager;
    public static TelegramManager telegramManager;
    
    public CheatMod() {
        INSTANCE = this;
        LOGGER.info("========================================");
        LOGGER.info("CheatMod начал инициализацию...");
        LOGGER.info("Версия: 1.0.0");
        LOGGER.info("Для Minecraft 1.16.5");
        LOGGER.info("========================================");
        
        // Регистрация событий
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        
        LOGGER.info("Регистрация событий завершена");
        
        // Инициализация менеджеров
        initializeManagers();
        
        LOGGER.info("CheatMod успешно инициализирован!");
        LOGGER.info("========================================");
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Настройка общей части мода...");
    }
    
    private void initializeManagers() {
        try {
            LOGGER.info("Загрузка конфигураций...");
            configManager = new ConfigManager();
            
            if (!configManager.loadMarketConfig()) {
                LOGGER.warn("Не удалось загрузить конфиг Market. Будет использован пустой конфиг.");
            }
            
            if (!configManager.loadAuctionConfig()) {
                LOGGER.info("Конфиг Auction не найден.");
            }
            
            LOGGER.info("Инициализация Telegram менеджера...");
            telegramManager = new TelegramManager();
            
            if (telegramManager.isEnabled()) {
                LOGGER.info("Telegram менеджер включен");
            } else {
                LOGGER.info("Telegram менеджер выключен");
            }
            
        } catch (Exception e) {
            LOGGER.error("Критическая ошибка: {}", e.getMessage());
        }
    }
    
    private void setupClient(final FMLClientSetupEvent event) {
        LOGGER.info("Настройка клиентской части...");
        
        try {
            KeyBinds.register();
            LOGGER.info("Клиентская часть настроена!");
        } catch (Exception e) {
            LOGGER.error("Ошибка настройки клиента: {}", e.getMessage());
        }
    }
    
    public static CheatMod getInstance() {
        return INSTANCE;
    }
    
    public static void reloadConfigs() {
        LOGGER.info("Перезагрузка конфигураций...");
        if (configManager != null) {
            configManager.loadMarketConfig();
            configManager.loadAuctionConfig();
        }
    }
}