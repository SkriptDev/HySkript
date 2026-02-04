package com.github.skriptdev.skript.plugin.elements.expressions.other;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class ExprCurrentContext implements Expression<String> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprCurrentContext.class, String.class, true,
                "current trigger context", "current thread")
            .noDoc() // Used for debugging
            .register();
    }

    private int pattern;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.pattern = matchedPattern;
        return true;
    }

    @Override
    public String[] getValues(@NotNull TriggerContext ctx) {
        return new String[]{this.pattern == 0 ? ctx.getName() : Thread.currentThread().getName()};
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "current trigger context";
    }

}
