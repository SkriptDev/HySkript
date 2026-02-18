package com.github.skriptdev.skript.plugin.elements.conditions.player;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import org.jetbrains.annotations.NotNull;

public class CondPlayerMovementSwimming extends PropertyConditional<Player> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondPlayerMovementSwimming.class,
                "players", ConditionalType.BE, "swimming")
            .name("Player Movement - Swimming")
            .description("Checks if the player is swimming.")
            .examples("if player is swimming:",
                "\tmessage \"You are swimming!\"")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return this.getPerformer().check(ctx, player -> {
            MovementStatesComponent component = EntityUtils.getMovementStatesComponent(player);

            assert component != null;
            return component.getMovementStates().swimming;
        }, isNegated());
    }

}
