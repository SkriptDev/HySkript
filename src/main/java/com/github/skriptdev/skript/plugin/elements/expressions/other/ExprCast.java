package com.github.skriptdev.skript.plugin.elements.expressions.other;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.Type;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ExprCast implements Expression<Object> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprCast.class, Object.class, true,
                "<.+> as %*type%")
            .name("Cast")
            .description("Casts an object to a specific type.")
            .examples("set {_i} to ingredient_poop as Item",
                "set {_bt} to ingredient_poop as BlockType",
                "set {_f} to 1 as Float")
            .since("1.0.0")
            .register();
    }

    private String castable;
    private Type<?> type;
    private Object parsedObject;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        SkriptLogger logger = parseContext.getLogger();
        this.castable = parseContext.getMatches().getFirst().group();
        Literal<Type<?>> expression = (Literal<Type<?>>) expressions[0];
        this.type = expression.getSingle().orElse(null);
        if (this.type == null) {
            // This shouldn't happen, but let's be safe;
            logger.error("Type cannot be null for cast expression initialization.", ErrorType.SEMANTIC_ERROR);
            return false;
        }
        if (this.type.getLiteralParser().isEmpty()) {
            String baseName = this.type.getBaseName();
            logger.error("The type '" + baseName + "' cannot be cast.", ErrorType.SEMANTIC_ERROR);
            return false;
        }
        Function<String, ?> parser = this.type.getLiteralParser().get();
        // Let's parse at parse time so we don't have to worry about it during runtime
        this.parsedObject = parser.apply(this.castable);
        if (this.parsedObject == null) {
            String baseName = this.type.getBaseName();
            logger.error("'" + this.castable + "' cannot be cast to " + baseName + ".", ErrorType.SEMANTIC_ERROR);
            return false;
        }

        return true;
    }

    @Override
    public Object[] getValues(@NotNull TriggerContext ctx) {
        return new Object[]{this.parsedObject};
    }

    @Override
    public Class<?> getReturnType() {
        return this.type.getTypeClass();
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return this.castable + " as " + this.type;
    }

}
