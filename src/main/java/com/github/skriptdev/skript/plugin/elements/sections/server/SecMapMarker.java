package com.github.skriptdev.skript.plugin.elements.sections.server;

import com.github.skriptdev.skript.api.hytale.objects.UserMapMarkerOverride;
import com.github.skriptdev.skript.api.hytale.utils.PlayerUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.protocol.packets.worldmap.ContextMenuItem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarker;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.worldstore.WorldMarkersResource;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.lang.CodeSection;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.entries.SectionConfiguration;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.util.color.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SecMapMarker extends CodeSection {

    public static void register(SkriptRegistration reg) {
        reg.newSection(SecMapMarker.class, "create map marker")
            .description("Creates a map marker for a world/player.",
                "**Entries**:",
                " - `id` = The id of the map marker. Must be unique (required).",
                " - `name` = The name of the map marker [optional].",
                " - `icon` = The icon which will be seen on the map (required).",
                " - `location` = The location of the marker (required).",
                " - `tint` = The color of the marker [optional].",
                " - `player` = The player to create the map marker for [optional].",
                "   - If a player is used, the marker will only be visible to that player, and it will save in their player config.",
                "   - If no player is used, the marker will be visible to all players in the world, and it will save in the world config.",
                " - `context_menu_items` = The context menu items to show when right clicking the marker [optional list].",
                " - `created_by` = The player who created the marker [optional, will allow the player to delete it].",
                "",
                "**Icons**:",
                " - Icons are found in the Assets file under `Common/UI/WorldMap/MapMarkers`.",
                " - Current icons: Campfire, Coordinate, Death, Home, OutsideViewMarker, Player, PlayerAbove, PlayerBelow, " +
                    "Portal, PortalInvasion, Prefab, Spawn, Temple_Gateway, UserA, UserB, UserC, UserD, UserE, UserF, Warp.",
                " - Custom icons are supported.")
            .examples("create map marker:",
                "\tid: \"le_map_marker\"",
                "\ticon: \"Campfire.png\"",
                "\tlocation: location of player",
                "\tname: \"Warming Spot!\"",
                "\ttint: color from hex \"#f0750a\"")
            .since("1.3.0")
            .register();
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        return true;
    }

    SectionConfiguration config = new SectionConfiguration.Builder()
        .addExpression("id", String.class, false)
        .addOptionalExpression("name", String.class, false)
        .addExpression("icon", String.class, false)
        .addExpression("location", Location.class, false)
        .addOptionalExpression("tint", Color.class, false)
        .addOptionalExpression("player", Player.class, false)
        .addOptionalExpression("context_menu_items", ContextMenuItem.class, true)
        .addOptionalExpression("created_by", Player.class, false)
        .build();

    @Override
    public boolean loadSection(@NotNull FileSection section, @NotNull ParserState parserState, @NotNull SkriptLogger logger) {
        return this.config.loadConfiguration(null, section, parserState, logger);
    }

    @Override
    public Optional<? extends Statement> walk(@NotNull TriggerContext ctx) {
        Optional<? extends Statement> nextStatement = getNext();

        // EXPRESSIONS
        Expression<String> idExpr = this.config.getExpression("id", String.class).orElse(null);
        if (idExpr == null) return nextStatement;

        Expression<String> nameExpr = this.config.getExpression("name", String.class).orElse(null);

        Expression<String> iconExpr = this.config.getExpression("icon", String.class).orElse(null);
        if (iconExpr == null) return nextStatement;

        Expression<Location> locationExpr = this.config.getExpression("location", Location.class).orElse(null);
        if (locationExpr == null) return nextStatement;

        Expression<Color> tintExpr = this.config.getExpression("tint", Color.class).orElse(null);

        Expression<Player> playerExpr = this.config.getExpression("player", Player.class).orElse(null);

        Expression<ContextMenuItem> contextExpr = this.config.getExpression("context_menu_items", ContextMenuItem.class).orElse(null);

        Expression<Player> createdByExpr = this.config.getExpression("created_by", Player.class).orElse(null);

        // OBJECTS
        String id = idExpr.getSingle(ctx).orElse(null);
        if (id == null) return nextStatement;

        String name = nameExpr != null ? nameExpr.getSingle(ctx).orElse(null) : null;

        String icon = iconExpr.getSingle(ctx).orElse(null);
        if (icon == null) return nextStatement;

        if (!icon.endsWith(".png")) {
            icon += ".png";
        }

        Location location = locationExpr.getSingle(ctx).orElse(null);
        if (location == null) return nextStatement;

        World world = Universe.get().getWorld(location.getWorld());
        if (world == null) return nextStatement;

        UserMapMarkerOverride userMapMarker = new UserMapMarkerOverride();
        userMapMarker.setId(id);
        userMapMarker.setName(name);
        userMapMarker.setIcon(icon);
        userMapMarker.setPosition(location.getPosition());

        if (contextExpr != null) {
            for (ContextMenuItem contextMenuItem : contextExpr.getArray(ctx)) {
                userMapMarker.addContextMenuItem(contextMenuItem);
            }
        }
        if (createdByExpr != null) {
            Player player = createdByExpr.getSingle(ctx).orElse(null);
            if (player != null) {
                PlayerRef playerRef = PlayerUtils.getPlayerRef(player);
                if (playerRef != null) {
                    userMapMarker.withCreatedByName(playerRef.getUsername());
                    userMapMarker.withCreatedByUuid(playerRef.getUuid());
                }
            }
        }

        if (tintExpr != null) {
            Color color = tintExpr.getSingle(ctx).orElse(null);
            if (color != null) {
                com.hypixel.hytale.protocol.Color hytaleColor = new com.hypixel.hytale.protocol.Color(
                    (byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
                userMapMarker.setColorTint(hytaleColor);
            }
        }
        if (playerExpr != null) {
            Player player = playerExpr.getSingle(ctx).orElse(null);
            if (player != null) {
                PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
                UserMapMarker oldMarker = perWorldData.getUserMapMarker(id);
                if (oldMarker != null) {
                    perWorldData.removeUserMapMarker(id);
                }
                perWorldData.addUserMapMarker(userMapMarker);
            }
        } else {
            Store<ChunkStore> store = world.getChunkStore().getStore();
            WorldMarkersResource worldMarkersResource = store.getResource(WorldMarkersResource.getResourceType());
            UserMapMarker oldMarker = worldMarkersResource.getUserMapMarker(id);
            if (oldMarker != null) {
                worldMarkersResource.removeUserMapMarker(id);
            }
            worldMarkersResource.addUserMapMarker(userMapMarker);
        }

        return nextStatement;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "create map marker";
    }

}
