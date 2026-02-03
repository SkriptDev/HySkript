package com.github.skriptdev.skript.plugin.elements.expressions.other;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.vector.Location;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExprDistance implements Expression<Number> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprDistance.class, Number.class, true,
                "distance between %location% and %location%")
            .name("Distance Between Locations")
            .description("Returns the distance between two locations.")
            .examples("set {_distance} to distance between player's location and location of target block")
            .since("1.0.0")
            .register();
    }

    private Expression<Location> loc1, loc2;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.loc1 = (Expression<Location>) expressions[0];
        this.loc2 = (Expression<Location>) expressions[1];
        return true;
    }

    @Override
    public Number[] getValues(@NotNull TriggerContext ctx) {
        Optional<? extends Location> single = this.loc1.getSingle(ctx);
        Optional<? extends Location> single2 = this.loc2.getSingle(ctx);
        if (single.isEmpty() || single2.isEmpty()) return null;
        Location loc1 = single.get();
        Location loc2 = single2.get();
        return new Number[]{loc1.getPosition().distanceTo(loc2.getPosition())};
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "distance between " + this.loc1.toString(ctx, debug) + " and " + this.loc2.toString(ctx, debug);
    }

}
