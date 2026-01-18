package com.cheatmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiHelper {
    private static final Minecraft mc = Minecraft.getInstance();
    
    // Определение окон БЕЗ учета инвентаря игрока
    public static boolean isMarketMainScreen() {
        if (mc.screen == null) return false;
        String title = mc.screen.getTitle().getString();
        Logging.debug("Проверка окна рынка: заголовок = " + title);
        // Проверяем по шаблону: "Маркет за коины (1/139)"
        boolean isMarket = title.startsWith("Маркет за коины (");
        return isMarket;
    }
    
    public static boolean isSortingScreen() {
        if (mc.screen == null) return false;
        String title = mc.screen.getTitle().getString();
        Logging.debug("Проверка окна сортировки: заголовок = " + title);
        return title.equals("Изменить тип сортировки");
    }
    
    public static boolean isBuyScreen() {
        if (mc.screen == null) return false;
        String title = mc.screen.getTitle().getString();
        Logging.debug("Проверка окна покупки: заголовок = " + title);
        return title.contains("Купить") || 
               title.contains("КУПИТЬ") ||
               title.contains("Buy");
    }
    
    public static boolean isLinkScreen() {
        if (mc.screen == null) return false;
        String title = mc.screen.getTitle().getString();
        Logging.debug("Проверка окна с ссылкой: заголовок = " + title);
        return title.contains("Перейти по ссылке") || 
               title.contains("Вы действительно хотите перейти по ссылке") ||
               title.contains("перейти по ссылке");
    }
    
    public static boolean isShulkerScreen() {
        if (mc.screen == null) return false;
        String title = mc.screen.getTitle().getString();
        Logging.debug("Проверка окна шалкера: заголовок = " + title);
        return title.startsWith("Инвентарь (") && title.contains(")");
    }
    
    // Получение цены из заголовка шалкера
    public static int getShulkerPrice() {
        if (!isShulkerScreen()) return 0;
        
        String title = mc.screen.getTitle().getString();
        Pattern pattern = Pattern.compile("Инвентарь \\((\\d+)\\)");
        Matcher matcher = pattern.matcher(title);
        
        if (matcher.find()) {
            try {
                String priceStr = matcher.group(1);
                return Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Logging.error("Не могу распарсить цену шалкера: " + title);
            }
        }
        return 0;
    }
    
    // Получаем количество страниц маркета из заголовка
    public static int getMarketPageNumber() {
        if (!isMarketMainScreen()) return 0;
        
        String title = mc.screen.getTitle().getString();
        Pattern pattern = Pattern.compile("Маркет за коины \\((\\d+)/(\\d+)\\)");
        Matcher matcher = pattern.matcher(title);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    public static int getMarketTotalPages() {
        if (!isMarketMainScreen()) return 0;
        
        String title = mc.screen.getTitle().getString();
        Pattern pattern = Pattern.compile("Маркет за коины \\((\\d+)/(\\d+)\\)");
        Matcher matcher = pattern.matcher(title);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    // Получение количества слотов GUI (без инвентаря)
    public static int getGuiSlotsCount() {
        if (mc.screen == null || mc.player == null || mc.player.containerMenu == null) 
            return 0;
        
        if (isMarketMainScreen()) return 54;
        if (isSortingScreen()) return 18; // 2x9
        if (isShulkerScreen()) return 54; // шалкер всегда 54 слота
        if (isBuyScreen()) {
            // Проверяем размер окна покупки
            int containerSize = mc.player.containerMenu.slots.size();
            // Если это окно покупки шалкера или обычного предмета
            return (containerSize >= 54) ? 54 : 27;
        }
        
        return mc.player.containerMenu.slots.size();
    }
    
    // Проверка, находится ли слот в GUI (а не в инвентаре)
    public static boolean isGuiSlot(int slot) {
        int guiSlots = getGuiSlotsCount();
        return slot >= 0 && slot < guiSlots;
    }
    
    // Клик по слоту GUI (игнорируем инвентарь)
    public static void clickGuiSlot(int slot) {
        if (!isGuiSlot(slot)) {
            Logging.warn("Попытка кликнуть по слоту вне GUI: " + slot);
            return;
        }
        clickSlot(slot);
    }
    
    // Работа со слотами (только GUI слоты)
    public static ItemStack getItemInGuiSlot(int slot) {
        if (!isGuiSlot(slot)) return ItemStack.EMPTY;
        return getItemInSlot(slot);
    }
    
    // Получение имени предмета в слоте GUI
    public static String getItemNameInGuiSlot(int slot) {
        ItemStack item = getItemInGuiSlot(slot);
        if (item.isEmpty()) return "";
        return item.getHoverName().getString();
    }
    
    // Проверка, что слот GUI пустой
    public static boolean isGuiSlotEmpty(int slot) {
        return getItemInGuiSlot(slot).isEmpty();
    }
    
    // Поиск кнопки в GUI слотах
    public static int findButtonInGui(String text) {
        int guiSlots = getGuiSlotsCount();
        
        for (int slot = 0; slot < guiSlots; slot++) {
            ItemStack item = getItemInGuiSlot(slot);
            if (item.isEmpty()) continue;
            
            String displayName = item.getHoverName().getString();
            if (displayName.contains(text)) {
                Logging.debug("Нашел кнопку с текстом '" + text + "' в слоте GUI " + slot);
                return slot;
            }
        }
        
        return -1;
    }
    
    // Специальные методы для конкретных кнопок маркета
    public static void clickSortingButton() {
        clickGuiSlot(47); // "Изменить тип сортировки"
    }
    
    public static void clickNewFirstButton() {
        clickGuiSlot(6); // "Сначала новые предметы"
    }
    
    public static void clickBackButton() {
        int backSlot = findButtonInGui("Назад");
        if (backSlot != -1) {
            clickGuiSlot(backSlot);
        } else {
            clickGuiSlot(14); // Стандартный слот "Назад"
        }
    }
    
    public static void clickRefreshButton() {
        clickGuiSlot(50); // "Обновить страницу"
    }
    
    public static void clickToPaymentButton() {
        clickGuiSlot(53); // "Перейти к оплате"
    }
    
    public static void clickExitBackButton() {
        clickGuiSlot(49); // "Выйти назад"
    }
    
    // ========== ОСНОВНЫЕ МЕТОДЫ (без изменений, но используем GUI-версии) ==========
    
    // Получение общего размера контейнера
    public static int getContainerSize() {
        if (mc.player == null || mc.player.containerMenu == null) return 0;
        return mc.player.containerMenu.slots.size();
    }
    
    public static ItemStack getItemInSlot(int slot) {
        if (mc.player == null || mc.player.containerMenu == null) 
            return ItemStack.EMPTY;
        
        if (slot < 0 || slot >= mc.player.containerMenu.slots.size())
            return ItemStack.EMPTY;
        
        Slot s = mc.player.containerMenu.getSlot(slot);
        return s.getItem();
    }
    
    public static String getItemNameInSlot(int slot) {
        ItemStack item = getItemInSlot(slot);
        if (item.isEmpty()) return "";
        return item.getHoverName().getString();
    }
    
    public static boolean isSlotEmpty(int slot) {
        return getItemInSlot(slot).isEmpty();
    }
    
    public static boolean slotContainsItem(int slot, String itemName) {
        String name = getItemNameInSlot(slot);
        return name.contains(itemName);
    }
    
    public static void clickSlot(int slot) {
        clickSlot(slot, 0); // Левый клик по умолчанию
    }
    
    public static void clickSlot(int slot, int mouseButton) {
        if (mc.gameMode == null || mc.player == null || mc.player.containerMenu == null) {
            Logging.warn("Не могу кликнуть по слоту " + slot + ": gameMode, player или containerMenu null");
            return;
        }
        
        DelayHelper.randomDelay(50, 150); // Очеловечивание
        
        try {
            Logging.debug("Кликаю на слот " + slot + " кнопкой " + mouseButton);
            mc.gameMode.handleInventoryMouseClick(
                mc.player.containerMenu.containerId,
                slot,
                mouseButton,
                ClickType.PICKUP,
                mc.player
            );
        } catch (Exception e) {
            Logging.error("Ошибка при клике на слот " + slot + ": " + e.getMessage());
        }
    }
    
    public static void clickSlotRight(int slot) {
        clickSlot(slot, 1); // Правый клик
    }
    
    public static void clickSlotShift(int slot) {
        if (mc.gameMode == null || mc.player == null || mc.player.containerMenu == null) {
            Logging.warn("Не могу кликнуть шифтом по слоту " + slot + ": gameMode, player или containerMenu null");
            return;
        }
        
        DelayHelper.randomDelay(50, 150);
        
        try {
            Logging.debug("Кликаю шифтом на слот " + slot);
            mc.gameMode.handleInventoryMouseClick(
                mc.player.containerMenu.containerId,
                slot,
                0,
                ClickType.QUICK_MOVE,
                mc.player
            );
        } catch (Exception e) {
            Logging.error("Ошибка при клике шифтом на слот " + slot + ": " + e.getMessage());
        }
    }
    
    // Находим зеленую кнопку "КУПИТЬ" в GUI
    public static int findGreenBuyButton() {
        int guiSlots = getGuiSlotsCount();
        
        for (int slot = 0; slot < guiSlots; slot++) {
            ItemStack item = getItemInGuiSlot(slot);
            if (item.isEmpty()) continue;
            
            String displayName = item.getHoverName().getString();
            
            // Ищем зеленую кнопку КУПИТЬ
            if (displayName.contains("КУПИТЬ") || displayName.contains("Купить")) {
                // Проверяем цвет (зеленый = §a)
                if (displayName.contains("§a") || displayName.contains("§2") || 
                    displayName.contains("§l") || displayName.contains("§n")) {
                    Logging.debug("Нашел зеленую кнопку покупки в слоте GUI " + slot);
                    return slot;
                }
            }
        }
        
        // Если не нашли, пробуем стандартные позиции
        if (isBuyScreen()) {
            // Окно покупки обычно 3x3 или 5x5
            int guiSize = getGuiSlotsCount();
            int centerSlot = (guiSize >= 9) ? 4 : (guiSize / 2);
            Logging.debug("Использую центральный слот для кнопки покупки: " + centerSlot);
            return centerSlot;
        }
        
        Logging.warn("Не нашел зеленую кнопку покупки в GUI");
        return -1;
    }
    
    public static int findButtonByText(String text) {
        int guiSlots = getGuiSlotsCount();
        
        for (int slot = 0; slot < guiSlots; slot++) {
            ItemStack item = getItemInGuiSlot(slot);
            if (item.isEmpty()) continue;
            
            String displayName = item.getHoverName().getString();
            if (displayName.contains(text)) {
                Logging.debug("Нашел кнопку с текстом " + text + " в слоте " + slot);
                return slot;
            }
        }
        
        Logging.debug("Не нашел кнопку с текстом " + text);
        return -1;
    }
    
    public static void clickCopyButton() {
        int copySlot = findButtonByText("Копировать");
        if (copySlot != -1) {
            clickSlot(copySlot);
        } else {
            // Пробуем стандартные слоты
            Logging.debug("Использую стандартный слот для кнопки Копировать: слот 1");
            clickSlot(1);
        }
        DelayHelper.randomDelay(200, 400);
    }
    
    public static void clickNoButton() {
        int noSlot = findButtonByText("Нет");
        if (noSlot != -1) {
            clickSlot(noSlot);
        } else {
            // Стандартная позиция для кнопки "Нет" (обычно справа)
            Logging.debug("Использую стандартный слот для кнопки Нет: слот 2");
            clickSlot(2);
        }
        DelayHelper.randomDelay(200, 400);
    }
    
    public static void clickYesButton() {
        int yesSlot = findButtonByText("Да");
        if (yesSlot != -1) {
            clickSlot(yesSlot);
        }
        DelayHelper.randomDelay(200, 400);
    }
    
    public static void clickDoneButton() {
        int doneSlot = findButtonByText("Готово");
        if (doneSlot != -1) {
            clickSlot(doneSlot);
        } else {
            // Стандартная позиция
            Logging.debug("Использую стандартный слот для кнопки Готово: слот 0");
            clickSlot(0);
        }
        DelayHelper.randomDelay(200, 400);
    }
    
    public static void sendChatCommand(String command) {
        if (mc.player != null && !command.isEmpty()) {
            Logging.debug("Отправляю команду в чат: " + command);
            mc.player.chat(command);
            DelayHelper.randomDelay(300, 500);
        }
    }
    
    // Извлечение ссылки
    public static String extractLinkFromScreen() {
        if (mc.screen == null) {
            Logging.warn("Не могу извлечь ссылку: экран null");
            return null;
        }
        
        // Пробуем получить ссылку из заголовка окна
        String screenText = mc.screen.getTitle().getString();
        String link = extractLinkFromText(screenText);
        if (link != null) {
            Logging.debug("Нашел ссылку в заголовке окна: " + link);
            return link;
        }
        
        // Пробуем получить ссылку из GUI
        link = extractLinkFromGui();
        if (link != null) {
            Logging.debug("Нашел ссылку в GUI: " + link);
            return link;
        }
        
        // Пробуем получить ссылку из чата
        String chatText = getLatestChatMessage();
        link = extractLinkFromText(chatText);
        if (link != null) {
            Logging.debug("Нашел ссылку в чате: " + link);
            return link;
        }
        
        Logging.warn("Не смог найти ссылку на экране");
        return null;
    }
    
    // Метод для извлечения ссылки из GUI (теперь проще)
    public static String extractLinkFromGui() {
        if (mc.screen == null) return null;
        
        // 1. Проверяем заголовок
        String screenText = mc.screen.getTitle().getString();
        String link = extractLinkFromText(screenText);
        if (link != null) return link;
        
        // 2. Проверяем предметы в GUI (может быть книга со ссылкой)
        int guiSlots = getGuiSlotsCount();
        for (int slot = 0; slot < guiSlots; slot++) {
            ItemStack item = getItemInGuiSlot(slot);
            if (item.isEmpty()) continue;
            
            // Проверяем книги, бумаги и т.д.
            if (item.getItem() == Items.WRITABLE_BOOK || 
                item.getItem() == Items.WRITTEN_BOOK ||
                item.getItem() == Items.PAPER) {
                
                List<String> lore = getItemLore(item);
                for (String line : lore) {
                    link = extractLinkFromText(line);
                    if (link != null) return link;
                }
            }
        }
        
        return null;
    }
    
    private static String extractLinkFromText(String text) {
        if (text == null || text.isEmpty()) return null;
        
        // Регулярное выражение для поиска URL
        Pattern pattern = Pattern.compile("https?://[\\w\\.\\-\\?=&/%#]+");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            String found = matcher.group();
            // Убираем возможные пробелы в конце
            found = found.trim();
            // Убираем возможные знаки препинания после ссылки
            while (!found.isEmpty() && 
                   (found.endsWith(".") || found.endsWith(",") || found.endsWith(")") || 
                    found.endsWith("]") || found.endsWith("}"))) {
                found = found.substring(0, found.length() - 1);
            }
            return found;
        }
        
        return null;
    }
    
    private static String getLatestChatMessage() {
        // TODO: Реализовать получение последнего сообщения из чата
        // Это требует работы с системой чата Minecraft
        return null;
    }
    
    // Получение информации о предмете
    public static String getItemDisplayName(ItemStack item) {
        if (item.isEmpty()) return "";
        return item.getHoverName().getString();
    }
    
    public static List<String> getItemLore(ItemStack item) {
        List<String> lore = new ArrayList<>();
        
        if (item.isEmpty() || mc.player == null) return lore;
        
        try {
            List<ITextComponent> tooltip = item.getTooltipLines(mc.player, mc.options.advancedItemTooltips ? 
                net.minecraft.client.util.ITooltipFlag.TooltipFlags.ADVANCED : 
                net.minecraft.client.util.ITooltipFlag.TooltipFlags.NORMAL);
            
            for (ITextComponent component : tooltip) {
                lore.add(component.getString());
            }
        } catch (Exception e) {
            Logging.error("Ошибка при получении лора предмета: " + e.getMessage());
        }
        
        return lore;
    }
    
    public static int getItemCount(ItemStack item) {
        if (item.isEmpty()) return 0;
        return item.getCount();
    }
    
    public static boolean isShulkerBox(ItemStack item) {
        if (item.isEmpty()) return false;
        
        // Проверяем, является ли предмет шалкером
        return item.getItem() == Items.SHULKER_BOX ||
               item.getItem() == Items.WHITE_SHULKER_BOX ||
               item.getItem() == Items.ORANGE_SHULKER_BOX ||
               item.getItem() == Items.MAGENTA_SHULKER_BOX ||
               item.getItem() == Items.LIGHT_BLUE_SHULKER_BOX ||
               item.getItem() == Items.YELLOW_SHULKER_BOX ||
               item.getItem() == Items.LIME_SHULKER_BOX ||
               item.getItem() == Items.PINK_SHULKER_BOX ||
               item.getItem() == Items.GRAY_SHULKER_BOX ||
               item.getItem() == Items.LIGHT_GRAY_SHULKER_BOX ||
               item.getItem() == Items.CYAN_SHULKER_BOX ||
               item.getItem() == Items.PURPLE_SHULKER_BOX ||
               item.getItem() == Items.BLUE_SHULKER_BOX ||
               item.getItem() == Items.BROWN_SHULKER_BOX ||
               item.getItem() == Items.GREEN_SHULKER_BOX ||
               item.getItem() == Items.RED_SHULKER_BOX ||
               item.getItem() == Items.BLACK_SHULKER_BOX;
    }
    
    // Копирование в буфер обмена
    public static void copyToClipboard(String text) {
        try {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            Logging.info("Текст скопирован в буфер обмена: " + text);
        } catch (Exception e) {
            Logging.error("Ошибка копирования в буфер обмена: " + e.getMessage());
        }
    }
    
    // Определение цвета в названии
    public static boolean hasColorInName(ItemStack item, TextFormatting color) {
        if (item.isEmpty()) return false;
        String displayName = item.getHoverName().getString();
        return displayName.contains(color.toString());
    }
    
    // Поиск предмета по имени
    public static int findItemByName(String name) {
        int guiSlots = getGuiSlotsCount();
        
        for (int slot = 0; slot < guiSlots; slot++) {
            ItemStack item = getItemInGuiSlot(slot);
            if (item.isEmpty()) continue;
            
            String displayName = getItemDisplayName(item);
            if (displayName.contains(name)) {
                Logging.debug("Нашел предмет " + name + " в слоте GUI " + slot);
                return slot;
            }
        }
        
        Logging.debug("Не нашел предмет с именем " + name + " в GUI");
        return -1;
    }
    
    // Открытие и закрытие GUI
    public static void closeScreen() {
        if (mc.player != null) {
            Logging.debug("Закрываю экран");
            mc.player.closeContainer();
            DelayHelper.randomDelay(100, 200);
        }
    }
}
