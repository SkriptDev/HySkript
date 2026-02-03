package com.github.skriptdev.skript.plugin.elements.effects.other;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.api.utils.Utils;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.DurationUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
                "(wait|halt) (0:until|1:while) %=boolean% [for %*duration%]")
            .name("Delay")
            .description("Delays the execution of the next statements for a specified duration.")
            .since("1.0.0")
            .register();
    }

    private static final ScheduledExecutorService SCHEDULER = HytaleServer.SCHEDULED_EXECUTOR;
    private Expression<Duration> duration;
    private Expression<Boolean> condition;
    private boolean isConditional;
    private boolean negated;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.isConditional = matchedPattern == 1;
        if (this.isConditional) {
            this.condition = (Expression<Boolean>) expressions[0];
            if (expressions.length == 2)
                this.duration = (Literal<Duration>) expressions[1];
            this.negated = parseContext.getNumericMark() == 0;
        } else {
            this.duration = (Expression<Duration>) expressions[0];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<? extends Statement> walk(@NotNull TriggerContext ctx) {
        if (getNext().isEmpty())
            return Optional.empty();

        if (this.isConditional) {
            var cond = condition.getSingle(ctx);
            // The code we want to run each check.
            Consumer<Void> code = exec -> {
                if (cond.filter(b -> negated == b.booleanValue()).isPresent()) {
                    Statement.runAll(getNext().get(), ctx);
                }
            };

            if (this.duration == null) {
                SCHEDULER.scheduleAtFixedRate(() ->
                        code.accept(null),
                    0,
                    DurationUtils.TICK,
                    TimeUnit.MILLISECONDS);

            } else {
                Duration dur = ((Optional<Duration>) ((Literal<Duration>) this.duration).getSingle()).orElse(Duration.ZERO);
                SCHEDULER.schedule(() -> {
                    Statement.runAll(getNext().get(), ctx);
                }, dur.toMillis(), TimeUnit.MILLISECONDS);
            }
        } else {
            Optional<? extends Duration> dur = duration.getSingle(ctx);
            if (dur.isEmpty())
                return getNext();

            SCHEDULER.schedule(() ->
                    Statement.runAll(getNext().get(), ctx),
                dur.get().toMillis(),
                TimeUnit.MILLISECONDS);
        }
        return Optional.empty();
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        if (this.isConditional) {
            String duration = this.duration == null ? "" : " for " + this.duration.toString(ctx, debug);
            return "wait " + (this.negated ? "until" : "while") + " " + this.condition.toString(ctx, debug) + duration;
        }
        return "wait " + this.duration.toString(ctx, debug);
    }

}
