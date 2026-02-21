package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprEntityScale extends PropertyExpression<Entity, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprEntityScale.class, Number.class,
                "scale", "entities")
            .name("Entity Scale")
            .description("Get/set the scale of an entity.",
                "**Note**: Changing the scale of a player does not affect their camera.")
            .examples("if scale of target entity of player > 1:",
                "set scale of {_e} to 10",
                "add 0.1 to scale of target entity of player",
                "remove 0.5 from scale of {_entity}",
                "reset scale of player")
            .since("1.1.0")
            .register();
    }

    @Override
    public @Nullable Number getProperty(@NotNull Entity entity) {
        EntityScaleComponent component = EntityUtils.getComponent(entity, EntityScaleComponent.getComponentType());
        if (component == null) return 1.0f;

        return component.getScale();
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
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Number number)) {
            return;
        }

        for (Entity entity : getOwner().getArray(ctx)) {
            EntityScaleComponent component = EntityUtils.ensureAndGetComponent(entity, EntityScaleComponent.getComponentType());

            float oldValue = component.getScale();
            float changeValue = number.floatValue();
            float newValue = switch (changeMode) {
                case ADD -> oldValue + changeValue;
                case REMOVE ->  oldValue - changeValue;
                case RESET -> 1.0f;
                default -> changeValue;
            };

            component.setScale(newValue);
        }
    }

}
