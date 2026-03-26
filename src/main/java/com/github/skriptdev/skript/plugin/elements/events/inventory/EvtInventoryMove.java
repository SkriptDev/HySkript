package com.github.skriptdev.skript.plugin.elements.events.inventory;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EvtInventoryMove extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtInventoryMove.class, "inventory move",
                "inventory pickup",
                "inventory (put|place) down")
            .name("Inventory Move")
            .description("Called when an item is moved in an inventory window.",
                "**NOTE**: This event has been removed by Hytale.")
            .since("1.3.0")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        parseContext.getLogger().error("This event is no longer valid, Hytale removed it", ErrorType.SEMANTIC_ERROR);

        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return false;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "deprecated";
    }

}
