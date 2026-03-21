package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityReferenceUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprEntityBoundingBox extends PropertyExpression<Object, Box> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprEntityBoundingBox.class, Box.class,
                "entity bounding box", "entities/refs")
            .name("Entity Bounding Box")
            .description("Get/set the bounding box of an Entity/Ref.",
                "This will use/return a Box object.",
                "Changes may not be persistent on some objects, such as dropped items.")
            .examples("set {_box} to entity bounding box of {_e}",
                "set entity bounding box of {_e} to box(0, 0, 0, 1, 1, 1)")
            .since("1.5.0")
            .register();
    }

    @Override
    public @Nullable Box getProperty(@NotNull Object owner) {
        BoundingBox component = EntityReferenceUtils.getComponent(owner, BoundingBox.getComponentType());
        if (component == null) return null;

        return component.getBoundingBox();
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
            return CollectionUtils.optionalArrayOf(Box.class);
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        Box box = new Box();

        if (changeWith != null && changeWith.length > 0 && changeWith[0] instanceof Box b) {
            box = b;
        }

        for (Object owner : getOwner().getArray(ctx)) {
            if (changeMode == ChangeMode.SET) {
                BoundingBox component = EntityReferenceUtils.ensureAndGetComponent(owner, BoundingBox.getComponentType());
                component.setBoundingBox(box);
            } else {
                EntityReferenceUtils.tryRemoveComponent(owner, BoundingBox.getComponentType());
            }
        }
    }

}
