package com.github.skriptdev.skript.plugin.elements.expressions.entityeffect;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import org.jetbrains.annotations.Nullable;

public class ExprActiveEntityEffectEffect extends PropertyExpression<ActiveEntityEffect, EntityEffect> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprActiveEntityEffectEffect.class, EntityEffect.class,
                "entity effect", "activeentityeffects")
            .name("Active Entity Effect - Entity Effect")
            .description("Returns the EntityEffect of an ActiveEntityEffect.")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public @Nullable EntityEffect getProperty(ActiveEntityEffect effect) {
        int index = effect.getEntityEffectIndex();
        return EntityEffect.getAssetMap().getAsset(index);
    }

}
