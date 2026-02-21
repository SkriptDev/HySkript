package com.github.skriptdev.skript.plugin.elements.expressions.player;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprPlayerClientViewRadius extends PropertyExpression<Player, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprPlayerClientViewRadius.class, Number.class,
                "client view radius", "players")
            .name("Player Client View Radius")
            .description("Get/set the client view radius of players.",
                "Do note setting a value higher than the server view radius will not override.")
            .examples("set client view radius of player to 5",
                "add 1 to client view radius of all players",
                "remove 2 from client view radius of {_p}")
            .since("1.1.0")
            .register();
    }

    @Override
    public @Nullable Number getProperty(Player player) {
        return player.getClientViewRadius();
    }


    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            return Optional.of(new Class<?>[]{Number.class});
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Number number)) {
            return;
        }

        for (Player player : getOwner().getValues(ctx)) {
            int oldValue = player.getClientViewRadius();
            int changeValue = number.intValue();
            int newValue = switch (changeMode) {
                case ADD -> oldValue + changeValue;
                case REMOVE -> oldValue - changeValue;
                default -> changeValue;
            };
            player.setClientViewRadius(newValue);
        }
    }

}
