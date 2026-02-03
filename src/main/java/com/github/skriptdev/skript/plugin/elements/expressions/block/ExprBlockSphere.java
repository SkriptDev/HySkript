package com.github.skriptdev.skript.plugin.elements.expressions.block;

import com.github.skriptdev.skript.api.hytale.Block;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.block.BlockSphereUtil;
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

public class ExprBlockSphere implements Expression<Block> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprBlockSphere.class, Block.class, false,
                "blocks in radius %number% (of|around) %location%")
            .name("Block Sphere")
            .description("Get all blocks within a sphere of a given radius around a location.")
            .examples("set {_blocks::*} to blocks in radius 5 of player's location",
                "loop blocks in radius 5 of player's location:",
                "\tset blocktype of loop-block to empty")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<Number> radius;
    private Expression<Location> loc;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.radius = (Expression<Number>) expressions[0];
        this.loc = (Expression<Location>) expressions[1];
        return true;
    }

    @Override
    public Block[] getValues(@NotNull TriggerContext ctx) {
        List<Block> blocks = new ArrayList<>();
        iterator(ctx).forEachRemaining(blocks::add);
        return blocks.toArray(Block[]::new);
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "blocks in radius " + this.radius.toString(ctx, debug) + " of " + this.loc.toString(ctx, debug);
    }

    @Override
    public Iterator<? extends Block> iterator(TriggerContext ctx) {
        List<Block> blocks = new ArrayList<>();

        Location location = this.loc.getSingle(ctx).orElse(null);
        if (location == null) return blocks.iterator();
        Number number = this.radius.getSingle(ctx).orElse(null);
        if (number == null) return blocks.iterator();
        int radius = number.intValue();

        String worldName = location.getWorld();
        if (worldName == null) return blocks.iterator();
        World world = Universe.get().getWorld(worldName);
        if (world == null) return blocks.iterator();

        Vector3i pos = location.getPosition().toVector3i();

        BlockSphereUtil.forEachBlock(pos.getX(), pos.getY(), pos.getZ(),
            radius, blocks, (i, i1, i2, blocks1) -> {
                blocks1.add(new Block(world, new Vector3i(i, i1, i2)));
                return true;
            });

        return blocks.iterator();
    }

}
