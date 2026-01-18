package com.cheatmod.events;

import com.cheatmod.CheatMod;
import com.cheatmod.KeyBinds;
import com.cheatmod.autobuy.MarketAutoBuy;
import com.cheatmod.gui.ModGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CheatMod.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        MarketAutoBuy marketAutoBuy = MarketAutoBuy.getInstance();
        
        // Открытие GUI по правому Shift
        if (KeyBinds.OPEN_GUI.isDown()) {
            mc.setScreen(new ModGuiScreen());
        }
        
        // Включение/выключение Market AutoBuy по M
        if (KeyBinds.TOGGLE_MARKET.isDown()) {
            marketAutoBuy.setEnabled(!marketAutoBuy.isEnabled());
        }
        
        // Пауза по P
        if (KeyBinds.PAUSE_AUTOBUY.isDown()) {
            marketAutoBuy.setPaused(!marketAutoBuy.isPaused());
        }
    }
}