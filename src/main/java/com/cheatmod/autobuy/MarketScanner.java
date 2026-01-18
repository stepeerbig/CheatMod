package com.cheatmod.autobuy;

import com.cheatmod.CheatMod;
import com.cheatmod.config.ItemConfig;
import com.cheatmod.utils.GuiHelper;
import com.cheatmod.utils.ItemHelper;
import com.cheatmod.utils.Logging;
import net.minecraft.item.ItemStack;
import java.util.List;

public class MarketScanner {
    private int lastValidSlot = -1;
    
    public boolean isItemValid(ItemStack item, int slot) {
        if (item.isEmpty()) {
            Logging.debug("Слот " + slot + ": пустой");
            return false;
        }
        
        try {
            String displayName = item.getHoverName().getString();
            Logging.info("Сканирую предмет в слоте " + slot + ": " + displayName);
            
            // Проверяем, есть ли предмет в конфиге Market
            ItemConfig config = CheatMod.configManager.getMarketItem(displayName);
            if (config == null) {
                Logging.debug("Предмет не найден в конфиге: " + displayName);
                return false;
            }
            
            Logging.info("✓ Предмет найден в конфиге: " + displayName);
            Logging.info("  Цена в конфиге: " + config.getMinPrice() + "-" + config.getMaxPrice() + " coins");
            
            // TODO: Здесь будет проверка зачарований и цены
            // Пока просто проверяем наличие в конфиге
            
            lastValidSlot = slot;
            return true;
            
        } catch (Exception e) {
            Logging.error("Ошибка при сканировании предмета: " + e.getMessage());
            return false;
        }
    }
    
    public int getLastValidSlot() {
        return lastValidSlot;
    }
}
