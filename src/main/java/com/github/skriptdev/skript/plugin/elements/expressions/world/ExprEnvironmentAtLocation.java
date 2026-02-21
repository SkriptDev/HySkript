package com.github.skriptdev.skript.plugin.elements.expressions.world;

import com.github.skriptdev.skript.api.hytale.utils.AssetStoreUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExprEnvironmentAtLocation implements Expression<Environment> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprEnvironmentAtLocation.class, Environment.class, true,
                "environment at %location%",
                "environment[s] within %location% and %location%")
            .name("Environment at Location")
            .description("Get/set the environment at a location or within a cuboid of 2 locations.")
            .examples("set environment at location of player to Env_Zone1_Forests",
                "set environment within {_loc1} and {_loc2} to Env_Zone3_Tundra")
            .since("1.1.0")
            .register();
    }

    private Expression<Location> loc1, loc2;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.loc1 = (Expression<Location>) expressions[0];
        if (matchedPattern == 1) {
            this.loc2 = (Expression<Location>) expressions[1];
        }
        return true;
    }

    @Override
    public Environment[] getValues(@NotNull TriggerContext ctx) {
        Location loc1 = this.loc1.getSingle(ctx).orElse(null);
        if (loc1 == null) return null;

        World world = Universe.get().getWorld(loc1.getWorld());
        if (world == null) return null;

        if (this.loc2 != null) {
            Location loc2 = this.loc2.getSingle(ctx).orElse(null);
            if (loc2 == null) return null;

            Vector3i pos1 = loc1.getPosition().toVector3i();
            Vector3i pos2 = loc2.getPosition().toVector3i();

            Vector3i posLow = Vector3i.min(pos1, pos2);
            Vector3i posHigh = Vector3i.max(pos1, pos2);

            List<Environment> environments = new ArrayList<>();
            for (int x = posLow.getX(); x < posHigh.getX(); x++) {
                for (int z = posLow.getZ(); x < posHigh.getZ(); x++) {
                    long chunkIndex = ChunkUtil.indexChunk(x, z);

                    for (int y = posLow.getY(); x < posHigh.getY(); x++) {
                        WorldChunk chunk = world.getChunk(chunkIndex);
                        if (chunk == null) continue;

                        BlockChunk blockChunk = chunk.getBlockChunk();
                        if (blockChunk == null) continue;

                        int id = blockChunk.getEnvironment(x, y, z);
                        environments.add(AssetStoreUtils.getEnvironment(id));
                    }
                }
            }

            return environments.toArray(new Environment[0]);
        } else {

            BlockChunk blockChunk = getBlockChunk(loc1);
            if (blockChunk == null) return null;

            Vector3i position = loc1.getPosition().toVector3i();
            int id = blockChunk.getEnvironment(position);
            return new Environment[]{AssetStoreUtils.getEnvironment(id)};
        }
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) return Optional.of(new Class<?>[]{Environment.class});
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Environment env)) return;

        Location loc1 = this.loc1.getSingle(ctx).orElse(null);
        if (loc1 == null) return;

        World world = Universe.get().getWorld(loc1.getWorld());
        assert world != null;

        int index = AssetStoreUtils.getEnvironmentIndex(env);

        if (this.loc2 != null) {

            Location loc2 = this.loc2.getSingle(ctx).orElse(null);
            if (loc2 == null) return;

            Vector3i pos1 = loc1.getPosition().toVector3i();
            Vector3i pos2 = loc2.getPosition().toVector3i();

            Vector3i posLow = Vector3i.min(pos1, pos2);
            Vector3i posHigh = Vector3i.max(pos1, pos2);

            List<Long> chunkIndexes = new ArrayList<>();
            for (int x = posLow.getX(); x < posHigh.getX(); x++) {
                for (int z = posLow.getZ(); z < posHigh.getZ(); z++) {
                    long chunkIndex = ChunkUtil.indexChunk(x, z);
                    if (!chunkIndexes.contains(chunkIndex)) {
                        chunkIndexes.add(chunkIndex);
                    }
                    for (int y = posLow.getY(); y < posHigh.getY(); y++) {
                        WorldChunk chunk = world.getChunk(chunkIndex);
                        if (chunk == null) continue;

                        BlockChunk blockChunk = chunk.getBlockChunk();
                        if (blockChunk == null) continue;

                        blockChunk.setEnvironment(x, y, z, index);
                    }
                }
            }

            for (Long chunkIndex : chunkIndexes) {
                world.getNotificationHandler().updateChunk(chunkIndex);
            }
        } else {
            BlockChunk blockChunk = getBlockChunk(loc1);
            if (blockChunk == null) return;

            Vector3i position = loc1.getPosition().toVector3i();

            int x = position.getX();
            int y = position.getY();
            int z = position.getZ();

            blockChunk.setEnvironment(x, y, z, index);

            long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
            world.getNotificationHandler().updateChunk(chunkIndex);
        }
    }

    private BlockChunk getBlockChunk(Location location) {
        String worldName = location.getWorld();
        World world = Universe.get().getWorld(worldName);
        if (world == null) return null;

        Vector3i position = location.getPosition().toVector3i();

        WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(position.getX(), position.getZ()));
        if (chunk == null) return null;

        return chunk.getBlockChunk();
    }

    @Override
    public boolean isSingle() {
        return this.loc2 == null;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        if (this.loc2 != null) {
            return "environment within " + this.loc1.toString(ctx, debug) + " and " + this.loc2.toString(ctx, debug);
        }
        return "environment at " + this.loc1.toString(ctx, debug);
    }

}
