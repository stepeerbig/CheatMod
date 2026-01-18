package com.cheatmod;

import com.cheatmod.commands.TestCommand;
import com.cheatmod.config.ConfigManager;
import com.cheatmod.events.KeyInputHandler;
import com.cheatmod.telegram.TelegramManager;
// import  net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import org.spongepowered.asm.launch.MixinBootstrap;
// import org.spongepowered.asm.mixin.Mixins;

@Mod("cheatmod")
public class CheatMod {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "cheatmod";
    public static CheatMod INSTANCE;
    
    public static ConfigManager configManager;
    public static TelegramManager telegramManager;
    
    // Флаг для инициализации Mixin
    private static boolean mixinInitialized = false;
    
    public CheatMod() {
        INSTANCE = this;
        LOGGER.info("========================================");
        LOGGER.info("CheatMod начал инициализацию...");
        LOGGER.info("Версия: 1.0.0");
        LOGGER.info("Для Minecraft 1.16.5");
        LOGGER.info("========================================");
        
        // Инициализация Mixin (только один раз)
        initializeMixin();
        
        // Регистрация событий
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        
        LOGGER.info("Регистрация событий завершена");
        
        // Инициализация менеджеров
        initializeManagers();
        
        LOGGER.info("CheatMod успешно инициализирован!");
        LOGGER.info("========================================");
    }
    
    /**
     * Безопасная инициализация Mixin
     */
    private void initializeMixin() {
        try {
            if (!mixinInitialized) {
                LOGGER.info("Инициализация Mixin...");
                MixinBootstrap.init();
                Mixins.addConfiguration("cheatmod.mixin.json");
                mixinInitialized = true;
                LOGGER.info("Mixin успешно инициализирован");
            } else {
                LOGGER.debug("Mixin уже инициализирован");
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка инициализации Mixin: {}", e.getMessage());
            LOGGER.error("Продолжаем без Mixin...");
        }
    }
    
    /**
     * Инициализация всех менеджеров и конфигураций
     */
    private void initializeManagers() {
        try {
            LOGGER.info("Загрузка конфигураций...");
            configManager = new ConfigManager();
            
            // Пытаемся загрузить конфиг маркета
            if (!configManager.loadMarketConfig()) {
                LOGGER.warn("Не удалось загрузить конфиг Market. Будет использован пустой конфиг.");
            }
            
            // Пытаемся загрузить конфиг аукциона
            if (!configManager.loadAuctionConfig()) {
                LOGGER.info("Конфиг Auction не найден, это нормально если вы не используете аукцион.");
            }
            
            // Инициализация Telegram
            LOGGER.info("Инициализация Telegram менеджера...");
            telegramManager = new TelegramManager();
            
            if (telegramManager.isEnabled()) {
                LOGGER.info("Telegram менеджер включен");
                LOGGER.debug("Chat ID: {}", telegramManager.getChatId());
            } else {
                LOGGER.info("Telegram менеджер выключен в настройках");
            }
            
        } catch (Exception e) {
            LOGGER.error("Критическая ошибка при инициализации менеджеров: {}", e.getMessage());
            LOGGER.error("Stack trace:", e);
        }
    }
    
    /**
     * Настройка клиентской части
     */
    private void setupClient(final FMLClientSetupEvent event) {
        LOGGER.info("Настройка клиентской части...");
        
        try {
            // Регистрация горячих клавиш
            LOGGER.debug("Регистрация горячих клавиш...");
            KeyBinds.register();
            
            // Регистрация команд
            event.enqueueWork(() -> {
                LOGGER.debug("Регистрация команд...");
                try {
                    // ClientCommandHandler.instance.registerCommand(new TestCommand());
                    LOGGER.info("Команда /cheattest зарегистрирована");
                } catch (Exception e) {
                    LOGGER.error("Ошибка регистрации команд: {}", e.getMessage());
                }
            });
            
            LOGGER.info("Клиентская часть успешно настроена!");
            
        } catch (Exception e) {
            LOGGER.error("Ошибка при настройке клиентской части: {}", e.getMessage());
            LOGGER.error("Stack trace:", e);
        }
    }
    
    /**
     * Статический метод для получения экземпляра мода
     */
    public static CheatMod getInstance() {
        return INSTANCE;
    }
    
    /**
     * Метод для перезагрузки конфигураций (можно вызвать из GUI)
     */
    public static void reloadConfigs() {
        LOGGER.info("Перезагрузка конфигураций...");
        try {
            if (configManager != null) {
                configManager.loadMarketConfig();
                configManager.loadAuctionConfig();
                LOGGER.info("Конфигурации успешно перезагружены");
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при перезагрузке конфигураций: {}", e.getMessage());
        }
    }
    
    /**
     * Метод для проверки состояния мода
     */
    public static String getModStatus() {
        StringBuilder status = new StringBuilder();
        status.append("CheatMod v1.0.0\n");
        
        if (configManager != null) {
            status.append("Market items: ").append(configManager.getMarketConfig().getItems().size()).append("\n");
            status.append("Auction items: ").append(configManager.getAuctionConfig().getItems().size()).append("\n");
        }
        
        if (telegramManager != null) {
            status.append("Telegram: ").append(telegramManager.isEnabled() ? "Включен" : "Выключен").append("\n");
        }
        
        status.append("Mixin: ").append(mixinInitialized ? "Инициализирован" : "Не инициализирован");
        
        return status.toString();
    }
}
