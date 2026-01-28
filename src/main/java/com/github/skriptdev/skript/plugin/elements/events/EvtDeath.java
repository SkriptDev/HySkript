package com.github.skriptdev.skript.plugin.elements.events;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtDeath extends SkriptEvent {

    public static void register(SkriptRegistration registration) {
        registration.newEvent(EvtDeath.class, "death", "death of player", "death of npc")
            .setHandledContexts(EntityDeathContext.class)
            .name("Death Event")
            .description("Called when an entity dies.",
                "**Patterns**:",
                "- `on death` = Death of all entities (players and NPCs).",
                "- `on death of player` = Death of players only.",
                "- `on death of npc` = Death of NPCs only.")
            .examples("on death of player:",
                "\tbroadcast \"Poor %context-victim%\" has died!",
                "",
                "on death of player:",
                "\tset {lost::%uuid of context-victim%} to context-lost-items")
            .since("INSERT VERSION")
            .register();

        registration.addContextValue(EntityDeathContext.class,
            Entity.class, true, "victim", EntityDeathContext::getVictim);
        registration.addContextValue(EntityDeathContext.class,
            DamageCause.class, true, "death-cause", EntityDeathContext::getDamageCause);
        registration.addContextValue(EntityDeathContext.class,
            Damage.class, true, "death-info", EntityDeathContext::getDamage);
        registration.addContextValue(EntityDeathContext.class,
            ItemStack.class, false, "lost-items", EntityDeathContext::getItemsLostOnDeath);
    }

    private static EntityDeathListener LISTENER;

    private int pattern;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        if (LISTENER == null) {
            ComponentRegistryProxy<EntityStore> entityStoreRegistry = HySk.getInstance().getEntityStoreRegistry();
            LISTENER = new EntityDeathListener(entityStoreRegistry, this);
        }
        this.pattern = matchedPattern;
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        if (ctx instanceof EntityDeathContext deathContext) {
            if (this.pattern == 0) return true;
            return deathContext.pattern == this.pattern;
        }

        return false;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "entity death";
    }

    private static class EntityDeathListener extends DeathSystems.OnDeathSystem {

        private final EvtDeath event;

        public EntityDeathListener(ComponentRegistryProxy<EntityStore> registry, EvtDeath event) {
            this.event = event;
            registry.registerSystem(this);
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        public void onComponentAdded(@NotNull Ref<EntityStore> ref, @NotNull DeathComponent deathComponent, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> buffer) {
            NPCEntity npc = buffer.getComponent(ref, NPCEntity.getComponentType());
            Player player = buffer.getComponent(ref, Player.getComponentType());
            DamageCause damageCause = deathComponent.getDeathCause();
            Damage damage = deathComponent.getDeathInfo();
            ItemStack[] itemsLostOnDeath = deathComponent.getItemsLostOnDeath();

            int pattern;
            Entity victim;
            if (player != null) {
                pattern = 1;
                victim = player;
            } else if (npc != null) {
                pattern = 2;
                victim = npc;
            } else return;

            for (Trigger trigger : this.event.getTriggers()) {
                Statement.runAll(trigger, new EntityDeathContext(pattern, victim, damageCause, damage, itemsLostOnDeath));
            }
        }

        @Override
        public @Nullable Query<EntityStore> getQuery() {
            return super.componentType();
        }
    }

    private static class EntityDeathContext implements TriggerContext {

        private final int pattern;
        private final Entity victim;
        private final DamageCause damageCause;
        private final Damage damage;
        private final ItemStack[] itemsLostOnDeath;

        public EntityDeathContext(int pattern, Entity victim, DamageCause damageCause, Damage damage, ItemStack[] itemsLostOnDeath) {
            this.pattern = pattern;
            this.victim = victim;
            this.damageCause = damageCause;
            this.damage = damage;
            this.itemsLostOnDeath = itemsLostOnDeath;
        }

        public Entity[] getVictim() {
            return new Entity[]{this.victim};
        }

        public DamageCause[] getDamageCause() {
            return new DamageCause[]{this.damageCause};
        }

        public Damage[] getDamage() {
            return new Damage[]{this.damage};
        }

        public ItemStack[] getItemsLostOnDeath() {
            return this.itemsLostOnDeath;
        }

        @Override
        public String getName() {
            return "entity-death-context";
        }
    }

}
