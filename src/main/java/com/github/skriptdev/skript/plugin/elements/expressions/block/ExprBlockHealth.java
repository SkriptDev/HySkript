package com.github.skriptdev.skript.plugin.elements.expressions.block;

import com.github.skriptdev.skript.api.hytale.Block;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExprBlockHealth implements Expression<Number> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprBlockHealth.class, Number.class, true,
                "block health of %blocks%")
            .name("Block Health")
            .description("Get/set the health of a block.",
                "The values range from 0 to 1")
            .examples("set block health of target block of player to 0.5",
                "if block health of {_block} > 0:")
            .since("1.1.0")
            .register();
    }

    private Expression<Block> blocks;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.blocks = (Expression<Block>) expressions[0];
        return true;
    }

    @Override
    public Number[] getValues(@NotNull TriggerContext ctx) {
        List<Number> health = new ArrayList<>();

        for (Block block : this.blocks.getArray(ctx)) {
            health.add(block.getBlockHealth());
        }

        return health.toArray(Number[]::new);
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) return Optional.of(new Class<?>[]{Number.class});
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Number num)) return;

        for (Block block : this.blocks.getArray(ctx)) {
            block.setBlockHealth(num.floatValue());
        }
    }

    @Override
    public boolean isSingle() {
        return this.blocks.isSingle();
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "block health of " + this.blocks.toString(ctx, debug);
    }

}
