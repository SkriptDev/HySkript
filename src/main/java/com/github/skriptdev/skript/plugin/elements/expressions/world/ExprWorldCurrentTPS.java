package com.github.skriptdev.skript.plugin.elements.expressions.world;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class ExprWorldCurrentTPS implements Expression<Number> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprWorldCurrentTPS.class, Number.class, true,
                "current tps of %world%",
                "current tps of %world% for the last (10|ten) seconds",
                "current tps of %world% for the last [(1|one) ]minute",
                "current tps of %world% for the last (5|five) minutes")
            .name("World Current TPS")
            .description("Represents the active TPS of a world (how well the world is ticking).")
            .examples("if current tps of world of player < 20:")
            .since("1.1.0")
            .register();
    }

    private final int[] timeStates = new int[]{10, 60, (60 * 5)};
    private int pattern;
    private Expression<World> worlds;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.pattern = matchedPattern;
        this.worlds = (Expression<World>) expressions[0];
        return true;
    }

    @Override
    public Number[] getValues(@NotNull TriggerContext ctx) {
        int pattern = this.pattern > 0 ? this.pattern - 1 : this.pattern;
        World world = this.worlds.getSingle(ctx).orElse(null);
        if (world == null) return null;

        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        double length = metrics.getTimestamps(pattern).length;

        double tps;
        if (length == 0) {
            tps = world.getTps();
        } else {
            tps = length / this.timeStates[pattern];
        }
        return new Number[]{tps};
    }


    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = switch (this.pattern) {
            case 1 -> " for the last 10 seconds";
            case 2 -> " for the last 1 minute";
            case 3 -> " for the last 5 minutes";
            default -> "";
        };
        return "current tps of " + this.worlds.toString(ctx, debug) + type;
    }

}
