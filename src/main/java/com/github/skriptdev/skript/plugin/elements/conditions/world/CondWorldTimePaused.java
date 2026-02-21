package com.github.skriptdev.skript.plugin.elements.conditions.world;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.base.ConditionalExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CondWorldTimePaused extends ConditionalExpression {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(CondWorldTimePaused.class, Boolean.class, true,
            "(world|game)[ ]time of %world% is paused",
                "(world|game)[ ]time of %world% is(n't| not) paused")
            .name("World Time Paused")
            .description("Check if the game time of a world is paused. Can be set.")
            .examples("set gametime of world(\"default\") is paused to false",
                "if game time of world of player is paused:",
                "\tset gametime of world of player is paused to false")
            .since("1.1.0")
            .register();

    }

    private Expression<World> world;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        setNegated(matchedPattern == 1);
        this.world = (Expression<World>) expressions[0];
        return true;
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return this.world.check(ctx, world -> world.getWorldConfig().isGameTimePaused());
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.optionalArrayOf(Boolean.class);
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Boolean bool)) {
            return;
        }

        boolean paused = isNegated() != bool;

        for (World world : this.world.getArray(ctx)) {
            WorldConfig worldConfig = world.getWorldConfig();
            worldConfig.setGameTimePaused(paused);
            worldConfig.markChanged();
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = isNegated() ? "is not" : "is";
        return "game time of " + this.world.toString(ctx,debug) + " " + type + " paused";
    }

}
