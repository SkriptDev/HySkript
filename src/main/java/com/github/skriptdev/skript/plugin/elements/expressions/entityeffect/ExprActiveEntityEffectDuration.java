package com.github.skriptdev.skript.plugin.elements.expressions.entityeffect;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class ExprActiveEntityEffectDuration extends PropertyExpression<ActiveEntityEffect, Duration> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprActiveEntityEffectDuration.class, Duration.class,
                "[(remaining|:initial)] effect duration", "activeentityeffects")
            .name("Active Entity Effect - Duration")
            .description("Returns the duration of an ActiveEntityEffect.",
                "You can get the initial duration or the remaining duration. Will default to remaining if not explicitly specified.")
            .examples("if remaining effect duration of {_effect} is less than 10 seconds:")
            .since("INSERT VERSION")
            .register();
    }

    private boolean initial;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.initial = parseContext.hasMark("initial");
        return super.init(expressions, matchedPattern, parseContext);
    }

    @Override
    public @Nullable Duration getProperty(@NotNull ActiveEntityEffect effect) {
        float duration;
        if (this.initial) {
            duration = effect.getInitialDuration();
        } else {
            duration = effect.getRemainingDuration();
        }
        return Duration.ofMillis((long) duration * 1000);
    }

}
