package com.github.skriptdev.skript.plugin.elements.conditions.player;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import org.jetbrains.annotations.NotNull;

public class CondPlayerMovementCrouching extends PropertyConditional<Player> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondPlayerMovementCrouching.class,
                "players", ConditionalType.BE, "crouching")
            .name("Player Movement - Crouching")
            .description("Checks if the player is crouching.")
            .examples("if player is crouching:",
                "\tmessage \"You are crouching!\"")
            .since("1.0.0")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return this.getPerformer().check(ctx, player -> {
            MovementStatesComponent component = EntityUtils.getMovementStatesComponent(player);

            assert component != null;
            return component.getMovementStates().crouching;
        }, isNegated());
    }

}
