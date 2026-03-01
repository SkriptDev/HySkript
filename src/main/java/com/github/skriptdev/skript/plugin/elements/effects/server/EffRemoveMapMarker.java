package com.github.skriptdev.skript.plugin.elements.effects.server;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.worldstore.WorldMarkersResource;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffRemoveMapMarker extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffRemoveMapMarker.class,
                "remove map marker with id[s] %strings% (of|from) %worlds/players%",
                "remove all map markers (of|from) %worlds/players%")
            .name("Remove Map Markers")
            .description("Removes map markers from worlds/players.",
                "You can specify multiple ids to remove multiple markers at once or remove all markers from a world/player.")
            .examples("remove map marker with id \"marker1\" from event-world",
                "remove map markers with ids \"marker1\", \"marker2\" from player",
                "remove all map markers from world world(\"default\")")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<String> ids;
    private Expression<?> owners;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        if (matchedPattern == 0) {
            this.ids = (Expression<String>) expressions[0];
            this.owners = expressions[1];
        } else {
            this.owners = expressions[0];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        List<String> markers = this.ids != null ? new ArrayList<>(Arrays.asList(this.ids.getArray(ctx))) : null;

        for (Object owner : this.owners.getArray(ctx)) {
            if (owner instanceof World world) {
                Runnable worldRunnable = () -> {
                    Store<ChunkStore> store = world.getChunkStore().getStore();
                    WorldMarkersResource worldMarkersResource = store.getResource(WorldMarkersResource.getResourceType());
                    if (markers == null) {
                        worldMarkersResource.setUserMapMarkers(null);
                    } else {
                        markers.forEach(worldMarkersResource::removeUserMapMarker);
                    }
                };
                if (world.isInThread()) {
                    worldRunnable.run();
                } else {
                    world.execute(worldRunnable);
                }
            } else if (owner instanceof Player player) {
                World world = player.getWorld();
                assert world != null;
                Runnable worldRunnable = () -> {
                    PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
                    if (markers == null) {
                        perWorldData.setUserMapMarkers(null);
                    } else {
                        markers.forEach(perWorldData::removeUserMapMarker);
                    }
                };
                if (world.isInThread()) {
                    worldRunnable.run();
                } else {
                    world.execute(worldRunnable);
                }
            }
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        if (this.ids == null) {
            return "remove all map markers from " + this.owners.toString(ctx, debug);
        }
        return "remove map markers with id " + this.ids.toString(ctx, debug) + " from " + this.owners.toString(ctx, debug);
    }

}

