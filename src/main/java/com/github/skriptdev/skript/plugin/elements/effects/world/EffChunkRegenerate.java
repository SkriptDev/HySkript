package com.github.skriptdev.skript.plugin.elements.effects.world;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EffChunkRegenerate extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffChunkRegenerate.class,
                "regenerate %chunks%")
            .name("Chunk Regenerate")
            .description("Regenerates the specified chunks.")
            .examples("regenerate chunk at player's location")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<WorldChunk> chunks;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.chunks = (Expression<WorldChunk>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        for (WorldChunk worldChunk : this.chunks.getArray(ctx)) {
            World world = worldChunk.getWorld();
            Runnable runnable = () -> world.getChunkStore().getChunkReferenceAsync(worldChunk.getIndex(), 9);
            if (world.isInThread()) {
                runnable.run();
            } else {
                world.execute(runnable);
            }
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "regenerate " + this.chunks.toString(ctx, debug);
    }

}
