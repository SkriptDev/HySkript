package com.github.skriptdev.skript.plugin.elements.events.inventory;

import com.github.skriptdev.skript.api.skript.event.SlotTransactionContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EvtItemStackSlotTransaction extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtItemStackSlotTransaction.class,
                "inventory item[stack] slot transaction")
            .name("Inventory Item Slot Transaction")
            .description("Called when there is an inventory transaction involving an ItemStack in a slot.")
            .since("1.3.0")
            .setHandledContexts(ItemStackSlotContext.class)
            .register();

        reg.newSingleContextValue(ItemStackSlotContext.class, ItemStack.class,
                "output", ItemStackSlotContext::getOutput)
            .register();

        reg.newSingleContextValue(ItemStackSlotContext.class, ItemStack.class,
                "remainder", ItemStackSlotContext::getRemainder)
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof ItemStackSlotContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "inventory item slot transaction";
    }

    public static class ItemStackSlotContext extends SlotTransactionContext {

        private final ItemStackSlotTransaction transaction;

        ItemStackSlotContext(LivingEntityInventoryChangeEvent event,
                             ItemStackSlotTransaction transaction,
                             Player player) {
            super(event, transaction, player);
            this.transaction = transaction;
        }

        public ItemStack getQuery() {
            return this.transaction.getQuery();
        }

        public ItemStack getRemainder() {
            return this.transaction.getRemainder();
        }

        @Override
        public String getName() {
            return "itemstack slot context";
        }
    }

}
