package com.github.skriptdev.skript.api.hytale.utils;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Quick utility methods for working with AssetStore
 */
public class AssetStoreUtils {

    /**
     * Get a BlockType from ID
     *
     * @param blockId ID of the BlockType
     * @return BlockType from ID if found, otherwise null
     */
    public static @Nullable BlockType getBlockType(@NotNull String blockId) {
        return BlockType.getAssetMap().getAsset(blockId);
    }

    /**
     * Get a BlockType from ItemStack
     *
     * @param itemStack ItemStack to get BlockType from
     * @return BlockType from ItemStack if found, otherwise null
     */
    public static @Nullable BlockType getBlockType(@NotNull ItemStack itemStack) {
        return getBlockType(itemStack.getItem());
    }

    /**
     * Get a BlockType from Item
     *
     * @param item Item to get BlockType from
     * @return BlockType from Item if found, otherwise null
     */
    public static @Nullable BlockType getBlockType(@NotNull Item item) {
        if (item.hasBlockType()) return getBlockType(item.getBlockId());
        return null;
    }

    /**
     * Get the AssetStore index of an Environment
     *
     * @param environment Environment to get index from
     * @return Index from Environment
     */
    public static int getEnvironmentIndex(Environment environment) {
        return Environment.getAssetMap().getIndex(environment.getId());
    }

    /**
     * Get an Environment from its AssetStore index.
     *
     * @param index Index to grab Environment from
     * @return Environment from index, or null if not found
     */
    public static Environment getEnvironment(int index) {
        return Environment.getAssetMap().getAsset(index);
    }

}
