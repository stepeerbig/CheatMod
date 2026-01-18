package com.cheatmod.gui;

import com.cheatmod.CheatMod;
import com.cheatmod.autobuy.MarketAutoBuy;
import com.cheatmod.config.ConfigManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModGuiScreen extends Screen {
    private Button toggleMarketBtn;
    private Button loadMarketConfigBtn;
    private Button telegramSettingsBtn;
    private Button closeBtn;
    
    private MarketAutoBuy marketAutoBuy;
    private ConfigManager configManager;
    
    private boolean marketEnabled = false;
    
    public ModGuiScreen() {
        super(new StringTextComponent("CheatMod Control Panel"));
        this.marketAutoBuy = MarketAutoBuy.getInstance();
        this.configManager = CheatMod.configManager;
    }
    
    @Override
    protected void init() {
        // Кнопка включения Market AutoBuy
        toggleMarketBtn = new Button(
            width / 2 - 100, height / 4 + 20,
            200, 20,
            new StringTextComponent(getMarketButtonText()),
            button -> {
                marketEnabled = !marketEnabled;
                marketAutoBuy.setEnabled(marketEnabled);
                toggleMarketBtn.setMessage(new StringTextComponent(getMarketButtonText()));
            }
        );
        addButton(toggleMarketBtn);
        
        // Кнопка загрузки конфига Market
        loadMarketConfigBtn = new Button(
            width / 2 - 100, height / 4 + 50,
            200, 20,
            new StringTextComponent("§eЗагрузить конфиг Market"),
            button -> {
                if (configManager.loadMarketConfig()) {
                    // Успешно загружено
                }
            }
        );
        addButton(loadMarketConfigBtn);
        
        // Кнопка настроек Telegram
        telegramSettingsBtn = new Button(
            width / 2 - 100, height / 4 + 80,
            200, 20,
            new StringTextComponent("§bНастройки Telegram"),
            button -> {
                minecraft.setScreen(new TelegramConfigScreen(this));
            }
        );
        addButton(telegramSettingsBtn);
        
        // Кнопка закрытия
        closeBtn = new Button(
            width / 2 - 100, height / 4 + 110,
            200, 20,
            new StringTextComponent("§cЗакрыть"),
            button -> onClose()
        );
        addButton(closeBtn);
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        
        // Заголовок
        drawCenteredString(matrixStack, font, "§l§6CheatMod AutoBuy", width / 2, 30, 0xFFFFFF);
        
        // Статус
        String status = marketAutoBuy.getStatus();
        drawCenteredString(matrixStack, font, "§7Статус: §f" + status, width / 2, height - 40, 0xFFFFFF);
        
        // Статистика
        if (marketEnabled) {
            drawCenteredString(matrixStack, font, "§a✓ Сканирование Market активно", width / 2, height - 60, 0xFFFFFF);
        }
    }
    
    private String getMarketButtonText() {
        return marketEnabled ? "§cВыключить Market AutoBuy" : "§aВключить Market AutoBuy";
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}