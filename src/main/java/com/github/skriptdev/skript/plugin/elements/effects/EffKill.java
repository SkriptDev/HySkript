package com.github.skriptdev.skript.plugin.elements.effects;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EffKill extends Effect {

    public static void register(SkriptRegistration registration) {
        registration.newEffect(EffKill.class, "kill %entities%",
                "kill %entities% with damage cause %damagecause%")
            .name("Kill Entity")
            .description("Kills the specified entities with an optional damage cause.")
            .examples("kill all players",
                "kill all players with damage cause slashing")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<Entity> entities;
    private Expression<DamageCause> damageCause;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.entities = (Expression<Entity>) expressions[0];
        if (matchedPattern == 1) {
            this.damageCause = (Expression<DamageCause>) expressions[1];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        DamageCause cause = DamageCause.COMMAND;
        if (this.damageCause != null) {
            Optional<? extends DamageCause> single = this.damageCause.getSingle(ctx);
            if (single.isPresent()) cause = single.get();
        }
        if (cause == null) cause = DamageCause.COMMAND;

        Damage damage = new Damage(Damage.NULL_SOURCE, cause, 1000.0f);
        for (Entity entity : this.entities.getArray(ctx)) {
            killEntity(entity, damage);
        }
    }

    private void killEntity(Entity entity, Damage damage) {
        World world = entity.getWorld();
        if (world == null) return;

        Store<EntityStore> store = world.getEntityStore().getStore();
        Ref<EntityStore> reference = entity.getReference();
        if (reference == null) return;


        if (store.isInThread()) {
            DeathComponent.tryAddComponent(store, reference, damage);
        } else {
            world.execute(() -> DeathComponent.tryAddComponent(store, reference, damage));
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String cause = this.damageCause != null ? " with damage cause " + this.damageCause.toString(ctx, debug) : "";
        return "kill " + this.entities.toString(ctx, debug) + cause;
    }

}
