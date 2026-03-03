package com.github.skriptdev.skript.plugin.elements.events.entity;

import com.github.skriptdev.skript.api.skript.event.WorldContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EvtLivingEntityInvChange extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtLivingEntityInvChange.class,
                "living entity inventory change", "living entity inventory changed", "living entity inventory change event")
            .name("Living Entity Inventory Change")
            .description("Called when a living entity's inventory changes.")
            .since("1.0.0")
            .setHandledContexts(InvChangeContext.class)
            .register();

        reg.addSingleContextValue(InvChangeContext.class, Entity.class, "entity", InvChangeContext::getEntity);
        reg.addSingleContextValue(InvChangeContext.class, ItemContainer.class, "item-container", InvChangeContext::getContainer);
        // TODO add transaction
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        return true;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return triggerContext instanceof InvChangeContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "living entity inventory change";
    }

    public record InvChangeContext(LivingEntityInventoryChangeEvent event) implements TriggerContext, WorldContext {

        private Entity getEntity() {
            return this.event.getEntity();
        }

        private ItemContainer getContainer() {
            return this.event.getItemContainer();
        }

        @Override
        public World getWorld() {
            return this.event.getEntity().getWorld();
        }

        @Override
        public String getName() {
            return "living entity inventory change context";
        }
    }

}
