package com.shanebeestudios.skript.api.skript.eventcontext;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import io.github.syst3ms.skriptparser.lang.TriggerContext;

public class ScriptLoadContext implements TriggerContext {

    @Override
    public String getName() {
        return "main";
    }

    public CommandSender[] getSender() {
        return new CommandSender[]{ConsoleSender.INSTANCE};
    }

}
