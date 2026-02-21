package com.github.skriptdev.skript.plugin.elements.conditions.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import org.jetbrains.annotations.NotNull;

public class CondEntityIsTameable extends PropertyConditional<LivingEntity> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondEntityIsTameable.class, "livingentities",
                ConditionalType.BE, "tameable")
            .name("Entity is Tameable")
            .description("Check if an entity is tameable.")
            .experimental("Hytale is just adding taming, so this may change in the future.")
            .examples("if target entity of player is tameable:")
            .since("1.1.0")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return this.getPerformer().check(ctx, entity -> {
            if (entity instanceof NPCEntity npcEntity) {
                return EntityUtils.isTameable(npcEntity);
            }
            return false;
        }, isNegated());
    }

}
