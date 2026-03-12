package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprEntityScale extends PropertyExpression<Object, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprEntityScale.class, Number.class,
                "scale", "entities/refs")
            .name("Entity Scale")
            .description("Get/set the scale of an Entity/Ref.",
                "**Note**: Changing the scale of a player does not affect their camera.")
            .examples("if scale of target entity of player > 1:",
                "set scale of {_e} to 10",
                "add 0.1 to scale of target entity of player",
                "remove 0.5 from scale of {_entity}",
                "reset scale of player")
            .since("1.1.0")
            .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Number getProperty(@NotNull Object o) {
        EntityScaleComponent component;
        if (o instanceof Entity entity) {
            component = EntityUtils.getComponent(entity, EntityScaleComponent.getComponentType());

        } else if (o instanceof Ref<?> r) {
            Ref<EntityStore> ref = (Ref<EntityStore>) r;
            component = ref.getStore().getComponent(ref, EntityScaleComponent.getComponentType());
        } else {
            return null;
        }
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

    @SuppressWarnings({"ConstantValue", "unchecked"})
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Number number)) {
            return;
        }

        for (Object o : getOwner().getArray(ctx)) {
            EntityScaleComponent component;
            if (o instanceof Entity entity) {
                component = EntityUtils.ensureAndGetComponent(entity, EntityScaleComponent.getComponentType());
            } else if (o instanceof Ref<?> r) {
                Ref<EntityStore> ref = (Ref<EntityStore>) r;
                component = ref.getStore().ensureAndGetComponent(ref, EntityScaleComponent.getComponentType());
            } else {
                continue;
            }

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
