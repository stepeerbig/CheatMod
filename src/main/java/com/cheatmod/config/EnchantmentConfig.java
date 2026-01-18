package com.cheatmod.config;

public class EnchantmentConfig {
    private String name;
    private int level;
    private double priceModifier;
    
    public EnchantmentConfig() {}
    
    public EnchantmentConfig(String name, int level, double priceModifier) {
        this.name = name;
        this.level = level;
        this.priceModifier = priceModifier;
    }
    
    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public double getPriceModifier() { return priceModifier; }
    public void setPriceModifier(double priceModifier) { this.priceModifier = priceModifier; }
}