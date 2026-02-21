package com.github.skriptdev.skript.plugin.elements.effects.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EffTame extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffTame.class, "tame %livingentities%", "untame %livingentities%")
            .name("Entity Taming")
            .description("Tame/untame a LivingEntity.",
                "This will only work on tameable entities.")
            .examples("tame target entity of player",
                "untame all entities in radius 5 around player")
            .since("INSERT VERSION")
            .register();
    }

    private boolean tame;
    private Expression<LivingEntity> entities;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.tame = matchedPattern == 0;
        this.entities = (Expression<LivingEntity>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        for (LivingEntity livingEntity : this.entities.getArray(ctx)) {
            if (livingEntity instanceof NPCEntity npcEntity) {
                EntityUtils.setTamed(npcEntity, this.tame);
            }
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = this.tame ? "tame" : "untame";
        return type + " " + this.entities.toString(ctx, debug);
    }

}
