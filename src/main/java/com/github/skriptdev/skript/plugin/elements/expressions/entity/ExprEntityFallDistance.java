package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprEntityFallDistance extends PropertyExpression<LivingEntity, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprEntityFallDistance.class, Number.class,
                "fall distance", "livingentities")
            .name("Entity Fall Distance")
            .description("Get/set the fall distance of an entity.")
            .examples("if fall distance of player > 10:",
                "set fall distance of player to 10",
                "add 1 to fall distance of player",
                "remove 10 from fall distance of {_e}",
                "clear fall distance of player")
            .register();
    }

    @Override
    public @Nullable Number getProperty(LivingEntity owner) {
        return owner.getCurrentFallDistance();
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.DELETE) {
            return CollectionUtils.optionalArrayOf(Number.class);
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        double changeValue = 0;
        if (changeWith != null && changeWith.length > 0 && changeWith[0] instanceof Number num) {
            changeValue = num.doubleValue();
        }

        for (LivingEntity livingEntity : getOwner().getArray(ctx)) {
            double current = livingEntity.getCurrentFallDistance();

            double newValue = switch (changeMode) {
                case ADD -> current + changeValue;
                case REMOVE -> current - changeValue;
                default -> changeValue;
            };
            livingEntity.setCurrentFallDistance(Math.max(newValue, 0));
        }
    }
}
