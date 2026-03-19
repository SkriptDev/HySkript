package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Armor;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Backpack;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Hotbar;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Storage;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Tool;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Utility;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExprItemContainer implements Expression<ItemContainer> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprItemContainer.class, ItemContainer.class, true,
                "storage [item] container of %inventory/livingentity%",
                "armor [item] container of %inventory/livingentity%",
                "hot[ ]bar [item] container of %inventory/livingentity%",
                "utility [item] container of %inventory/livingentity%",
                "tools [item] container of %inventory/livingentity%",
                "backpack [item] container of %inventory/livingentity%",
                "combined everything [item] container of %inventory/livingentity%",
                "combined hotbar first [item] container of %inventory/livingentity%",
                "combined storage first [item] container of %inventory/livingentity%",
                "combined backpack storage hotbar [item] container of %inventory/livingentity%")
            .name("Item Container of LivingEntity")
            .description("Returns different item containers of an Inventory/LivingEntity.",
                "Theres also a few combined options (These are from Hytale).",
                "**The inventory option has been deprecated and will be removed in a future version.**")
            .since("1.0.0")
            .register();
    }

    private int pattern;
    private Expression<?> inventory;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.pattern = matchedPattern;
        this.inventory = expressions[0];
        return true;
    }

    @Override
    public ItemContainer[] getValues(@NotNull TriggerContext ctx) {
        Optional<?> single = this.inventory.getSingle(ctx);
        if (single.isEmpty()) return null;

        Object object = single.get();

        ItemContainer container = null;
        if (object instanceof Inventory inventory) {
            container = switch (this.pattern) {
                case 0 -> inventory.getStorage();
                case 1 -> inventory.getArmor();
                case 2 -> inventory.getHotbar();
                case 3 -> inventory.getUtility();
                case 4 -> inventory.getTools();
                case 5 -> inventory.getBackpack();
                //case 6 -> inventory.getCombinedEverything();
                case 7 -> inventory.getCombinedHotbarFirst();
                case 8 -> inventory.getCombinedStorageFirst();
                case 9 -> inventory.getCombinedBackpackStorageHotbar();
                default -> null;
            };
        } else if (object instanceof LivingEntity livingEntity) {
            Ref<EntityStore> reference = livingEntity.getReference();
            if (reference == null) return null;

            Store<EntityStore> store = reference.getStore();
            Object o = switch (this.pattern) {
                case 0 -> store.getComponent(reference, Storage.getComponentType());
                case 1 -> store.getComponent(reference, Armor.getComponentType());
                case 2 -> store.getComponent(reference, Hotbar.getComponentType());
                case 3 -> store.getComponent(reference, Utility.getComponentType());
                case 4 -> store.getComponent(reference, Tool.getComponentType());
                case 5 -> store.getComponent(reference, Backpack.getComponentType());
                case 6 -> InventoryComponent.getCombined(store, reference, InventoryComponent.EVERYTHING);
                case 7 -> InventoryComponent.getCombined(store, reference, InventoryComponent.HOTBAR_FIRST);
                case 8 -> InventoryComponent.getCombined(store, reference, InventoryComponent.STORAGE_FIRST);
                case 9 -> InventoryComponent.getCombined(store, reference, InventoryComponent.BACKPACK_STORAGE_HOTBAR);
                default -> null;
            };
            if (o instanceof InventoryComponent inventoryComponent) {
                container = inventoryComponent.getInventory();
            } else if (o instanceof ItemContainer itemContainer) {
                container = itemContainer;
            } else {
                return null;
            }
        }
        return new ItemContainer[]{container};
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = switch (this.pattern) {
            case 0 -> "storage";
            case 1 -> "armor";
            case 2 -> "hotbar";
            case 3 -> "utility";
            case 4 -> "tools";
            case 5 -> "backpack";
            case 6 -> "combined everything";
            case 7 -> "combined hotbar first";
            case 8 -> "combined storage first";
            case 9 -> "combined backpack storage hotbar";
            default -> "unknown";
        };
        return type + " item container of " + this.inventory.toString(ctx, debug);
    }

}
