package com.github.skriptdev.skript.plugin.elements.conditions.player;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import org.jetbrains.annotations.NotNull;

public class CondPlayerHasPlayedBefore extends PropertyConditional<Player> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondPlayerHasPlayedBefore.class,
            "players",
            ConditionalType.HAVE,
            "played before")
            .name("Player - Has Played Before")
            .description("Checks if the player has played before.")
            .examples("on player ready:",
                "\tif player has not played before:",
                "\t\tadd itemstack of 10 of food_kebab_meat to inventory of player")
            .since("1.1.0")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return getPerformer().check(ctx, player -> !player.isFirstSpawn(), isNegated());
    }

}
