package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Armor;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Backpack;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Hotbar;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Storage;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Tool;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Utility;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExprInventory implements Expression<ItemContainer> {

    public static void register(SkriptRegistration registration) {
        registration.newExpression(ExprInventory.class, ItemContainer.class, false,
                "inventory of %livingentities%")
            .name("Inventory of LivingEntity")
            .description("While it is called \"inventory\" it will " +
                    "actually return the combined hotbar/storage ItemContainers of a LivingEntity.",
                "This is essentially a shortcut for the `combined hotbar first item contatiner of` expression.",
                "Clearing will clear all ItemContainers, not just the hotbar/storage.")
            .examples("set {_inv} to inventory of player",
                "clear inventory of player",
                "add itemstack of ingredient_stick to inventory of player")
            .since("1.0.0")
            .register();
    }

    private Expression<LivingEntity> entities;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.entities = (Expression<LivingEntity>) expressions[0];
        return true;
    }

    @Override
    public ItemContainer[] getValues(@NotNull TriggerContext ctx) {
        List<ItemContainer> containers = new ArrayList<>();
        for (LivingEntity livingEntity : this.entities.getArray(ctx)) {
            Ref<EntityStore> ref = livingEntity.getReference();
            if (ref == null) continue;

            Store<EntityStore> store = ref.getStore();
            CombinedItemContainer combined = InventoryComponent.getCombined(store, ref, InventoryComponent.HOTBAR_FIRST);
            containers.add(combined);
        }
        return containers.toArray(new ItemContainer[0]);
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.ADD || mode == ChangeMode.DELETE) {
            return Optional.of(new Class<?>[]{ItemStack[].class});
        }
        return Optional.empty();
    }

    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        ItemContainer[] toChange = getValues(ctx);
        if (changeMode == ChangeMode.ADD) {
            for (ItemContainer container : toChange) {
                for (Object o : changeWith) {
                    if (o instanceof ItemStack itemStack) {
                        container.addItemStack(itemStack);
                    }
                }
            }
        } else if (changeMode == ChangeMode.DELETE) {
            for (LivingEntity livingEntity : this.entities.getArray(ctx)) {
                // Combined storage clearing is currently broken
                Storage storage = EntityUtils.getComponent(livingEntity, Storage.getComponentType());
                if (storage != null) {
                    storage.getInventory().clear();
                }
                Hotbar hotbar = EntityUtils.getComponent(livingEntity, Hotbar.getComponentType());
                if (hotbar != null) {
                    hotbar.getInventory().clear();
                }
                Armor armor = EntityUtils.getComponent(livingEntity, Armor.getComponentType());
                if (armor != null) {
                    armor.getInventory().clear();
                }
                Backpack backpack = EntityUtils.getComponent(livingEntity, Backpack.getComponentType());
                if (backpack != null) {
                    backpack.getInventory().clear();
                }
                Utility utility = EntityUtils.getComponent(livingEntity, Utility.getComponentType());
                if (utility != null) {
                    utility.getInventory().clear();
                }
                Tool tool = EntityUtils.getComponent(livingEntity, Tool.getComponentType());
                if (tool != null) {
                    tool.getInventory().clear();
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "inventory of " + this.entities.toString(ctx, debug);
    }

}
