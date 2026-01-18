package com.cheatmod.test;

import com.cheatmod.utils.GuiHelper;
import com.cheatmod.utils.Logging;

public class TestScript {
    
    public static void testMarketDetection() {
        Logging.info("=== ТЕСТ ОПРЕДЕЛЕНИЯ ОКОН ===");
        Logging.info("isMarketMainScreen: " + GuiHelper.isMarketMainScreen());
        Logging.info("isSortingScreen: " + GuiHelper.isSortingScreen());
        Logging.info("isBuyScreen: " + GuiHelper.isBuyScreen());
        Logging.info("isLinkScreen: " + GuiHelper.isLinkScreen());
        Logging.info("isShulkerScreen: " + GuiHelper.isShulkerScreen());
        Logging.info("Размер контейнера: " + GuiHelper.getContainerSize());
    }
    
    public static void testItemScanning() {
        Logging.info("=== ТЕСТ СКАНИРОВАНИЯ ПРЕДМЕТОВ ===");
        for (int i = 0; i < 4; i++) {
            String itemName = GuiHelper.getItemNameInSlot(i);
            if (!itemName.isEmpty()) {
                Logging.info("Слот " + i + ": " + itemName);
            } else {
                Logging.info("Слот " + i + ": пустой");
            }
        }
    }
}