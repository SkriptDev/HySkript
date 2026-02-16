package com.github.skriptdev.skript.plugin.elements.expressions.other;

import com.github.skriptdev.skript.api.hytale.utils.LocationUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class ExprLocationOffset implements Expression<Location> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprLocationOffset.class, Location.class, true,
                "%location% offset by %vector3i/vector3d%",
                "%location% ~ %vector3i/vector3d%")
            .name("Location Offset")
            .description("Returns a location offset by a vector.")
            .examples("teleport player to (location of player offset by vector3i(0, 10, 0) # Teleports them 10 blocks up",
                "set {_loc} to {_loc} ~ vector3d(0, -1.5, 0)")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<Location> location;
    private Expression<?> vector;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.location = (Expression<Location>) expressions[0];
        this.vector = expressions[1];
        return true;
    }

    @Override
    public Location[] getValues(@NotNull TriggerContext ctx) {

        Location loc = this.location.getSingle(ctx).orElse(null);
        if (loc == null) return null;

        Vector3d offset;
        Object o = this.vector.getSingle(ctx).orElse(null);
        if (o instanceof Vector3i vector3i) {
            offset = vector3i.toVector3d();
        } else if (o instanceof Vector3d vector3d) {
            offset = vector3d;
        } else {
            return null;
        }

        Location location = LocationUtils.clone(loc);
        location.getPosition().add(offset);

        return new Location[]{location};
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return this.location.toString(ctx, debug) + " offset by " +
            this.vector.toString(ctx, debug);
    }

}
