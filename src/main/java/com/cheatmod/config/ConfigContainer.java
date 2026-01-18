package com.cheatmod.config;

import java.util.List;
import java.util.ArrayList;

public class ConfigContainer {
    private String version = "1.0";
    private List<ItemConfig> items;
    
    public ConfigContainer() {
        this.items = new ArrayList<>();
    }
    
    // Геттеры и сеттеры
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public List<ItemConfig> getItems() { return items; }
    public void setItems(List<ItemConfig> items) { this.items = items; }
    public void addItem(ItemConfig item) { this.items.add(item); }
}