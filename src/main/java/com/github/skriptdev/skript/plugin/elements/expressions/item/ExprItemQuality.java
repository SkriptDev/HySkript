package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprItemQuality extends PropertyExpression<Object, ItemQuality> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprItemQuality.class, ItemQuality.class,
                "item quality", "items/itemstacks")
            .name("Item Quality")
            .description("Returns the quality of an Item/ItemStack.")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public @Nullable ItemQuality getProperty(@NotNull Object owner) {
        int index;
        if (owner instanceof ItemStack itemStack) {
            index = itemStack.getItem().getQualityIndex();
        } else if (owner instanceof Item item) {
            index = item.getQualityIndex();
        } else {
            return null;
        }
        return ItemQuality.getAssetMap().getAsset(index);
    }

}
