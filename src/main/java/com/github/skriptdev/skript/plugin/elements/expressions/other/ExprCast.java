package com.github.skriptdev.skript.plugin.elements.expressions.other;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
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
    private Function<String, ?> parser;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.castable = parseContext.getMatches().getFirst().group();
        Literal<Type<?>> expression = (Literal<Type<?>>) expressions[0];
        this.type = expression.getSingle().orElse(null);
        if (this.type == null) {
            // This shouldn't happen, but let's be safe;
            parseContext.getLogger().error("Type cannot be null for cast expression initialization.", ErrorType.SEMANTIC_ERROR);
            return false;
        }
        if (this.type.getLiteralParser().isEmpty()) {
            String baseName = this.type.getBaseName();
            parseContext.getLogger().error("The type '" + baseName + "' cannot be casted.", ErrorType.SEMANTIC_ERROR);
            return false;
        }
        this.parser = this.type.getLiteralParser().get();

        return true;
    }

    @Override
    public Object[] getValues(@NotNull TriggerContext ctx) {
        Object apply = this.parser.apply(castable);
        return new Object[]{apply};
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
