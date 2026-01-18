package com.cheatmod.autobuy;

import com.cheatmod.CheatMod;
import com.cheatmod.config.ConfigManager;
import com.cheatmod.config.ItemConfig;
import com.cheatmod.telegram.TelegramManager;
import com.cheatmod.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MarketAutoBuy {
    private static final Logger LOGGER = LogManager.getLogger();
    private static MarketAutoBuy INSTANCE;
    
    private boolean enabled = false;
    private boolean paused = false;
    private State currentState = State.IDLE;
    private String status = "Ожидание";
    
    private ConfigManager configManager;
    private TelegramManager telegramManager;
    private MarketScanner scanner;
    
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    // Текущие данные
    private ItemStack currentTargetItem;
    private String currentLink;
    private int scanAttempts = 0;
    private int totalBought = 0;
    
    // Константы для слотов
    private static final int SORTING_SLOT = 47;
    private static final int NEW_FIRST_SLOT = 6;
    private static final int BACK_SLOT = 14;
    private static final int REFRESH_SLOT = 50;
    private static final int TO_PAYMENT_SLOT = 53;
    private static final int EXIT_BACK_SLOT = 49;
    
    public enum State {
        IDLE,
        OPENING_MARKET,
        WAITING_FOR_MARKET,
        SORTING,
        WAITING_FOR_SORT,
        SCANNING,
        CLICKING_ITEM,
        WAITING_FOR_BUY_SCREEN,
        CLICKING_BUY_BUTTON,
        WAITING_FOR_LINK_SCREEN,
        COPYING_LINK,
        SENDING_TELEGRAM,
        CLICKING_NO_BUTTON,
        CLICKING_DONE_BUTTON,
        UPDATING_PAGE,
        WAITING_FOR_UPDATE
    }
    
    private MarketAutoBuy() {
        this.configManager = CheatMod.configManager;
        this.telegramManager = CheatMod.telegramManager;
        this.scanner = new MarketScanner();
        
        // Запускаем основной цикл обработки
        scheduler.scheduleAtFixedRate(this::process, 0, 100, TimeUnit.MILLISECONDS);
    }
    
    public static MarketAutoBuy getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MarketAutoBuy();
        }
        return INSTANCE;
    }
    
    private void process() {
        if (!enabled) {
            Logging.debug("Autobuy выключен");
            return;
        }
        if (paused) {
            Logging.debug("Autobuy на паузе");
            return;
        }
        if (Minecraft.getInstance().player == null) {
            Logging.debug("Игрок не загружен");
            return;
        }
        
        try {
            Logging.debug("Обработка состояния: " + currentState);
            switch (currentState) {
                case IDLE:
                    handleIdle();
                    break;
                case OPENING_MARKET:
                    handleOpeningMarket();
                    break;
                case WAITING_FOR_MARKET:
                    handleWaitingForMarket();
                    break;
                case SORTING:
                    handleSorting();
                    break;
                case WAITING_FOR_SORT:
                    handleWaitingForSort();
                    break;
                case SCANNING:
                    handleScanning();
                    break;
                case CLICKING_ITEM:
                    handleClickingItem();
                    break;
                case WAITING_FOR_BUY_SCREEN:
                    handleWaitingForBuyScreen();
                    break;
                case CLICKING_BUY_BUTTON:
                    handleClickingBuyButton();
                    break;
                case WAITING_FOR_LINK_SCREEN:
                    handleWaitingForLinkScreen();
                    break;
                case COPYING_LINK:
                    handleCopyingLink();
                    break;
                case SENDING_TELEGRAM:
                    handleSendingTelegram();
                    break;
                case CLICKING_NO_BUTTON:
                    handleClickingNoButton();
                    break;
                case CLICKING_DONE_BUTTON:
                    handleClickingDoneButton();
                    break;
                case UPDATING_PAGE:
                    handleUpdatingPage();
                    break;
                case WAITING_FOR_UPDATE:
                    handleWaitingForUpdate();
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка в AutoBuy: ", e);
            status = "Ошибка: " + e.getMessage();
            DelayHelper.randomDelay(1000, 2000);
        }
    }
    
    private void handleIdle() {
        Logging.info("Начинаю работу...");
        status = "Открываю Market...";
        currentState = State.OPENING_MARKET;
    }
    
    private void handleOpeningMarket() {
        // Отправляем команду /mk
        if (Minecraft.getInstance().player != null) {
            Logging.info("Отправляю команду /mk");
            Minecraft.getInstance().player.chat("/mk");
            ChatHelper.addMessageToHistory("/mk");
        }
        status = "Отправляю /mk...";
        scanAttempts = 0;
        
        DelayHelper.randomDelay(1500, 2000); // Даем время на открытие GUI
        currentState = State.WAITING_FOR_MARKET;
    }
    
    private void handleWaitingForMarket() {
        Logging.debug("Ожидание открытия Market...");
        if (GuiHelper.isMarketMainScreen()) {
            Logging.info("Market успешно открыт");
            int page = GuiHelper.getMarketPageNumber();
            int total = GuiHelper.getMarketTotalPages();
            Logging.info("Страница: " + page + "/" + total);
            status = "Market открыт, начинаю сортировку...";
            DelayHelper.humanLikeDelay();
            currentState = State.SORTING;
        } else {
            // Ждем еще
            DelayHelper.randomDelay(300, 500);
        }
    }
    
    private void handleSorting() {
        Logging.info("Начинаю сортировку...");
        // Кликаем на слот 47 (Изменить тип сортировки)
        GuiHelper.clickGuiSlot(SORTING_SLOT);
        status = "Кликаю 'Изменить тип сортировки'...";
        
        DelayHelper.humanLikeDelay();
        currentState = State.WAITING_FOR_SORT;
    }
    
    private void handleWaitingForSort() {
        Logging.debug("Ожидание окна сортировки...");
        if (GuiHelper.isSortingScreen()) {
            Logging.info("Окно сортировки открыто");
            // Ждем немного перед кликом
            DelayHelper.thinkingDelay();
            
            // Кликаем на слот 6 (Сначала новые предметы)
            GuiHelper.clickGuiSlot(NEW_FIRST_SLOT);
            status = "Выбираю 'Сначала новые предметы'...";
            
            // Ждем сообщения в чате о успешной сортировке
            DelayHelper.randomDelay(800, 1200);
            
            // Проверяем чат на сообщение об успешной сортировке
            if (ChatHelper.hasSortingCompleteMessage()) {
                Logging.info("Сортировка успешно изменена");
            }
            
            // Кликаем на кнопку "Назад"
            GuiHelper.clickBackButton();
            status = "Возвращаюсь назад...";
            
            DelayHelper.humanLikeDelay();
            currentState = State.SCANNING;
        } else {
            Logging.debug("Окно сортировки еще не открыто");
            DelayHelper.randomDelay(100, 200);
        }
    }
    
    private void handleScanning() {
        Logging.info("Начинаю сканирование предметов...");
        status = "Сканирую предметы...";
        
        // Сканируем первые 4 слота (0, 1, 2, 3) ТОЛЬКО в GUI
        boolean foundItem = false;
        for (int slot = 0; slot < 4; slot++) {
            if (!GuiHelper.isGuiSlot(slot)) {
                Logging.debug("Слот " + slot + " не в GUI, пропускаю");
                continue;
            }
            
            ItemStack item = GuiHelper.getItemInGuiSlot(slot);
            if (item.isEmpty()) {
                Logging.debug("Слот GUI " + slot + " пуст");
                continue;
            }
            
            String itemName = item.getHoverName().getString();
            Logging.info("Сканирую слот GUI " + slot + ": " + itemName);
            
            // Проверяем предмет через сканер
            if (scanner.isItemValid(item, slot)) {
                currentTargetItem = item;
                Logging.info("✓ Нашел подходящий предмет: " + itemName);
                foundItem = true;
                currentState = State.CLICKING_ITEM;
                return;
            } else {
                Logging.debug("Предмет не подходит: " + itemName);
            }
        }
        
        // Если ничего не нашли, обновляем страницу
        scanAttempts++;
        Logging.debug("Сканирование завершено, попыток: " + scanAttempts);
        
        if (foundItem) {
            // Уже перешли в состояние CLICKING_ITEM
            return;
        }
        
        if (scanAttempts > 5) { // Максимум 5 попыток на странице
            Logging.info("Слишком много попыток, обновляю страницу...");
            status = "Слишком много попыток, обновляю страницу...";
            currentState = State.UPDATING_PAGE;
        } else {
            // Ждем немного и сканируем снова (возможно предметы обновились)
            Logging.debug("Ничего не нашел, жду и сканирую снова...");
            DelayHelper.randomDelay(1000, 1500);
            // Остаемся в состоянии SCANNING
        }
    }
    
    private void handleClickingItem() {
        int slot = scanner.getLastValidSlot();
        Logging.info("Кликаю на предмет в слоте " + slot);
        GuiHelper.clickGuiSlot(slot);
        status = "Кликаю на предмет...";
        
        DelayHelper.humanLikeDelay();
        currentState = State.WAITING_FOR_BUY_SCREEN;
    }
    
    private void handleWaitingForBuyScreen() {
        Logging.debug("Ожидание окна покупки...");
        if (GuiHelper.isBuyScreen()) {
            Logging.info("Окно покупки открыто");
            currentState = State.CLICKING_BUY_BUTTON;
        } else {
            DelayHelper.randomDelay(100, 200);
        }
    }
    
    private void handleClickingBuyButton() {
        // Ищем зеленую кнопку "КУПИТЬ"
        int buySlot = GuiHelper.findGreenBuyButton();
        if (buySlot != -1) {
            Logging.info("Нашел кнопку покупки в слоте " + buySlot + ", кликаю...");
            GuiHelper.clickGuiSlot(buySlot);
            status = "Кликаю 'КУПИТЬ'...";
            
            DelayHelper.randomDelay(500, 800);
            currentState = State.WAITING_FOR_LINK_SCREEN;
        } else {
            Logging.warn("Не нашел кнопку КУПИТЬ, возвращаюсь к сканированию...");
            status = "Не нашел кнопку КУПИТЬ, возвращаюсь...";
            currentState = State.SCANNING;
        }
    }
    
    private void handleWaitingForLinkScreen() {
        Logging.debug("Ожидание окна с ссылкой...");
        if (GuiHelper.isLinkScreen()) {
            Logging.info("Окно с ссылкой открыто");
            currentState = State.COPYING_LINK;
        } else {
            DelayHelper.randomDelay(100, 200);
        }
    }
    
    private void handleCopyingLink() {
        Logging.info("Пытаюсь извлечь ссылку...");
        // Копируем ссылку из GUI
        String link = GuiHelper.extractLinkFromGui();
        if (link != null && !link.isEmpty()) {
            currentLink = link;
            Logging.info("Ссылка скопирована: " + link);
            status = "Ссылка скопирована: " + link;
            currentState = State.SENDING_TELEGRAM;
        } else {
            Logging.warn("Не могу найти ссылку, пробую еще раз...");
            status = "Не могу найти ссылку, пробую еще раз...";
            DelayHelper.randomDelay(500, 800);
            // Кликаем "Копировать" если есть
            GuiHelper.clickCopyButton();
        }
    }
    
    private void handleSendingTelegram() {
        Logging.info("Отправляю ссылку в Telegram...");
        if (telegramManager.sendLink(currentLink)) {
            Logging.info("Ссылка успешно отправлена в Telegram");
            status = "Ссылка отправлена в Telegram";
            totalBought++;
            currentState = State.CLICKING_NO_BUTTON;
        } else {
            Logging.error("Ошибка отправки в Telegram");
            status = "Ошибка отправки в Telegram";
            // Продолжаем в любом случае
            currentState = State.CLICKING_NO_BUTTON;
        }
        DelayHelper.randomDelay(300, 500);
    }
    
    private void handleClickingNoButton() {
        Logging.info("Кликаю 'Нет'...");
        // Кликаем "Нет"
        GuiHelper.clickNoButton();
        status = "Кликаю 'Нет'...";
        
        DelayHelper.randomDelay(300, 500);
        currentState = State.CLICKING_DONE_BUTTON;
    }
    
    private void handleClickingDoneButton() {
        Logging.info("Кликаю 'Готово'...");
        // Кликаем "Готово"
        GuiHelper.clickDoneButton();
        status = "Кликаю 'Готово'...";
        
        DelayHelper.randomDelay(500, 800);
        // Возвращаемся к сканированию
        Logging.info("Возвращаюсь к сканированию...");
        scanAttempts = 0; // Сбрасываем счетчик попыток
        currentState = State.SCANNING;
    }
    
    private void handleUpdatingPage() {
        Logging.info("Обновляю страницу...");
        // Кликаем на слот 50 (Обновить страницу)
        GuiHelper.clickRefreshButton();
        status = "Обновляю страницу...";
        
        DelayHelper.randomDelay(300, 500);
        currentState = State.WAITING_FOR_UPDATE;
    }
    
    private void handleWaitingForUpdate() {
        Logging.debug("Ожидание обновления страницы...");
        // Ждем обновления страницы
        DelayHelper.randomDelay(800, 1200);
        scanAttempts = 0; // Сбрасываем счетчик попыток
        currentState = State.SCANNING;
    }
    
    // Геттеры и сеттеры
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            currentState = State.IDLE;
            status = "Выключено";
            Logging.info("Autobuy выключен");
        } else {
            status = "Запускаю...";
            currentState = State.OPENING_MARKET;
            Logging.info("Autobuy включен");
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setPaused(boolean paused) {
        this.paused = paused;
        status = paused ? "На паузе" : "Продолжаю";
        Logging.info("Autobuy " + (paused ? "приостановлен" : "возобновлен"));
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public String getStatus() {
        return status;
    }
    
    public int getTotalBought() {
        return totalBought;
    }
    
    public void shutdown() {
        scheduler.shutdown();
        Logging.info("Autobuy выключен");
    }
}