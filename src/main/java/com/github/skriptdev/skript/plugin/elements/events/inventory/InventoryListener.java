package com.github.skriptdev.skript.plugin.elements.events.inventory;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.elements.events.entity.EvtLivingEntityInvChange.InvChangeContext;
import com.github.skriptdev.skript.plugin.elements.events.inventory.EvtInventoryMove.InventoryMoveContext;
import com.github.skriptdev.skript.plugin.elements.events.inventory.EvtItemStackSlotTransaction.ItemStackSlotContext;
import com.github.skriptdev.skript.plugin.elements.events.inventory.EvtItemStackTransaction.ItemStackTransactionContext;
import com.github.skriptdev.skript.plugin.elements.events.inventory.EvtSlotTransaction.SlotTransactionContext;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MoveTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import io.github.syst3ms.skriptparser.lang.TriggerMap;

public class InventoryListener {

    public static void registerListener(SkriptRegistration reg) {
        EventRegistry eventRegistry = reg.getSkript().getPlugin().getEventRegistry();

        eventRegistry.registerGlobal(LivingEntityInventoryChangeEvent.class, event -> {
            InvChangeContext ctx = new InvChangeContext(event);
            TriggerMap.callTriggersByContext(ctx);

            if (event.getEntity() instanceof Player player) {
                Transaction transaction = event.getTransaction();

                if (transaction instanceof MoveTransaction<?> move) {
                    InventoryMoveContext context = new InventoryMoveContext(event, move, player);
                    TriggerMap.callTriggersByContext(context);
                } else if (transaction instanceof ItemStackSlotTransaction slot) {
                    ItemStackSlotContext context = new ItemStackSlotContext(event, slot, player);
                    TriggerMap.callTriggersByContext(context);
                } else if (transaction instanceof ItemStackTransaction stack) {
                    ItemStackTransactionContext context = new ItemStackTransactionContext(event, stack, player);
                    TriggerMap.callTriggersByContext(context);
                } else if (transaction instanceof SlotTransaction slotTransaction) {
                    SlotTransactionContext context = new SlotTransactionContext(event, slotTransaction, player);
                    TriggerMap.callTriggersByContext(context);
                }
            }
        });
    }

}
