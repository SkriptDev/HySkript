package com.github.skriptdev.skript.plugin.elements.expressions;

import com.hypixel.hytale.server.core.Message;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExprMessage implements Expression<Message> {

    public static void register(SkriptRegistration registration) {
        registration.newExpression(ExprMessage.class, Message.class, true,
                "[new] [raw] message from %string%",
                "[new] translated message from %string%",
                "\"[new] message translation from %string%\"")
            .name("Message")
            .description("Create a new message from a string.",
                "The translated option will use a translation key from the game's lang file instead of a raw string.")
            .examples("on player ready:",
                "\tset {_message} to message from \"Welcome to the server %context-player%\"",
                "\tset message color of {_message} to \"##0CB1F7\"",
                "\tsend {_message} to player",
                "set {_message} to message translation from \"server.chat.playerMessage\"")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<String> string;
    private boolean translation;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.string = (Expression<String>) expressions[0];
        this.translation = matchedPattern > 0;
        return true;
    }

    @Override
    public Message[] getValues(@NotNull TriggerContext ctx) {
        Optional<? extends String> single = this.string.getSingle(ctx);
        return single.map(s -> {
            Message m;
            if (this.translation) {
                m = Message.translation(s);
            } else {
                m = Message.raw(s);
            }
            return new Message[]{m};
        }).orElse(null);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String translated = this.translation ? "translated" : "raw";
        return translated + " message from " + this.string.toString(ctx, debug);
    }

}
