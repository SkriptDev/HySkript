package com.shanebeestudios.skript.plugin.command;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.shanebeestudios.skript.plugin.HySk;
import com.shanebeestudios.skript.plugin.Skript;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SkriptCommand extends AbstractCommandCollection {

    public SkriptCommand(CommandRegistry registry) {
        super("skript", "Skript commands");
        addAliases("sk");

        addSubCommand(reloadCommand());

        registry.registerCommand(this);
    }

    private AbstractCommand reloadCommand() {
        return new AbstractCommand("reload", "Reloads all scripts.") {
            @Override
            protected CompletableFuture<Void> execute(@NotNull CommandContext commandContext) {
                return CompletableFuture.runAsync(() -> {
                    Skript skript = HySk.getInstance().getSkript();
                    skript.getElementRegistration().clearTriggers();
                    skript.getScriptsLoader().loadScripts(skript.getScriptsPath(), true);
                });
            }
        };
    }

}
