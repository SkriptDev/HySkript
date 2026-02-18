package com.github.skriptdev.skript.plugin.elements.expressions.player;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprPlayerMovementFlySpeed extends PropertyExpression<Player, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprPlayerMovementFlySpeed.class, Number.class,
                "[(:horizontal|:vertical)] fly speed", "players")
            .name("Player Movement - Fly Speed")
            .description("Get/change the fly speed of a player.",
                "Without specifying horizontal/vertical, both will be changed and an average will be returned.",
                "This is not persistent.")
            .examples("set fly speed of player to 20",
                "add 2 to horizontal fly speed of player",
                "reset vertical fly speed of player")
            .since("INSERT VERSION")
            .register();
    }

    private boolean vertical;
    private boolean horizontal;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.vertical = parseContext.hasMark("vertical");
        this.horizontal = parseContext.hasMark("horizontal");
        return super.init(expressions, matchedPattern, parseContext);
    }

    @Override
    public @Nullable Number getProperty(@NotNull Player player) {
        MovementManager component = EntityUtils.getComponent(player, MovementManager.getComponentType());
        if (component == null) return 0.0f;

        MovementSettings settings = component.getSettings();
        if (this.vertical) {
            return settings.verticalFlySpeed;
        } else if (this.horizontal) {
            return settings.horizontalFlySpeed;
        } else {
            float total = settings.verticalFlySpeed + settings.horizontalFlySpeed;
            return total / 2;
        }

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

            MovementSettings settings = component.getSettings();
            MovementSettings defaultSettings = component.getDefaultSettings();

            if (changeMode == ChangeMode.RESET) {
                if (this.vertical) {
                    settings.verticalFlySpeed = defaultSettings.verticalFlySpeed;
                } else if (this.horizontal) {
                    settings.horizontalFlySpeed = defaultSettings.horizontalFlySpeed;
                } else {
                    settings.verticalFlySpeed = defaultSettings.verticalFlySpeed;
                    settings.horizontalFlySpeed = defaultSettings.horizontalFlySpeed;
                }
            } else if (number != null) {

                float previousValue;
                if (this.vertical) {
                    previousValue = settings.verticalFlySpeed;
                } else if (this.horizontal) {
                    previousValue = settings.horizontalFlySpeed;
                } else {
                    float v = settings.verticalFlySpeed + settings.horizontalFlySpeed;
                    previousValue = v / 2;
                }

                float changeValue = number.floatValue();

                float newValue = switch (changeMode) {
                    case ADD -> previousValue + changeValue;
                    case REMOVE -> previousValue - changeValue;
                    default -> changeValue;
                };

                if (this.vertical) {
                    settings.verticalFlySpeed = newValue;
                } else if (this.horizontal) {
                    settings.horizontalFlySpeed = newValue;
                } else {
                    settings.verticalFlySpeed = newValue;
                    settings.horizontalFlySpeed = newValue;
                }
            }
            PlayerRef ref = EntityUtils.getComponent(player, PlayerRef.getComponentType());
            assert ref != null;
            component.update(ref.getPacketHandler());
        }
    }

}
