package com.github.skriptdev.skript.plugin.elements.expressions.server;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExprServerViewRadius implements Expression<Number> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprServerViewRadius.class, Number.class, true, "server view radius")
            .name("Server View Radius")
            .description("Get/set the max view radius (in chunks) of players on the server.")
            .examples("set server view radius to 10",
                "add 1 to server view radius",
                "remove 3 from server view radius")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        return true;
    }

    @Override
    public Number[] getValues(@NotNull TriggerContext ctx) {
        return new Number[]{HytaleServer.get().getConfig().getMaxViewRadius()};
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
        HytaleServerConfig config = HytaleServer.get().getConfig();
        int oldValue = config.getMaxViewRadius();
        int change = number.intValue();
        int newValue = switch (changeMode) {
            case ADD -> oldValue + change;
            case REMOVE -> oldValue - change;
            default -> change;
        };
        config.setMaxViewRadius(newValue);
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "server view radius";
    }

}
