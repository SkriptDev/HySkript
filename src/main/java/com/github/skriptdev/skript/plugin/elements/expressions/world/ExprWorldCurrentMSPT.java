package com.github.skriptdev.skript.plugin.elements.expressions.world;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ExprWorldCurrentMSPT implements Expression<Number> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprWorldCurrentMSPT.class, Number.class, true,
                "current mspt of %world%",
                "current mspt of %world% for the last (10|ten) seconds",
                "current mspt of %world% for the last [(1|one) ]minute",
                "current mspt of %world% for the last (5|five) minutes")
            .name("World Current MSPT")
            .description("Represents the active MSPT (milliseconds per tick) of a world.",
                "This would be how long it's taking each tick to process.")
            .examples("if current mspt of world of player > 20:")
            .since("1.1.0")
            .register();
    }

    private static final double NANOS_PER_MILLI = TimeUnit.MILLISECONDS.toNanos(1);
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

        double total = 0;

        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        double avgNanos = metrics.getAverage(pattern);

        if (avgNanos > 0) {
            total = avgNanos / NANOS_PER_MILLI;
        }

        return new Number[]{total};
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = switch (this.pattern) {
            case 1 -> " for the last 10 seconds";
            case 2 -> " for the last 1 minute";
            case 3 -> " for the last 5 minutes";
            default -> "";
        };
        return "current mspt of " + this.worlds.toString(ctx, debug) + type;
    }

}
