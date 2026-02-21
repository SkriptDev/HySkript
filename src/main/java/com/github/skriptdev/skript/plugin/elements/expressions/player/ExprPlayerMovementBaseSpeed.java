package com.github.skriptdev.skript.plugin.elements.expressions.player;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprPlayerMovementBaseSpeed extends PropertyExpression<Player, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprPlayerMovementBaseSpeed.class, Number.class,
                "base speed", "players")
            .name("Player Movement - Base Speed")
            .description("Get/change the base speed of a player.",
                "This is not persistent. Default = 5.5")
            .examples("set base speed of player to 10",
                "reset base speed of player")
            .since("1.1.0")
            .register();
    }

    @Override
    public @Nullable Number getProperty(@NotNull Player player) {
        MovementManager component = EntityUtils.getComponent(player, MovementManager.getComponentType());
        if (component == null) return 0.0f;

        return component.getSettings().baseSpeed;
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.RESET) {
            return Optional.of(new Class<?>[]{Number.class});
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        Number number = null;
        if (changeWith != null && changeWith.length > 0 && changeWith[0] instanceof Number num) {
            number = num;
        }

        for (Player player : getOwner().getArray(ctx)) {
            MovementManager component = EntityUtils.getComponent(player, MovementManager.getComponentType());
            if (component == null) continue;

            if (changeMode == ChangeMode.RESET) {
                component.getSettings().baseSpeed = component.getDefaultSettings().baseSpeed;
            } else if (number != null) {

                float previousValue = component.getSettings().baseSpeed;
                float changeValue = number.floatValue();

                component.getSettings().baseSpeed = switch (changeMode) {
                    case ADD -> previousValue + changeValue;
                    case REMOVE -> previousValue - changeValue;
                    default -> changeValue;
                };
            }
            PlayerRef ref = EntityUtils.getComponent(player, PlayerRef.getComponentType());
            assert ref != null;
            component.update(ref.getPacketHandler());
        }
    }

}
