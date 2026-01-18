package com.cheatmod.commands;

import com.cheatmod.test.TestScript;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class TestCommand {
    
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal("cheattest")
            .executes(context -> {
                TestScript.testMarketDetection();
                TestScript.testItemScanning();
                context.getSource().sendSuccess(
                    new StringTextComponent("Тест запущен. Смотрите логи."), 
                    false
                );
                return Command.SINGLE_SUCCESS;
            });
        
        dispatcher.register(command);
    }
}
