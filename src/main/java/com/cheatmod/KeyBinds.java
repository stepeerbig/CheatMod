package com.cheatmod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBinds {
    public static KeyBinding OPEN_GUI;
    public static KeyBinding TOGGLE_MARKET;
    public static KeyBinding TOGGLE_AUCTION;
    public static KeyBinding PAUSE_AUTOBUY;
    
    public static void register() {
        OPEN_GUI = new KeyBinding("key.cheatmod.opengui", 
            InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.cheatmod");
        
        TOGGLE_MARKET = new KeyBinding("key.cheatmod.toggle_market", 
            InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.categories.cheatmod");
        
        TOGGLE_AUCTION = new KeyBinding("key.cheatmod.toggle_auction", 
            InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_A, "key.categories.cheatmod");
        
        PAUSE_AUTOBUY = new KeyBinding("key.cheatmod.pause", 
            InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.cheatmod");
        
        ClientRegistry.registerKeyBinding(OPEN_GUI);
        ClientRegistry.registerKeyBinding(TOGGLE_MARKET);
        ClientRegistry.registerKeyBinding(TOGGLE_AUCTION);
        ClientRegistry.registerKeyBinding(PAUSE_AUTOBUY);
    }
}