package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.skript.event.CancellableContext;
import com.github.skriptdev.skript.api.skript.event.PlayerContext;
import com.github.skriptdev.skript.api.skript.event.SystemEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent;
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent.Drop;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtPlayerDropItem extends SystemEvent<EntityEventSystem<EntityStore, Drop>> {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerDropItem.class,
                "player drop item", "player dropped item")
            .setHandledContexts(DropItemContext.class)
            .name("Player Drop Item")
            .description("Called when a player drops an item.",
                "This is called before the item is actually dropped but after the player tries to drop.",
                "Cancelling this event will prevent the item from being dropped but the player will lose the item.")
            .since("1.0.0")
            .register();

        reg.addContextValue(DropItemContext.class, Float.class, true, "throw-speed", DropItemContext::getThrowSpeed);
        reg.addContextValue(DropItemContext.class, Item.class, true, "dropped-item", DropItemContext::getItem);
        reg.addContextValue(DropItemContext.class, ItemStack.class, true, "dropped-itemstack", DropItemContext::getItemStack);
    }

    private static PlayerDropItemSystem SYSTEM;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        if (SYSTEM == null) {
            SYSTEM = new PlayerDropItemSystem();
            HySk.getInstance().getEntityStoreRegistry().registerSystem(SYSTEM);
        }
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof DropItemContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "player drop item event";
    }

    private record DropItemContext(Player player, Drop drop) implements PlayerContext, CancellableContext {

        public Player[] getPlayer() {
            return new Player[]{this.player};
        }

        public Float[] getThrowSpeed() {
            return new Float[]{this.drop.getThrowSpeed()};
        }

        public Item[] getItem() {
            return new Item[]{this.drop.getItemStack().getItem()};
        }

        public ItemStack[] getItemStack() {
            return new ItemStack[]{this.drop.getItemStack()};
        }

        @Override
        public boolean isCancelled() {
            return this.drop.isCancelled();
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.drop.setCancelled(cancelled);
        }

        @Override
        public String getName() {
            return "player drop item context";
        }
    }

    private static class PlayerDropItemSystem extends EntityEventSystem<EntityStore, DropItemEvent.Drop> {

        protected PlayerDropItemSystem() {
            super(DropItemEvent.Drop.class);
        }

        @Override
        public void handle(int i, @NotNull ArchetypeChunk<EntityStore> archetypeChunk,
                           @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer,
                           @NotNull Drop drop) {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
            Player player = store.getComponent(ref, Player.getComponentType());
            DropItemContext dropItemContext = new DropItemContext(player, drop);
            TriggerMap.callTriggersByContext(dropItemContext);
        }

        @Override
        public @Nullable Query<EntityStore> getQuery() {
            return Player.getComponentType();
        }
    }

}
