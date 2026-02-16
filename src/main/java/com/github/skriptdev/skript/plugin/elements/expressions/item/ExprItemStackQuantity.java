package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprItemStackQuantity extends PropertyExpression<ItemStack, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprItemStackQuantity.class, Number.class,
                "(item[stack] quantity|[item]stack size)", "itemstacks")
            .name("ItemStack Quantity")
            .description("Get the quanity/stack size of an ItemStack.",
                "This cannot be set (Yes, Silly... I know!).")
            .examples("set {_amount} to stack size of {_itmstack}",
                "if item quanity of {_itme} > 1:")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public @Nullable Number getProperty(@NotNull ItemStack itemStack) {
        return itemStack.getQuantity();
    }

}
