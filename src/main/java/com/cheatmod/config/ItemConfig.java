package com.cheatmod.config;

import java.util.List;
import java.util.ArrayList;

public class ItemConfig {
    private String displayName;
    private String minecraftId;
    private int minPrice;
    private int maxPrice;
    private String customEffects;
    private String specialAbility;
    private List<EnchantmentConfig> enchantments;
    
    public ItemConfig() {
        this.enchantments = new ArrayList<>();
    }
    
    public ItemConfig(String displayName, String minecraftId, int minPrice, int maxPrice) {
        this();
        this.displayName = displayName;
        this.minecraftId = minecraftId;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
    
    // Геттеры и сеттеры
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getMinecraftId() { return minecraftId; }
    public void setMinecraftId(String minecraftId) { this.minecraftId = minecraftId; }
    
    public int getMinPrice() { return minPrice; }
    public void setMinPrice(int minPrice) { this.minPrice = minPrice; }
    
    public int getMaxPrice() { return maxPrice; }
    public void setMaxPrice(int maxPrice) { this.maxPrice = maxPrice; }
    
    public String getCustomEffects() { return customEffects; }
    public void setCustomEffects(String customEffects) { this.customEffects = customEffects; }
    
    public String getSpecialAbility() { return specialAbility; }
    public void setSpecialAbility(String specialAbility) { this.specialAbility = specialAbility; }
    
    public List<EnchantmentConfig> getEnchantments() { return enchantments; }
    public void setEnchantments(List<EnchantmentConfig> enchantments) { this.enchantments = enchantments; }
    public void addEnchantment(EnchantmentConfig enchantment) { this.enchantments.add(enchantment); }
    
    @Override
    public String toString() {
        return displayName + " [" + minecraftId + "] " + minPrice + "-" + maxPrice + " coins";
    }
}