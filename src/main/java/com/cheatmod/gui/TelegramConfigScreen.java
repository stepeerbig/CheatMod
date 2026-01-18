package com.cheatmod.gui;

import com.cheatmod.CheatMod;
import com.cheatmod.telegram.TelegramManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class TelegramConfigScreen extends Screen {
    private final Screen parentScreen;
    private TextFieldWidget tokenField;
    private TextFieldWidget chatIdField;
    private Button toggleButton;
    private Button saveButton;
    
    public TelegramConfigScreen(Screen parentScreen) {
        super(new StringTextComponent("Telegram Settings"));
        this.parentScreen = parentScreen;
    }
    
    @Override
    protected void init() {
        // Поле для токена бота
        tokenField = new TextFieldWidget(font, width / 2 - 100, 80, 200, 20, 
            new StringTextComponent("Bot Token"));
        tokenField.setMaxLength(100);
        tokenField.setValue(CheatMod.telegramManager.getBotToken() != null ? 
            CheatMod.telegramManager.getBotToken() : "");
        addWidget(tokenField);
        
        // Поле для Chat ID
        chatIdField = new TextFieldWidget(font, width / 2 - 100, 110, 200, 20, 
            new StringTextComponent("Chat ID"));
        chatIdField.setMaxLength(50);
        chatIdField.setValue(CheatMod.telegramManager.getChatId() != null ? 
            CheatMod.telegramManager.getChatId() : "");
        addWidget(chatIdField);
        
        // Кнопка включения/выключения
        toggleButton = new Button(width / 2 - 100, 140, 200, 20,
            CheatMod.telegramManager.isEnabled() ? 
                new StringTextComponent("§aTelegram включен") : 
                new StringTextComponent("§cTelegram выключен"),
            button -> {
                CheatMod.telegramManager.setEnabled(!CheatMod.telegramManager.isEnabled());
                toggleButton.setMessage(CheatMod.telegramManager.isEnabled() ?
                    new StringTextComponent("§aTelegram включен") :
                    new StringTextComponent("§cTelegram выключен"));
            });
        addButton(toggleButton);
        
        // Кнопка сохранения
        saveButton = new Button(width / 2 - 100, 170, 200, 20,
            new StringTextComponent("§aСохранить"),
            button -> {
                CheatMod.telegramManager.setBotToken(tokenField.getValue());
                CheatMod.telegramManager.setChatId(chatIdField.getValue());
                onClose();
            });
        addButton(saveButton);
        
        // Кнопка отмены
        addButton(new Button(width / 2 - 100, 200, 200, 20,
            new StringTextComponent("§cОтмена"),
            button -> onClose()));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, font, "Настройки Telegram", width / 2, 20, 0xFFFFFF);
        drawString(matrixStack, font, "Токен бота:", width / 2 - 100, 70, 0xAAAAAA);
        drawString(matrixStack, font, "Chat ID:", width / 2 - 100, 100, 0xAAAAAA);
        
        tokenField.render(matrixStack, mouseX, mouseY, partialTicks);
        chatIdField.render(matrixStack, mouseX, mouseY, partialTicks);
        
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void tick() {
        tokenField.tick();
        chatIdField.tick();
    }
    
    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}