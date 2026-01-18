package com.cheatmod.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemHelper {
    
    // Получение информации о зачарованиях
    public static List<String> getEnchantments(ItemStack item) {
        List<String> enchantments = new ArrayList<>();
        
        if (item.isEmpty() || !item.isEnchanted()) return enchantments;
        
        CompoundNBT tag = item.getTag();
        if (tag != null && tag.contains("Enchantments", 9)) {
            ListNBT enchantList = tag.getList("Enchantments", 10);
            for (int i = 0; i < enchantList.size(); i++) {
                CompoundNBT enchantTag = enchantList.getCompound(i);
                String enchantId = enchantTag.getString("id");
                int level = enchantTag.getInt("lvl");
                
                // Преобразуем ID в читаемое имя
                String enchantName = getEnchantmentName(enchantId);
                enchantments.add(enchantName + " " + level);
            }
        }
        
        return enchantments;
    }
    
    private static String getEnchantmentName(String id) {
        // Простое преобразование ID зачарований
        switch (id) {
            case "minecraft:protection": return "Защита";
            case "minecraft:fire_protection": return "Защита от огня";
            case "minecraft:feather_falling": return "Невесомость";
            case "minecraft:blast_protection": return "Взрывоустойчивость";
            case "minecraft:projectile_protection": return "Защита от снарядов";
            case "minecraft:thorns": return "Шипы";
            case "minecraft:respiration": return "Подводное дыхание";
            case "minecraft:aqua_affinity": return "Подводник";
            case "minecraft:sharpness": return "Острота";
            case "minecraft:smite": return "Небесная кара";
            case "minecraft:bane_of_arthropods": return "Бич членистоногих";
            case "minecraft:knockback": return "Отдача";
            case "minecraft:fire_aspect": return "Заговор огня";
            case "minecraft:looting": return "Добыча";
            case "minecraft:efficiency": return "Эффективность";
            case "minecraft:silk_touch": return "Шелковое касание";
            case "minecraft:unbreaking": return "Прочность";
            case "minecraft:fortune": return "Удача";
            case "minecraft:power": return "Сила";
            case "minecraft:punch": return "Отбрасывание";
            case "minecraft:flame": return "Огонь";
            case "minecraft:infinity": return "Бесконечность";
            case "minecraft:luck_of_the_sea": return "Везучий рыбак";
            case "minecraft:lure": return "Приманка";
            case "minecraft:mending": return "Починка";
            case "minecraft:depth_strider": return "Подводная ходьба";
            case "minecraft:frost_walker": return "Ледоход";
            case "minecraft:sweeping": return "Разящий клинок";
            case "minecraft:loyalty": return "Верность";
            case "minecraft:impaling": return "Пронзание";
            case "minecraft:riptide": return "Водоворот";
            case "minecraft:channeling": return "Громовержец";
            default: return id.replace("minecraft:", "").replace("_", " ");
        }
    }
    
    // Проверка на наличие конкретного зачарования
    public static boolean hasEnchantment(ItemStack item, String enchantName) {
        List<String> enchants = getEnchantments(item);
        for (String enchant : enchants) {
            if (enchant.toLowerCase().contains(enchantName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    // Получение уровня зачарования
    public static int getEnchantmentLevel(ItemStack item, String enchantName) {
        List<String> enchants = getEnchantments(item);
        for (String enchant : enchants) {
            if (enchant.toLowerCase().contains(enchantName.toLowerCase())) {
                // Извлекаем уровень (последнее число в строке)
                Pattern pattern = Pattern.compile("(\\d+)$");
                Matcher matcher = pattern.matcher(enchant);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
                return 1; // По умолчанию
            }
        }
        return 0;
    }
    
    // Проверка на наличие эффектов зелья
    public static List<String> getPotionEffects(ItemStack item) {
        List<String> effects = new ArrayList<>();
        
        if (item.isEmpty()) return effects;
        
        CompoundNBT tag = item.getTag();
        if (tag != null && tag.contains("CustomPotionEffects", 9)) {
            ListNBT effectList = tag.getList("CustomPotionEffects", 10);
            for (int i = 0; i < effectList.size(); i++) {
                CompoundNBT effectTag = effectList.getCompound(i);
                int effectId = effectTag.getInt("Id");
                int amplifier = effectTag.getInt("Amplifier");
                int duration = effectTag.getInt("Duration");
                
                String effectName = getEffectName(effectId);
                effects.add(effectName + " " + (amplifier + 1) + " (" + (duration/20) + "сек)");
            }
        }
        
        return effects;
    }
    
    private static String getEffectName(int id) {
        // Базовые эффекты зелий
        switch (id) {
            case 1: return "Скорость";
            case 2: return "Замедление";
            case 3: return "Прыгучесть";
            case 4: return "Слабость";
            case 5: return "Сила";
            case 6: return "Мгновенное лечение";
            case 7: return "Мгновенный урон";
            case 8: return "Регенерация";
            case 9: return "Огнестойкость";
            case 10: return "Водное дыхание";
            case 11: return "Незримость";
            case 12: return "Слепота";
            case 13: return "Ночное зрение";
            case 14: return "Голод";
            case 15: return "Усталость";
            case 16: return "Ядовитость";
            case 17: return "Исцеление";
            case 18: return "Усиление";
            case 19: return "Поднятие";
            case 20: return "Удача";
            case 21: return "Неудача";
            default: return "Эффект " + id;
        }
    }
    
    // Извлечение цены из лора
    public static int extractPriceFromLore(ItemStack item) {
        List<String> lore = GuiHelper.getItemLore(item);
        
        for (String line : lore) {
            // Ищем строки с ценой (обычно содержат "coins", "монет" или знак $/₽)
            if (line.contains("coin") || line.contains("монет") || 
                line.contains("$") || line.contains("₽") || line.contains("€")) {
                
                // Ищем числа в строке
                Pattern pattern = Pattern.compile("(\\d{1,3}(?:[,\\s]?\\d{3})*(?:\\.\\d+)?)");
                Matcher matcher = pattern.matcher(line);
                
                if (matcher.find()) {
                    try {
                        String numberStr = matcher.group(1).replaceAll("[,\\s]", "");
                        return Integer.parseInt(numberStr);
                    } catch (NumberFormatException e) {
                        // Пропускаем некорректные числа
                    }
                }
            }
        }
        
        return 0;
    }
    
    // Проверка, является ли предмет инструментом
    public static boolean isTool(ItemStack item) {
        if (item.isEmpty()) return false;
        
        String name = item.getHoverName().getString().toLowerCase();
        return name.contains("кирка") || name.contains("pickaxe") ||
               name.contains("топор") || name.contains("axe") ||
               name.contains("лопата") || name.contains("shovel") ||
               name.contains("мотыга") || name.contains("hoe") ||
               name.contains("меч") || name.contains("sword") ||
               name.contains("лук") || name.contains("bow") ||
               name.contains("арбалет") || name.contains("crossbow") ||
               name.contains("удочка") || name.contains("fishing rod");
    }
    
    // Проверка, является ли предмет броней
    public static boolean isArmor(ItemStack item) {
        if (item.isEmpty()) return false;
        
        String name = item.getHoverName().getString().toLowerCase();
        return name.contains("шлем") || name.contains("helmet") ||
               name.contains("нагрудник") || name.contains("chestplate") ||
               name.contains("поножи") || name.contains("leggings") ||
               name.contains("ботинки") || name.contains("boots") ||
               name.contains("щит") || name.contains("shield");
    }
    
    // Получение NBT тега как строки (для отладки)
    public static String getNbtAsString(ItemStack item) {
        if (item.isEmpty() || !item.hasTag()) return "";
        
        CompoundNBT tag = item.getTag();
        return tag != null ? tag.toString() : "";
    }
}