package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class ExprItemStackWithQuantity implements Expression<ItemStack> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprItemStackWithQuantity.class, ItemStack.class, true,
                "%itemstack% with quantity %number%")
            .name("ItemStack with Quantity")
            .description("Returns a copy ItemStack with the new quantity.")
            .examples("set {_item} to held item of player with quanity 10")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<ItemStack> itemStack;
    private Expression<Number> quantity;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.itemStack = (Expression<ItemStack>) expressions[0];
        this.quantity = (Expression<Number>) expressions[1];
        return true;
    }

    @Override
    public ItemStack[] getValues(@NotNull TriggerContext ctx) {
        ItemStack itemStack = this.itemStack.getSingle(ctx).orElse(null);
        Number number = this.quantity.getSingle(ctx).orElse(null);
        if (itemStack == null || number == null) return null;

        return new ItemStack[]{itemStack.withQuantity(number.intValue())};
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return this.itemStack.toString(ctx, debug) + " with quantity " + this.quantity.toString(ctx, debug);
    }

}
