package com.github.skriptdev.skript.plugin.elements.expressions.server;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarker;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.worldstore.WorldMarkersResource;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ExprMapMarkerIds implements Expression<String> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprMapMarkerIds.class, String.class, false,
                "map marker ids of %worlds/players%")
            .name("Map Marker Ids")
            .description("Get the IDs of all map markers of a world or player.")
            .examples("set {_ids::*} to map marker ids of player")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<?> owners;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.owners = expressions[0];
        return true;
    }

    @Override
    public String[] getValues(@NotNull TriggerContext ctx) {
        List<String> markers = new ArrayList<>();

        for (Object owner : this.owners.getArray(ctx)) {
            if (owner instanceof World world) {
                markers.addAll(getMarkers(world));
            } else if (owner instanceof Player player) {
                markers.addAll(getMarkers(player));
            }
        }

        return markers.toArray(new String[0]);
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "map marker ids of " + this.owners.toString(ctx, debug);
    }

    private List<String> getMarkers(World world) {
        if (world.isInThread()) {
            Store<ChunkStore> store = world.getChunkStore().getStore();
            WorldMarkersResource worldMarkersResource = store.getResource(WorldMarkersResource.getResourceType());
            return worldMarkersResource.getUserMapMarkers().stream().map(UserMapMarker::getId).toList();
        } else {
            return CompletableFuture.supplyAsync(() -> getMarkers(world), world).join();
        }
    }

    private List<String> getMarkers(Player player) {
        World world = player.getWorld();
        if (world == null) {
            return List.of();
        }
        if (world.isInThread()) {
            PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
            return perWorldData.getUserMapMarkers().stream().map(UserMapMarker::getId).toList();
        } else {
            return CompletableFuture.supplyAsync(() -> getMarkers(player), world).join();
        }
    }

}
