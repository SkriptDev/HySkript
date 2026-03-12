package com.github.skriptdev.skript.plugin.elements.events.inventory;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EvtSlotTransaction extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtSlotTransaction.class,
                "inventory slot transaction")
            .name("Inventory Slot Transaction")
            .description("Called when there is an inventory transaction involving a slot.")
            .since("1.3.0")
            .setHandledContexts(SlotTransactionContext.class)
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof SlotTransactionContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "inventory slot transaction";
    }

    public static class SlotTransactionContext
        extends com.github.skriptdev.skript.api.skript.event.SlotTransactionContext {

        public SlotTransactionContext(LivingEntityInventoryChangeEvent event,
                                      SlotTransaction slotTransaction, Player player) {
            super(event, slotTransaction, player);
        }

        @Override
        public String getName() {
            return "slot transaction context";
        }
    }

}
