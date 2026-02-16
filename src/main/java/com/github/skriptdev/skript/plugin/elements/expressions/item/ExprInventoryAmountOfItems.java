package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExprInventoryAmountOfItems implements Expression<Number> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprInventoryAmountOfItems.class, Number.class, true,
                "item amount of %items/itemstacks% in %inventory/itemcontainer%")
            .name("Inventory Item Count")
            .description("Get the amount of a certain Item/ItemStack in an Inventory/ItemContainer.",
                "If you pass in an Item, it will compare the Item types of ItemStacks in the inventory.",
                "If you pass in an ItemStack, it will direct compare that the ItemStacks are the same " +
                    "(ie: same durability, max durability, metadata, etc), excluding stack size.")
            .examples("set {_amount} to item amount of ingredient_stick in inventory of player")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<?> items;
    private Expression<?> container;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.items = expressions[0];
        this.container = expressions[1];
        return true;
    }

    @Override
    public Number[] getValues(@NotNull TriggerContext ctx) {
        ItemContainer container;

        Object o = this.container.getSingle(ctx).orElse(null);
        if (o instanceof ItemContainer c) {
            container = c;
        } else if (o instanceof Inventory inv) {
            container = inv.getCombinedEverything();
        } else {
            return null;
        }

        List<Object> itemStacks = new ArrayList<>();
        for (Object object : this.items.getArray(ctx)) {
            if (object instanceof ItemStack itemStack) {
                itemStacks.add(itemStack);
            } else if (object instanceof Item item) {
                itemStacks.add(item);
            }
        }
        if (itemStacks.isEmpty()) {
            return new Number[]{0};
        }

        int count = container.countItemStacks(itemStack -> {
            for (Object obj : itemStacks) {
                if (obj instanceof ItemStack stack && stack.isStackableWith(itemStack)) {
                    return true;
                } else if (obj instanceof Item item && itemStack.getItem().getId().equals(item.getId())) {
                    return true;
                }
            }
            return false;
        });

        return new Number[]{count};
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "item amount of " + this.items.toString(ctx, debug) +
            " in " + this.container.toString(ctx, debug);
    }

}
