package com.github.skriptdev.skript.plugin.elements.types;

import com.github.skriptdev.skript.api.skript.AssetStoreRegistry;
import com.github.skriptdev.skript.api.utils.Utils;
import com.hypixel.hytale.builtin.hytalegenerator.assets.biomes.BiomeAsset;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.role.Role;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.TypeManager;

import java.util.UUID;

public class Types {

    public static void register(SkriptRegistration registration) {
        Utils.log("Setting up Types");
        registerJavaTypes(registration);
        registerServerTypes(registration);
        registerEntityTypes(registration);
        registerItemTypes(registration);
        registerBlockTypes(registration);
        registerWorldTypes(registration);

        TypeManager.register(registration);
    }

    private static void registerJavaTypes(SkriptRegistration registration) {
        registration.newType(UUID.class, "uuid", "uuid@s")
            .name("UUID")
            .description("Represents a UUID.")
            .examples("set {_uuid} to uuid of {_player}")
            .since("INSERT VERSION")
            .register();
    }

    private static void registerServerTypes(SkriptRegistration registration) {
        registration.newType(CommandSender.class, "commandsender", "commandSender@s")
            .name("Command Sender")
            .description("Represents a command sender such as a player or the console.")
            .since("INSERT VERSION")
            .toStringFunction(CommandSender::getDisplayName)
            .register();
        registration.newType(HytaleServer.class, "server", "server@s")
            .name("Server")
            .description("Represents the Hytale server.")
            .since("INSERT VERSION")
            .register();
    }

    private static void registerEntityTypes(SkriptRegistration registration) {
        registration.newType(Role.class, "npcrole", "npcrole@s")
            .name("NPC Role")
            .description("Represents the type of NPCs in the game.")
            .examples("coming soon") // TODO
            .usage(String.join(", ", NPCPlugin.get().getRoleTemplateNames(false).stream().sorted().toList()))
            .since("INSERT VERSION")
            .toStringFunction(Role::getRoleName)
            // TODO figure out parsing (it's a nightmare)
            .register();
        registration.newType(Entity.class, "entity", "entit@y@ies")
            .toStringFunction(Entity::toString) // TODO get its name or something
            .name("Entity")
            .description("Represents any entity in the game, including players and mobs.")
            .since("INSERT VERSION")
            .register();
        registration.newType(LivingEntity.class, "livingentity", "livingEntit@y@ies")
            .name("Living Entity")
            .description("Represents any living entity in the game, including players and mobs.")
            .since("INSERT VERSION")
            .toStringFunction(LivingEntity::toString) // TODO get its name or something
            .register();
        registration.newType(Player.class, "player", "player@s")
            .name("Player")
            .description("Represents a player in the game.")
            .since("INSERT VERSION")
            .toStringFunction(Player::getDisplayName)
            .register();
    }

    private static void registerItemTypes(SkriptRegistration registration) {
        AssetStoreRegistry.register(registration, Item.class, Item.getAssetMap(), "item", "item@s")
            .name("Item")
            .description("Represents the types of items in the game.")
            .examples("set {_i} to itemstack of Food_Fish_Grilled")
            .since("INSERT VERSION")
            .toStringFunction(Item::getId)
            .register();
        registration.newType(ItemStack.class, "itemstack", "itemstack@s")
            .name("Item Stack")
            .description("Represents an item in an inventory slot.")
            .examples("set {_i} to itemstack of Food_Fish_Grilled")
            .since("INSERT VERSION")
            .toStringFunction(itemStack -> {
                String quantity = itemStack.getQuantity() == 1 ? "" : itemStack.getQuantity() + " of ";
                return "itemstack of " + quantity + itemStack.getItem().getId();
            })
            .register();
        registration.newType(Inventory.class, "inventory", "inventor@y@ies")
            .name("Inventory")
            .description("Represents an inventory of an entity or block.")
            .since("INSERT VERSION")
            .toStringFunction(Inventory::toString)
            .register();
    }

    private static void registerBlockTypes(SkriptRegistration registration) {
        AssetStoreRegistry.register(registration, BlockType.class, BlockType.getAssetMap(), "blocktype", "blockType@s")
            .name("BlockType")
            .description("Represents the types of blocks in the game.")
            .examples("set {_block} to blocktype of block at player")
            .since("INSERT VERSION")
            .toStringFunction(BlockType::getId)
            .register();
    }

    private static void registerWorldTypes(SkriptRegistration registration) {
        AssetStoreRegistry.register(registration, BiomeAsset.class, BiomeAsset.getAssetStore().getAssetMap(), "biome", "biome@s")
            .name("Biome")
            .description("Represents the types of biomes in the game.")
            .since("INSERT VERSION")
            .register();
        registration.newType(World.class, "world", "world@s")
            .name("World")
            .description("Represents a world in the game.")
            .since("INSERT VERSION")
            .toStringFunction(World::getName)
            .register();
        AssetStoreRegistry.register(registration, Weather.class, Weather.getAssetMap(), "weather", "weather@s")
            .name("Weather")
            .description("Represents the types of weather in the game.")
            .since("INSERT VERSION")
            .register();
    }

}
