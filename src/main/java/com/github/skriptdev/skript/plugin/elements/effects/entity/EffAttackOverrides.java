package com.github.skriptdev.skript.plugin.elements.effects.entity;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.support.CombatSupport;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EffAttackOverrides extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffAttackOverrides.class,
                "apply attack override %interaction% to %npcentities%",
                "clear attack overrides of %npcentities%")
            .name("NPC Attack Overrides")
            .description("Applies an attack override to NPCs or clears their attack overrides.")
            .examples("apply attack override Gun_Shoot to {_e}",
                "clear attack overrides of {_e}")
            .since("1.3.0")
            .register();
    }

    private Expression<Interaction> interaction;
    private Expression<NPCEntity> npcs;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        if (matchedPattern == 0) {
            this.interaction = (Expression<Interaction>) expressions[0];
            this.npcs = (Expression<NPCEntity>) expressions[1];
        } else {
            this.npcs = (Expression<NPCEntity>) expressions[0];
        }
        return true;
    }


    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        Interaction interaction = null;
        if (this.interaction != null) {
            interaction = this.interaction.getSingle(ctx).orElse(null);
        }

        for (NPCEntity npcEntity : this.npcs.getArray(ctx)) {
            Role role = npcEntity.getRole();
            assert role != null;

            Ref<EntityStore> npcRef = npcEntity.getReference();
            if (npcRef == null) continue;

            CombatSupport combatSupport = CombatSupport.get(npcRef, npcRef.getStore());
            if (interaction == null) {
                combatSupport.clearAttackOverrides();
            } else {
                combatSupport.addAttackOverride(interaction.getId());
            }
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        if (this.interaction != null) {
            return "apply attack override " + this.interaction.toString(ctx, debug) + " to " + this.npcs.toString(ctx, debug);
        }
        return "clear attack overrides of " + this.npcs.toString(ctx, debug);
    }

}
