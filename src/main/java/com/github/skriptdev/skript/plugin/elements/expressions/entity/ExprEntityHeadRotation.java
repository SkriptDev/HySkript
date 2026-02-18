package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprEntityHeadRotation extends PropertyExpression<Entity, Vector3f> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprEntityHeadRotation.class, Vector3f.class,
                "head rotation", "entities")
            .name("Entity Head Rotation")
            .description("Get the head rotation of an entity.")
            .examples("set {_rot} to head rotation of player")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public @Nullable Vector3f getProperty(@NotNull Entity entity) {
        HeadRotation component = EntityUtils.getComponent(entity, HeadRotation.getComponentType());
        if (component == null) return null;

        return component.getRotation();
    }

}
