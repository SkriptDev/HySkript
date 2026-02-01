package com.github.skriptdev.skript.plugin.elements.conditions;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.base.ConditionalExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class CondInventoryCanHold extends ConditionalExpression {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(CondInventoryCanHold.class, Boolean.class, true,
                "%inventory/itemcontainer% can hold %itemstacks%",
                "%inventory/itemcontainer% (can't|cannot) hold %itemstacks%")
            .name("Inventory Can Hold")
            .description("Checks if the inventory can hold the given items.")
            .examples("if inventory of player can hold itemstack of ingredient_poop:",
                "if inventory of player can hold {_itemstack}:")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<?> holders;
    private Expression<ItemStack> items;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.holders = expressions[0];
        this.items = (Expression<ItemStack>) expressions[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return this.holders.check(ctx, holder ->
            this.items.check(ctx, item -> {
                if (holder instanceof Inventory inventory) {
                    return inventory.getCombinedEverything().canAddItemStack(item);
                } else if (holder instanceof ItemContainer container) {
                    return container.canAddItemStack(item);
                }
                return false;
            }), isNegated());
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String hold = isNegated() ? "cannot hold" : "can hold";
        return this.holders.toString(ctx, debug) + " " + hold + " " + this.items.toString(ctx, debug);
    }

}
