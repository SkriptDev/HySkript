package com.github.skriptdev.skript.plugin.elements.conditions.player;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import org.jetbrains.annotations.NotNull;

public class CondPlayerMovementFalling extends PropertyConditional<Player> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondPlayerMovementFalling.class,
                "players", ConditionalType.BE, "falling")
            .name("Player Movement - Falling")
            .description("Checks if the player is falling.")
            .examples("if player is falling:",
                "\tmessage \"You are falling!\"")
            .since("1.1.0")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return this.getPerformer().check(ctx, player -> {
            MovementStatesComponent component = EntityUtils.getMovementStatesComponent(player);

            assert component != null;
            return component.getMovementStates().falling;
        }, isNegated());
    }

}
