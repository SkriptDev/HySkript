package com.github.skriptdev.skript.plugin.elements.conditions.player;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.protocol.SavedMovementStates;
import com.hypixel.hytale.protocol.packets.player.SetMovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CondPlayerMovementCanFly extends PropertyConditional<Player> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondPlayerMovementCanFly.class,
                "players", ConditionalType.CAN, "fly")
            .name("Player Movement - Can Fly")
            .description("Check/set whether the player can fly.")
            .examples("if player can fly:",
                "set player can fly to false",
                "reset player can fly")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return getPerformer().check(ctx, player -> {
            MovementManager component = EntityUtils.getComponent(player, MovementManager.getComponentType());
            assert component != null;
            return component.getSettings().canFly;
        }, isNegated());
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
            return Optional.of(new Class<?>[]{Boolean.class});
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        Boolean changeValue = null;
        if (changeWith != null && changeWith.length > 0 && changeWith[0] instanceof Boolean bool) {
            changeValue = bool;
        }
        for (Player player : getPerformer().getArray(ctx)) {
            MovementManager component = EntityUtils.getComponent(player, MovementManager.getComponentType());
            if (component == null) continue;

            PlayerRef ref = EntityUtils.getComponent(player, PlayerRef.getComponentType());
            assert ref != null;

            if (changeMode == ChangeMode.RESET) {
                component.getSettings().canFly = component.getDefaultSettings().canFly;
            } else if (changeValue != null) {
                component.getSettings().canFly = changeValue;

            }

            if (!component.getSettings().canFly) {
                // Stop the player from actually flying
                MovementStatesComponent movementStates = EntityUtils.getMovementStatesComponent(player);
                assert movementStates != null;
                movementStates.getMovementStates().flying = false;
                ref.getPacketHandler().writeNoCache(new SetMovementStates(new SavedMovementStates(false)));

            }

            component.update(ref.getPacketHandler());
        }
    }

}
