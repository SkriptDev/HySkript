package com.github.skriptdev.skript.plugin.elements.expressions.world;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.api.utils.ReflectionUtils;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;

public class ExprWorldTimeDurations extends PropertyExpression<World, Duration> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprWorldTimeDurations.class, Duration.class,
                "world (daytime|1:nighttime|2:total[ ]time) duration", "worlds")
            .name("World Time Durations")
            .description("Represents the daytime/nighttime durations of a world.",
                "These values are from a GamePlayConfig but can be overridden in your World config.",
                "Set/add/remove/reset will update your world config with these values.")
            .examples("set {_Day} to world daytime duration of world of player",
                "set world daytime duration of {_world} to 1 hour",
                "add 10 minutes to world daytime duration of {_world}",
                "reset world daytime duration of {_world}")
            .since("1.1.0")
            .register();
    }

    private int pattern;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.pattern = parseContext.getNumericMark();
        return super.init(expressions, matchedPattern, parseContext);
    }

    @Override
    public @Nullable Duration getProperty(@NotNull World world) {
        int seconds = switch (this.pattern) {
            case 0 -> world.getDaytimeDurationSeconds();
            case 1 -> world.getNighttimeDurationSeconds();
            case 2 -> world.getDaytimeDurationSeconds() + world.getNighttimeDurationSeconds();
            default -> 0;
        };
        return Duration.ofSeconds(seconds);
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, SET, RESET -> {
                if (this.pattern == 2) {
                    // LOG
                    yield Optional.empty();
                }
                yield CollectionUtils.optionalArrayOf(Duration.class);
            }
            default -> Optional.empty();
        };
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        Duration duration = null;
        if (changeWith != null && changeWith.length > 0 && changeWith[0] instanceof Duration d) {
            duration = d;
        }

        int durationSeconds;
        if (duration == null) {
            durationSeconds = 0;
        } else {
            durationSeconds = (int) duration.toMillis() / 1000;
        }

        boolean daytime = this.pattern == 0;
        for (World world : getOwner().getArray(ctx)) {
            int previons;
            if (daytime) {
                previons = world.getDaytimeDurationSeconds();
            } else {
                previons = world.getNighttimeDurationSeconds();
            }

            Integer seconds = switch (changeMode) {
                case ADD -> previons + durationSeconds;
                case REMOVE -> previons - durationSeconds;
                case SET -> durationSeconds;
                default -> null;
            };

            ReflectionUtils.setWorldTimeOverrides(world, seconds, daytime);
        }
    }

}
