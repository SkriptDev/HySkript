package com.github.skriptdev.skript.api.skript.event;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;

public abstract class SlotTransactionContext implements PlayerContext {

    private final LivingEntityInventoryChangeEvent event;
    private final SlotTransaction slotTransaction;
    private final Player player;

    public SlotTransactionContext(LivingEntityInventoryChangeEvent event,
                                  SlotTransaction slotTransaction, Player player) {
        this.event = event;
        this.slotTransaction = slotTransaction;
        this.player = player;
    }

    public ItemContainer getContainer() {
        return this.event.getItemContainer();
    }

    public ActionType getActionType() {
        return this.slotTransaction.getAction();
    }

    public int getSlot() {
        return this.slotTransaction.getSlot();
    }

    public ItemStack getSlotBefore() {
        return this.slotTransaction.getSlotBefore();
    }

    public ItemStack getSlotAfter() {
        return this.slotTransaction.getSlotAfter();
    }

    public ItemStack getOutput() {
        return this.slotTransaction.getOutput();
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

}
