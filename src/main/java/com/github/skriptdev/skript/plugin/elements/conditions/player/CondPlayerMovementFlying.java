package com.github.skriptdev.skript.plugin.elements.conditions.player;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.protocol.SavedMovementStates;
import com.hypixel.hytale.protocol.packets.player.SetMovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CondPlayerMovementFlying extends PropertyConditional<Player> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondPlayerMovementFlying.class,
                "players", ConditionalType.BE, "flying")
            .name("Player Movement - Flying")
            .description("Checks if the player is flying. Can be set.")
            .examples("if player is flying:",
                "\tmessage \"You shouldn't be flying!\"",
                "\tset player is flying to false")
            .since("1.1.0")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return this.getPerformer().check(ctx, player -> {
            MovementStatesComponent component = EntityUtils.getMovementStatesComponent(player);

            assert component != null;
            return component.getMovementStates().flying;
        }, isNegated());
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.optionalArrayOf(Boolean.class);
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Boolean bool)) {
            return;
        }

        for (Player player : getPerformer().getArray(ctx)) {
            PlayerRef ref = EntityUtils.getComponent(player, PlayerRef.getComponentType());
            assert ref != null;
            MovementStatesComponent movementStates = EntityUtils.getMovementStatesComponent(player);
            assert movementStates != null;
            movementStates.getMovementStates().flying = bool;
            ref.getPacketHandler().writeNoCache(new SetMovementStates(new SavedMovementStates(bool)));
        }
    }
}
