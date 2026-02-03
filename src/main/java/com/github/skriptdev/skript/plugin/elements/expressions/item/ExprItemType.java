package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.hytale.Block;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import org.jetbrains.annotations.Nullable;

public class ExprItemType extends PropertyExpression<Object, Item> {

    public static void register(SkriptRegistration registration) {
        registration.newPropertyExpression(ExprItemType.class, Item.class,
                "item[[ ]type]", "itemstack/blocktype/block")
            .name("Item Type")
            .description("Returns the item type of an ItemStack, BlockType or Block.",
                "If the Block/BlockType doesn't have an associated item, it returns null.")
            .examples("set {_item} to itemtype of {_item}",
                "set {_item} to itemtype of block at player's location")
            .since("1.0.0")
            .register();
    }

    @Override
    public @Nullable Item getProperty(Object owner) {
        return switch (owner) {
            case ItemStack itemStack -> itemStack.getItem();
            case BlockType blockType -> blockType.getItem();
            case Block block -> block.getType().getItem();
            default -> null;
        };
    }

}
