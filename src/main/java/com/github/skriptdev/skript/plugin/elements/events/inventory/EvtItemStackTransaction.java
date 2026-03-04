package com.github.skriptdev.skript.plugin.elements.events.inventory;

import com.github.skriptdev.skript.api.skript.event.PlayerContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EvtItemStackTransaction extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtItemStackTransaction.class, "inventory item[stack] transaction")
            .name("Inventory ItemStack Transaction")
            .description("Called when there is an inventory transaction involving an ItemStack.")
            .since("1.3.0")
            .setHandledContexts(ItemStackTransactionContext.class)
            .register();

        reg.newSingleContextValue(ItemStackTransactionContext.class, ItemContainer.class,
                "item-container", ItemStackTransactionContext::getContainer)
            .register();
        reg.newSingleContextValue(ItemStackTransactionContext.class, ActionType.class,
                "action-type", ItemStackTransactionContext::getActionType)
            .register();
        reg.newSingleContextValue(ItemStackTransactionContext.class, ItemStack.class,
                "query", ItemStackTransactionContext::getQuery)
            .register();
        reg.newSingleContextValue(ItemStackTransactionContext.class, ItemStack.class,
                "remainder", ItemStackTransactionContext::getRemainder)
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof ItemStackTransactionContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "inventory itemstack transaction";
    }

    public record ItemStackTransactionContext(LivingEntityInventoryChangeEvent event,
                                              ItemStackTransaction transaction,
                                              Player player) implements PlayerContext {

        public ItemContainer getContainer() {
            return this.event.getItemContainer();
        }

        public ActionType getActionType() {
            return this.transaction.getAction();
        }

        public ItemStack getQuery() {
            return this.transaction.getQuery();
        }

        public ItemStack getRemainder() {
            return this.transaction.getRemainder();
        }

        @Override
        public Player getPlayer() {
            return this.player;
        }

        @Override
        public String getName() {
            return "itemstack transaction context";
        }
    }

}
