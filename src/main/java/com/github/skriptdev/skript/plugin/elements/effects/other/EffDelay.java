package com.github.skriptdev.skript.plugin.elements.effects.other;

import com.github.skriptdev.skript.api.skript.event.PlayerContext;
import com.github.skriptdev.skript.api.skript.event.WorldContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.api.utils.Utils;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.DurationUtils;
import io.github.syst3ms.skriptparser.variables.VariableMap;
import io.github.syst3ms.skriptparser.variables.Variables;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EffDelay extends Effect {

    public static void register(SkriptRegistration registration) {
        World defaultWorld = Universe.get().getDefaultWorld();
        if (defaultWorld == null) {
            Utils.warn("Could not find default world. Skipping Delay effect registration.");
            return;
        }
        // Hytale runs at 30TPS, but you can override this and choose a custom TPS
        DurationUtils.overrideTickLength(defaultWorld.getTickStepNanos());

        registration.newEffect(EffDelay.class, "(wait|halt) [for] %duration%",
                "(wait|halt) in [world] %world% [for] %duration%",
                "(wait|halt) (0:until|1:while) %=boolean%",
                "(wait|halt) in [world] %world% (0:until|1:while) %=boolean%")
            .name("Delay")
            .description("Delays the execution of the next statements for a specified duration.",
                "Allows for conditional delays based on a boolean condition.",
                "Supports execution in specific worlds and conditional.",
                "If the world is not provided, a world will be chosen based on the event used.")
            .since("1.0.0")
            .register();
    }

    private static final ScheduledExecutorService SCHEDULER = HytaleServer.SCHEDULED_EXECUTOR;
    private Expression<Duration> duration;
    private Expression<World> world;
    private Expression<Boolean> condition;
    private boolean isConditional;
    private boolean negated;
    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        parseContext.getParserState().setDelayed(true);
        this.pattern = matchedPattern;
        this.isConditional = matchedPattern > 1;
        switch (matchedPattern) {
            case 0 -> {
                this.duration = (Expression<Duration>) expressions[0];
            }
            case 1 -> {
                this.world = (Expression<World>) expressions[0];
                this.duration = (Expression<Duration>) expressions[1];
            }
            case 2 -> {
                this.condition = (Expression<Boolean>) expressions[0];
                this.negated = parseContext.getNumericMark() == 0;
            }
            case 3 -> {
                this.world = (Expression<World>) expressions[0];
                this.condition = (Expression<Boolean>) expressions[1];
                this.negated = parseContext.getNumericMark() == 0;
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<? extends Statement> walk(@NotNull TriggerContext ctx) {
        Optional<? extends Statement> nextOpt = getNext();
        if (nextOpt.isEmpty()) return Optional.empty();

        Statement nextStatement = nextOpt.get();

        World world;
        if (this.world != null) {
            world = this.world.getSingle(ctx).orElse(null);
        } else if (ctx instanceof WorldContext worldContext) {
            world = worldContext.getWorld();
        } else if (ctx instanceof PlayerContext playerContext) {
            world = playerContext.getPlayer().getWorld();
        } else {
            world = null;
        }

        // Copy local variables to reuse after our delay
        VariableMap variableMap = Variables.copyLocalVariables(ctx);
        if (this.isConditional) {
            var cond = condition.getSingle(ctx);
            // The code we want to run each check.
            Runnable code = () -> {
                if (cond.filter(b -> negated == b.booleanValue()).isPresent()) {
                    // Copy them back into the original context
                    Variables.setLocalVariables(ctx, variableMap);
                    Statement.runAll(nextStatement, ctx);
                    // Now delete it all
                    Variables.clearLocalVariables(ctx);
                }
            };
            Runnable worldTask = () -> {
                if (world == null || world.isInThread()) {
                    code.run();
                } else {
                    world.execute(code);
                }
            };

            SCHEDULER.scheduleAtFixedRate(worldTask, 0, DurationUtils.TICK, TimeUnit.MILLISECONDS);

        } else {
            Optional<? extends Duration> dur = duration.getSingle(ctx);
            if (dur.isEmpty()) return nextOpt;

            Runnable code = () -> {
                // Copy them back into the original context
                Variables.setLocalVariables(ctx, variableMap);
                Statement.runAll(nextStatement, ctx);
                // Now delete it all
                Variables.clearLocalVariables(ctx);
            };
            Runnable worldTask = () -> {
                if (world == null || world.isInThread()) {
                    code.run();
                } else {
                    world.execute(code);
                }
            };

            SCHEDULER.schedule(worldTask, dur.get().toMillis(), TimeUnit.MILLISECONDS);
        }
        return Optional.empty();
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return switch (this.pattern) {
            case 0 -> "wait " + this.duration.toString(ctx, debug);
            case 1 -> "wait in " + this.world.toString(ctx, debug) + " for " + this.duration.toString(ctx, debug);
            case 2 -> "wait " + (this.negated ? "until" : "while") + " " + this.condition.toString(ctx, debug);
            case 3 -> "wait in " + this.world.toString(ctx, debug) + " " + (this.negated ? "until" : "while") + " " +
                this.condition.toString(ctx, debug);
            default -> "unknown";
        };
    }

}
