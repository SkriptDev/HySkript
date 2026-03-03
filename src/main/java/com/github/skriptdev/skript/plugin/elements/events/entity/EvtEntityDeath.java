package com.github.skriptdev.skript.plugin.elements.events.entity;

import com.github.skriptdev.skript.api.skript.event.WorldContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.MessageUtil;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.context.ContextValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EvtEntityDeath extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtEntityDeath.class, "death", "death of player", "death of npc")
            .setHandledContexts(EntityDeathContext.class)
            .name("Entity Death")
            .description("Called when an entity dies.",
                "**Patterns**:",
                "- `on death` = Death of all entities (players and NPCs).",
                "- `on death of player` = Death of players only.",
                "- `on death of npc` = Death of NPCs only.")
            .examples("on death of player:",
                "\tbroadcast \"Poor %context-victim%\" has died!",
                "",
                "on death of player:",
                "\tset {lost::%uuid of context-victim%} to context-lost-itemstacks")
            .since("1.0.0")
            .register();

        reg.newSingleContextValue(EntityDeathContext.class,
                Entity.class, "victim", EntityDeathContext::getVictim)
            .description("The entity that died.")
            .setUsage(ContextValue.Usage.EXPRESSION_OR_ALONE)
            .register();
        reg.newSingleContextValue(EntityDeathContext.class,
                Entity.class, "attacker", EntityDeathContext::getAttacker)
            .description("The entity that killed the victim.")
            .setUsage(ContextValue.Usage.EXPRESSION_OR_ALONE)
            .register();
        reg.addSingleContextValue(EntityDeathContext.class,
            Damage.Source.class, "damage-source", EntityDeathContext::getDamageSource);
        reg.addSingleContextValue(EntityDeathContext.class,
            DamageCause.class, "death-cause", EntityDeathContext::getDamageCause);
        reg.addSingleContextValue(EntityDeathContext.class,
            Damage.class, "death-info", EntityDeathContext::getDamage);
        reg.newListContextValue(EntityDeathContext.class,
                Item.class, "lost-items", EntityDeathContext::getItemsLostOnDeath)
            .description("The Item types of the ItemStacks lost on death.")
            .register();
        reg.newListContextValue(EntityDeathContext.class,
                ItemStack.class, "lost-itemstacks", EntityDeathContext::getItemStacksLostOnDeath)
            .setUsage(ContextValue.Usage.EXPRESSION_OR_ALONE)
            .description("The ItemStacks lost on death.")
            .addListSetter(EntityDeathContext::setItemStacksLostOnDeath)
            .register();
        reg.newSingleContextValue(EntityDeathContext.class,
                Boolean.class, "show-death-menu", EntityDeathContext::isShowDeathMenu)
            .description("Whether the death menu should be shown to the player. " +
                "Disabling this will respawn the player.")
            .setUsage(ContextValue.Usage.EXPRESSION_OR_ALONE)
            .addSetter(EntityDeathContext::setShowDeathMenu)
            .register();
        reg.newSingleContextValue(EntityDeathContext.class,
                Message.class, "death-message", EntityDeathContext::getDeathMessage)
            .setUsage(ContextValue.Usage.EXPRESSION_OR_ALONE)
            .addSetter(EntityDeathContext::setDeathMessage)
            .register();
        reg.newSingleContextValue(EntityDeathContext.class,
                String.class, "death-message-string", EntityDeathContext::getDeathMessageString)
            .setUsage(ContextValue.Usage.EXPRESSION_OR_ALONE)
            .addSetter(EntityDeathContext::setDeathMessageString)
            .register();
    }

    private static EntityDeathListener LISTENER;

    private int pattern;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        if (LISTENER == null) {
            LISTENER = new EntityDeathListener();
            HySk.getInstance().getEntityStoreRegistry().registerSystem(LISTENER);
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

        @Override
        public @NotNull Set<Dependency<EntityStore>> getDependencies() {
            return Collections.singleton(RootDependency.first());
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        public void onComponentAdded(@NotNull Ref<EntityStore> ref, @NotNull DeathComponent deathComponent,
                                     @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> buffer) {
            NPCEntity npc = buffer.getComponent(ref, NPCEntity.getComponentType());
            Player player = buffer.getComponent(ref, Player.getComponentType());

            int pattern;
            Entity victim;
            if (player != null) {
                pattern = 1;
                victim = player;
            } else if (npc != null) {
                pattern = 2;
                victim = npc;
            } else {
                return;
            }

            TriggerMap.callTriggersByContext(new EntityDeathContext(pattern, victim, deathComponent));
        }

        @Override
        public @Nullable Query<EntityStore> getQuery() {
            return super.componentType();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private static class EntityDeathContext implements TriggerContext, WorldContext {

        private final int pattern;
        private final Entity victim;
        private final DeathComponent component;
        private final OverridingDamageSource damageSource;

        EntityDeathContext(int pattern, Entity victim, DeathComponent component) {
            this.pattern = pattern;
            this.victim = victim;
            this.component = component;
            this.damageSource = new OverridingDamageSource(component.getDeathInfo().getSource());
            this.component.getDeathInfo().setSource(this.damageSource);
        }

        public Entity getVictim() {
            return this.victim;
        }

        public Entity getAttacker() {
            return this.damageSource.getAttacker();
        }

        public Damage.Source getDamageSource() {
            return this.damageSource.parentSource;
        }

        public DamageCause getDamageCause() {
            return this.component.getDeathCause();
        }

        public Damage getDamage() {
            return this.component.getDeathInfo();
        }

        public Item[] getItemsLostOnDeath() {
            return Arrays.stream(this.component.getItemsLostOnDeath()).map(ItemStack::getItem).toArray(Item[]::new);
        }

        public ItemStack[] getItemStacksLostOnDeath() {
            return this.component.getItemsLostOnDeath();
        }

        public void setItemStacksLostOnDeath(ItemStack[] itemStacks) {
            this.component.setItemsLostOnDeath(List.of(itemStacks));
        }

        public boolean isShowDeathMenu() {
            return this.component.isShowDeathMenu();
        }

        public void setShowDeathMenu(boolean showDeathMenu) {
            this.component.setShowDeathMenu(showDeathMenu);
            // We have to respawn the player or they get locked in a death pose
            if (!showDeathMenu && this.getVictim() instanceof Player player) {
                Ref<EntityStore> reference = player.getReference();
                DeathComponent.respawn(reference.getStore(), reference);
            }
        }

        public Message getDeathMessage() {
            return this.component.getDeathMessage();
        }

        public void setDeathMessage(Message deathMessage) {
            this.component.setDeathMessage(deathMessage);
            this.damageSource.setDeathMessage(deathMessage);
        }

        public String getDeathMessageString() {
            return MessageUtil.toAnsiString(this.component.getDeathMessage()).toAnsi();
        }

        public void setDeathMessageString(String deathMessage) {
            setDeathMessage(Message.raw(deathMessage));
        }

        @Override
        public World getWorld() {
            return this.victim.getWorld();
        }

        @Override
        public String getName() {
            return "entity-death-context";
        }
    }

    public static class OverridingDamageSource implements Damage.Source {

        private final Damage.Source parentSource;
        private Message deathMessage;

        public OverridingDamageSource(Damage.Source parentSource) {
            this.parentSource = parentSource;
        }

        @Override
        public @NotNull Message getDeathMessage(@NotNull Damage info, @NotNull Ref<EntityStore> targetRef, @NotNull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.deathMessage != null) return this.deathMessage;
            return this.parentSource.getDeathMessage(info, targetRef, componentAccessor);
        }

        public void setDeathMessage(Message deathMessage) {
            this.deathMessage = deathMessage;
        }

        @SuppressWarnings("DataFlowIssue")
        public Entity getAttacker() {
            Entity attacker = null;
            if (this.parentSource instanceof Damage.EntitySource entitySource) {
                Ref<EntityStore> attackerRef = entitySource.getRef();
                Store<EntityStore> store = attackerRef.getStore();
                Player player = store.getComponent(attackerRef, Player.getComponentType());
                if (player != null) {
                    attacker = player;
                } else {
                    NPCEntity npc = store.getComponent(attackerRef, NPCEntity.getComponentType());
                    if (npc != null) attacker = npc;
                }
            }
            return attacker;
        }

    }

}
