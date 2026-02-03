package com.github.skriptdev.skript.plugin.elements.expressions.world;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class ExprChunkAtLocation implements Expression<WorldChunk> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprChunkAtLocation.class, WorldChunk.class, true,
                "chunk[s] at %locations%")
            .name("Chunk At Location")
            .description("Get the chunk at a location.")
            .examples("set {_chunk} to chunk at player's location",
                "regenerate chunk at player's location")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<Location> locations;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.locations = (Expression<Location>) expressions[0];
        return true;
    }

    @Override
    public WorldChunk[] getValues(@NotNull TriggerContext ctx) {
        Location[] locArray = this.locations.getArray(ctx);
        WorldChunk[] chunks = new WorldChunk[locArray.length];

        for (int i = 0; i < locArray.length; i++) {
            Location location = locArray[i];
            String worldName = location.getWorld();
            if (worldName == null) continue;

            World world = Universe.get().getWorld(worldName);
            if (world == null) continue;

            Vector3i pos = location.getPosition().toVector3i();

            long index = ChunkUtil.indexChunkFromBlock(pos.getX(), pos.getZ());
            chunks[i] = world.getChunk(index);
        }

        return chunks;
    }

    @Override
    public boolean isSingle() {
        return this.locations.isSingle();
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "chunk[s] at " + this.locations.toString(ctx, debug);
    }

}
