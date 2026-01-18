package com.cheatmod.events;

import com.cheatmod.CheatMod;
import com.cheatmod.utils.ChatHelper;
import com.cheatmod.utils.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CheatMod.MOD_ID, value = Dist.CLIENT)
public class ChatEventHandler {
    
    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        if (event.getMessage() == null) return;
        
        String message = event.getMessage().getString();
        ChatHelper.addMessageToHistory(message);
        
        // Логируем все сообщения для отладки
        Logging.debug("Получено сообщение: " + message);
        
        // Проверяем важные сообщения
        checkImportantMessages(message);
    }
    
    private static void checkImportantMessages(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Сообщения о сортировке
        if (lowerMessage.contains("тип сортировки был успешно изменен") ||
            lowerMessage.contains("сортировка установлена") ||
            lowerMessage.contains("sorting changed") ||
            lowerMessage.contains("сортировка по новизне")) {
            
            Logging.info("Получено подтверждение сортировки");
            // Здесь можно обновить состояние автобай
        }
        
        // Сообщения о покупке
        if (lowerMessage.contains("успешно куплен") ||
            lowerMessage.contains("покупка завершена") ||
            lowerMessage.contains("successfully purchased") ||
            lowerMessage.contains("вы купили")) {
            
            Logging.info("Получено подтверждение покупки");
            // Обработка успешной покупки
        }
        
        // Сообщения об ошибках
        if (lowerMessage.contains("недостаточно средств") ||
            lowerMessage.contains("не хватает") ||
            lowerMessage.contains("insufficient funds") ||
            lowerMessage.contains("ошибка покупки")) {
            
            Logging.error("Ошибка покупки: " + message);
            // Обработка ошибки
        }
        
        // Сообщения с ссылками
        if (lowerMessage.contains("http://") || lowerMessage.contains("https://")) {
            Logging.info("Обнаружена ссылка в чате");
            // Можно извлечь ссылку
        }
        
        // Сообщения о шалкерах
        if (lowerMessage.contains("шалкер") || lowerMessage.contains("shulker")) {
            Logging.debug("Сообщение о шалкере: " + message);
        }
    }
}
