package com.github.skriptdev.skript.plugin.elements.events.inventory;

import com.github.skriptdev.skript.api.skript.event.PlayerContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.MoveTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MoveType;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.context.ContextValue;
import org.jetbrains.annotations.NotNull;

public class EvtInventoryMove extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtInventoryMove.class, "inventory move",
                "inventory pickup",
                "inventory (put|place) down")
            .name("Inventory Move")
            .description("Called when an item is moved in an inventory window.",
                "**Types**:",
                " - move = Called after an item is moved from one slot to another (fired twice).",
                " - pickup = Called when an item is picked up from the inventory (fired once after the item is put down).",
                " - place down = Called when an item is placed down in the inventory (fired once).")
            .since("INSERT VERSION")
            .setHandledContexts(InventoryMoveContext.class)
            .register();

        reg.newSingleContextValue(InventoryMoveContext.class, ItemStack.class,
                "itemstack", InventoryMoveContext::getSlotBefore)
            .setState(ContextValue.State.PAST)
            .description("Represents the ItemStack in the slot before the action.")
            .register();
        reg.newSingleContextValue(InventoryMoveContext.class, ItemStack.class,
                "itemstack", InventoryMoveContext::getSlotAfter)
            .description("Represents the ItemStack in the slot after the action.")
            .register();
        reg.newSingleContextValue(InventoryMoveContext.class, ItemStack.class,
                "output", InventoryMoveContext::getOutput)
            .register();
        reg.newSingleContextValue(InventoryMoveContext.class, ActionType.class,
                "action", InventoryMoveContext::getActionType)
            .description("Represents the type of action performed in the inventory move.")
            .register();
        reg.newSingleContextValue(InventoryMoveContext.class, Integer.class,
                "slot", InventoryMoveContext::getSlot)
            .description("Represents the slot number involved in the inventory move.")
            .register();
        reg.newSingleContextValue(InventoryMoveContext.class, ItemContainer.class,
                "item-container", InventoryMoveContext::getPastContainer)
            .description("Represents the container before the inventory move.")
            .setState(ContextValue.State.PAST)
            .register();
        reg.newSingleContextValue(InventoryMoveContext.class, ItemContainer.class,
                "item-container", InventoryMoveContext::getContainer)
            .description("Represents the container after the inventory move.")
            .register();
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.pattern = matchedPattern;
        return true;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean check(TriggerContext ctx) {
        if (!(ctx instanceof InventoryMoveContext context)) return false;
        if (this.pattern == 0) {
            return true;
        } else if (this.pattern == 1 && context.transaction.getMoveType() == MoveType.MOVE_FROM_SELF) {
            return true;
        } else if (this.pattern == 2 && context.transaction.getMoveType() == MoveType.MOVE_TO_SELF) {
            return true;
        }

        return false;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return switch (pattern) {
            case 0 -> "inventory move";
            case 1 -> "inventory pickup";
            case 2 -> "inventory put down";
            default -> "unknown";
        };
    }

    public record InventoryMoveContext(LivingEntityInventoryChangeEvent event, MoveTransaction<?> transaction, Player player)
        implements PlayerContext {

        @Override
        public Player getPlayer() {
            return this.player;
        }

        public ItemStack getSlotBefore() {
            if (this.transaction.getMoveType() == MoveType.MOVE_FROM_SELF) {
                return this.transaction.getRemoveTransaction().getSlotBefore();
            } else if (this.transaction.getAddTransaction() instanceof SlotTransaction slotTransaction) {
                return slotTransaction.getSlotBefore();
            }
            return null;
        }

        public ItemStack getSlotAfter() {
            if (this.transaction.getMoveType() == MoveType.MOVE_FROM_SELF) {
                return this.transaction.getRemoveTransaction().getSlotAfter();
            } else if (this.transaction.getAddTransaction() instanceof SlotTransaction slotTransaction) {
                return slotTransaction.getSlotAfter();
            }
            return null;
        }

        public ItemStack getOutput() {
            if (this.transaction.getMoveType() == MoveType.MOVE_FROM_SELF) {
                return this.transaction.getRemoveTransaction().getOutput();
            } else if (this.transaction.getAddTransaction() instanceof SlotTransaction slotTransaction) {
                return slotTransaction.getOutput();
            }
            return null;
        }

        public ActionType getActionType() {
            if (this.transaction.getMoveType() == MoveType.MOVE_FROM_SELF) {
                return this.transaction.getRemoveTransaction().getAction();
            } else if (this.transaction.getAddTransaction() instanceof SlotTransaction slotTransaction) {
                return slotTransaction.getAction();
            }
            return null;
        }

        public int getSlot() {
            if (this.transaction.getMoveType() == MoveType.MOVE_FROM_SELF) {
                return this.transaction.getRemoveTransaction().getSlot();
            } else if (this.transaction.getAddTransaction() instanceof SlotTransaction slotTransaction) {
                return slotTransaction.getSlot();
            }
            return -1;
        }

        public ItemContainer getPastContainer() {
            return this.event.getItemContainer();
        }

        public ItemContainer getContainer() {
            return this.transaction.getOtherContainer();
        }

        @Override
        public String getName() {
            return "inventory move context";
        }
    }

}
