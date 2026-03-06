package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
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
                    "actually return the combined everything ItemContainer of a LivingEntity.",
                "This is essentially a shortcut for the `combined everything item contatiner of` expression.")
            .examples("set {_inv} to inventory of player",
                "clear inventory of player",
                "add itemstack of ingredient_stick to inventory of player")
            .since("1.0.0")
            .register();
    }

    private Expression<LivingEntity> entity;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        parseContext.getLogger().warn("'inventory of %livingentity%' has been deprecated. " +
            "This may still work but will be removed in the future.");
        this.entity = (Expression<LivingEntity>) expressions[0];
        return true;
    }

    @Override
    public ItemContainer[] getValues(@NotNull TriggerContext ctx) {
        List<ItemContainer> containers = new ArrayList<>();
        for (LivingEntity livingEntity : this.entity.getArray(ctx)) {
            Ref<EntityStore> reference = livingEntity.getReference();
            if (reference == null) continue;

            Store<EntityStore> store = reference.getStore();
            CombinedItemContainer combined = InventoryComponent.getCombined(store, reference, InventoryComponent.EVERYTHING);
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
            for (ItemContainer inventory : toChange) {
                for (Object o : changeWith) {
                    if (o instanceof ItemStack itemStack) {
                        inventory.addItemStack(itemStack);
                    }
                }
            }
        } else if (changeMode == ChangeMode.DELETE) {
            for (ItemContainer inventory : toChange) {
                inventory.clear();
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "inventory of " + this.entity.toString(ctx, debug);
    }

}
