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

public class ExprPlayerMovementJumpForce extends PropertyExpression<Player, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprPlayerMovementJumpForce.class, Number.class,
                "jump force", "players")
            .name("Player Movement - Jump Force")
            .description("Get/change the jump force of a player.",
                "This is not persistent. Default = 11.8")
            .examples("set jump force of player to 10",
                "reset jump force of player")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public @Nullable Number getProperty(@NotNull Player player) {
        MovementManager component = EntityUtils.getComponent(player, MovementManager.getComponentType());
        if (component == null) return 0.0f;

        return component.getSettings().jumpForce;
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
                component.getSettings().jumpForce = component.getDefaultSettings().jumpForce;
            } else if (number != null) {

                float previousValue = component.getSettings().jumpForce;
                float changeValue = number.floatValue();

                component.getSettings().jumpForce = switch (changeMode) {
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
