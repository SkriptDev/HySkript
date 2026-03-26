package com.github.skriptdev.skript.plugin.elements.events.entity;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EvtLivingEntityInvChange extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtLivingEntityInvChange.class,
                "living entity inventory change", "living entity inventory changed", "living entity inventory change event")
            .name("Living Entity Inventory Change")
            .description("Called when a living entity's inventory changes.",
                "**NOTE**: This event has been removed by Hytale.")
            .since("1.0.0")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        parseContext.getLogger().error("This event is no longer valid, Hytale removed it", ErrorType.SEMANTIC_ERROR);
        return false;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return false;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "living entity inventory change";
    }

}
