package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.skript.event.CancellableContext;
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
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtPlayerDropItemRequest extends SystemEvent<EntityEventSystem<EntityStore, DropItemEvent.PlayerRequest>> {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerDropItemRequest.class,
                "player drop item request", "player request to drop item")
            .setHandledContexts(RequestDropItemContext.class)
            .name("Player Drop Item Request")
            .description("Called when a player tries to drops an item.",
                "This event is called before the drop happens.",
                "Cancelling this event will prevent the item from being dropped and the player will keep the item.")
            .since("1.0.0")
            .register();

        reg.addContextValue(RequestDropItemContext.class, Player.class, true, "player", RequestDropItemContext::getPlayer);
        reg.addContextValue(RequestDropItemContext.class, Integer.class, true, "slot-id", RequestDropItemContext::getSlotId);
        reg.addContextValue(RequestDropItemContext.class, Integer.class, true, "inventory-section-id", RequestDropItemContext::getInventorySectionId);
        reg.addContextValue(RequestDropItemContext.class, Item.class, true, "item", RequestDropItemContext::getItem);
        reg.addContextValue(RequestDropItemContext.class, ItemStack.class, true, "itemstack", RequestDropItemContext::getItemStack);
    }

    private static PlayerRequestSystem SYSTEM;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        if (SYSTEM == null) {
            SYSTEM = new PlayerRequestSystem();
            HySk.getInstance().getEntityStoreRegistry().registerSystem(SYSTEM);
        }
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof RequestDropItemContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "player request drop item event";
    }

    private record RequestDropItemContext(Player player, DropItemEvent.PlayerRequest request)
        implements TriggerContext, CancellableContext {

        public Player[] getPlayer() {
            return new Player[]{this.player};
        }

        public Integer[] getSlotId() {
            return new Integer[]{(int) this.request.getSlotId()};
        }

        public Integer[] getInventorySectionId() {
            return new Integer[]{(int) this.request.getInventorySectionId()};
        }

        public Item[] getItem() {
            Inventory inventory = this.player.getInventory();
            ItemContainer container = inventory.getSectionById(this.request.getInventorySectionId());
            if (container == null) return null;
            ItemStack itemStack = container.getItemStack(this.request.getSlotId());
            if (itemStack == null) return null;
            Item item = itemStack.getItem();
            return new Item[]{item};
        }

        public ItemStack[] getItemStack() {
            Inventory inventory = this.player.getInventory();
            ItemContainer container = inventory.getSectionById(this.request.getInventorySectionId());
            if (container == null) return null;
            return new ItemStack[]{container.getItemStack(this.request.getSlotId())};
        }

        @Override
        public boolean isCancelled() {
            return this.request.isCancelled();
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.request.setCancelled(cancelled);
        }

        @Override
        public String getName() {
            return "player request drop item context";
        }
    }

    private static class PlayerRequestSystem extends EntityEventSystem<EntityStore, DropItemEvent.PlayerRequest> {

        protected PlayerRequestSystem() {
            super(DropItemEvent.PlayerRequest.class);
        }

        @Override
        public void handle(int i, @NotNull ArchetypeChunk<EntityStore> archetypeChunk,
                           @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer,
                           @NotNull DropItemEvent.PlayerRequest playerRequest) {

            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
            Player player = store.getComponent(ref, Player.getComponentType());
            RequestDropItemContext requestDropItemContext = new RequestDropItemContext(player, playerRequest);
            TriggerMap.callTriggersByContext(requestDropItemContext);
        }

        @Override
        public @Nullable Query<EntityStore> getQuery() {
            return Player.getComponentType();
        }
    }

}
