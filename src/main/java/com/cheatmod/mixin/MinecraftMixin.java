package com.cheatmod.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    
    @Inject(method = "getModList", at = @At("RETURN"), cancellable = true)
    private void hideOurModFromList(CallbackInfoReturnable<List<String>> cir) {
        List<String> mods = cir.getReturnValue();
        // Убираем наш мод из списка
        mods.removeIf(mod -> 
            mod.toLowerCase().contains("cheatmod") || 
            mod.toLowerCase().contains("cheat") ||
            mod.contains("CheatMod")
        );
        cir.setReturnValue(mods);
    }
}
