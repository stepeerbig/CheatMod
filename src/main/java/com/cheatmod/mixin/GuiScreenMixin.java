package com.cheatmod.mixin;

import com.cheatmod.utils.Logging;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class GuiScreenMixin {
    
    @Inject(method = "init", at = @At("HEAD"))
    private void onScreenInit(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        String title = screen.getTitle().getString();
        
        // Логируем открытие всех окон для отладки
        Logging.debug("Открыто окно: " + title);
        
        // Автоматическое определение окон маркета
        if (title.startsWith("Маркет за коины (")) {
            Logging.info("Обнаружен главный экран маркета");
        } else if (title.equals("Изменить тип сортировки")) {
            Logging.info("Обнаружено окно сортировки");
        } else if (title.startsWith("Инвентарь (")) {
            Logging.info("Обнаружен шалкер с ценой: " + title);
        }
    }
}
