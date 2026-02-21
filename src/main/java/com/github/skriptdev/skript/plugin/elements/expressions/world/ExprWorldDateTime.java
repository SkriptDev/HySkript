package com.github.skriptdev.skript.plugin.elements.expressions.world;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.SkriptDate;
import io.github.syst3ms.skriptparser.util.Time;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.TimeZone;

public class ExprWorldDateTime implements Expression<Object> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprWorldDateTime.class, Object.class, true,
                "world (:time|date) (in|of) %world%")
            .name("World Date/Time")
            .description("Get/set the date or time of a world.",
                "You can also add/remove a duration to the time/date.")
            .examples("set {_date} to world date of world of player",
                "set {_time} to world time of world of player",
                "set world time of world of player to 12:00 pm",
                "add 3 hours to world time of world of player",
                "add 1 year to world date of world of player")
            .since("1.1.0")
            .register();
    }

    private boolean isTime;
    private Expression<World> world;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.world = (Expression<World>) expressions[0];
        this.isTime = parseContext.hasMark("time");
        return true;
    }

    @Override
    public Object[] getValues(@NotNull TriggerContext ctx) {
        World world = this.world.getSingle(ctx).orElse(null);
        if (world == null) return null;

        WorldTimeResource worldTimeResource = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
        Instant gameTime = worldTimeResource.getGameTime();

        LocalDateTime gameDateTime = worldTimeResource.getGameDateTime();

        if (this.isTime) {
            return new Time[]{Time.of(gameDateTime.toLocalTime())};
        } else {
            SkriptDate skriptDate = SkriptDate.of(gameTime.toEpochMilli(), TimeZone.getTimeZone(ZoneId.systemDefault()));
            return new SkriptDate[]{skriptDate};
        }
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (this.isTime) return Optional.of(new Class<?>[]{Time.class});
            return Optional.of(new Class<?>[]{SkriptDate.class});
        } else if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            return Optional.of(new Class<?>[]{Duration.class});
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0) return;

        World world = this.world.getSingle(ctx).orElse(null);
        if (world == null) return;

        Store<EntityStore> store = world.getEntityStore().getStore();
        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());


        if (changeMode == ChangeMode.SET) {
            if (this.isTime && changeWith[0] instanceof Time time) {
                double dayTime = (double) time.toMillis() / 1000 / 60 / 60 / 24;
                worldTimeResource.setDayTime(dayTime, world, store);
            } else if (!this.isTime && changeWith[0] instanceof SkriptDate date) {
                LocalDateTime localDateTime = date.toLocalDateTime();
                Instant instant = Instant.ofEpochMilli(localDateTime
                    .toInstant(ZoneOffset.UTC.getRules().getOffset(localDateTime))
                    .toEpochMilli());
                worldTimeResource.setGameTime(instant, world, store);
            }
        } else if (changeMode == ChangeMode.ADD || changeMode == ChangeMode.REMOVE) {
            if (changeWith[0] instanceof Duration duration) {
                Instant newTime;
                if (changeMode == ChangeMode.ADD) {
                    newTime = worldTimeResource.getGameTime().plus(duration);
                } else {
                    newTime = worldTimeResource.getGameTime().minus(duration);
                }
                worldTimeResource.setGameTime(newTime, world, store);
            }
        }
    }

    @Override
    public Class<?> getReturnType() {
        if (this.isTime) return Time.class;
        return SkriptDate.class;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = this.isTime ? "time" : "date";
        return "world " + type + " of " + this.world.toString(ctx, debug);
    }

}
