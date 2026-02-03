package com.github.skriptdev.skript.plugin.elements.expressions.block;

import com.github.skriptdev.skript.api.hytale.Block;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.iterator.LineIterator;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExprBlockIterator implements Expression<Block> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprBlockIterator.class, Block.class, false,
                "blocks within %location% and %location%",
                "blocks between %location% and %location%")
            .name("Block Iterator")
            .description("Get all blocks within (cuboid) or between (straight line) two locations.")
            .examples("loop blocks within {_loc1} and {_loc2}:",
                "\tset blocktype of loop-value to rock_stone")
            .since("INSERT VERSION")
            .register();
    }

    private boolean within;
    private Expression<Location> loc1, loc2;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.within = matchedPattern == 0;
        this.loc1 = (Expression<Location>) expressions[0];
        this.loc2 = (Expression<Location>) expressions[1];
        return true;
    }

    @Override
    public Block[] getValues(@NotNull TriggerContext ctx) {
        List<Block> blocks = new ArrayList<>();
        iterator(ctx).forEachRemaining(blocks::add);
        return blocks.toArray(Block[]::new);
    }

    @SuppressWarnings("RedundantOperationOnEmptyContainer")
    @Override
    public Iterator<? extends Block> iterator(@NotNull TriggerContext ctx) {
        List<Block> blocks = new ArrayList<>();

        Location loc1 = this.loc1.getSingle(ctx).orElse(null);
        Location loc2 = this.loc2.getSingle(ctx).orElse(null);
        if (loc1 == null || loc2 == null) return blocks.iterator();

        String worldString = loc1.getWorld();
        if (worldString == null) return blocks.iterator();

        World world = Universe.get().getWorld(worldString);
        if (world == null) return blocks.iterator();

        Vector3i pos1 = loc1.getPosition().toVector3i();
        Vector3i pos2 = loc2.getPosition().toVector3i();

        if (this.within) {
            Vector3i min = Vector3i.min(pos1, pos2);
            Vector3i max = Vector3i.max(pos1, pos2);
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int y = min.getY(); y <= max.getY(); y++) {
                    for (int z = min.getZ(); z <= max.getZ(); z++) {
                        Block block = new Block(world, new Vector3i(x, y, z));
                        blocks.add(block);
                    }
                }
            }
            return blocks.iterator();
        } else {
            return new BlockLineIterator(world, pos1, pos2);
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = this.within ? " within " : " between ";
        return "Blocks" + type + this.loc1.toString(ctx, debug) + " and " + this.loc2.toString(ctx, debug);
    }

    private static class BlockLineIterator implements Iterator<Block> {

        private final World world;
        private final LineIterator lineIterator;

        public BlockLineIterator(World world, Vector3i pos1, Vector3i pos2) {
            this.world = world;
            this.lineIterator = new LineIterator(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
        }

        @Override
        public boolean hasNext() {
            return this.lineIterator.hasNext();
        }

        @Override
        public Block next() {
            Vector3i pos = this.lineIterator.next();
            return new Block(world, pos);
        }
    }

}
