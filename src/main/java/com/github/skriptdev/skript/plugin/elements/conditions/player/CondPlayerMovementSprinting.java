package com.github.skriptdev.skript.plugin.elements.conditions.player;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import org.jetbrains.annotations.NotNull;

public class CondPlayerMovementSprinting extends PropertyConditional<Player> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondPlayerMovementSprinting.class,
                "players", ConditionalType.BE, "sprinting")
            .name("Player Movement - Sprinting")
            .description("Checks if the player is sprinting.")
            .examples("if player is sprinting:",
                "\tmessage \"You are sprinting!\"")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return this.getPerformer().check(ctx, player -> {
            MovementStatesComponent component = EntityUtils.getMovementStatesComponent(player);

            assert component != null;
            return component.getMovementStates().sprinting;
        }, isNegated());
    }

}
