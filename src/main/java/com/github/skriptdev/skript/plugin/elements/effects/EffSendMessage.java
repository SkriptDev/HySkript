package com.github.skriptdev.skript.plugin.elements.effects;

import com.github.skriptdev.skript.api.skript.eventcontext.PlayerEventContext;
import com.github.skriptdev.skript.api.utils.Utils;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffSendMessage extends Effect {

    public static void register(SkriptRegistration registration) {
        registration.newEffect(EffSendMessage.class, "send [message[s]] %strings% [to %-messagereceivers%]")
            .name("Send Message")
            .description("Sends a message to a command sender such as a player or the console.")
            .examples("send \"Welcome to the server\" to player")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<String> message;
    private Expression<IMessageReceiver> senders;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull ParseContext parseContext) {
        this.message = (Expression<String>) exprs[0];
        if (exprs.length > 1) { // TODO whatttt?!?!?
            this.senders = (Expression<IMessageReceiver>) exprs[1];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        String[] messages = this.message.getValues(ctx);
        if (this.senders != null) {
            for (IMessageReceiver commandSender : this.senders.getArray(ctx)) {
                for (String value : messages) {
                    Utils.sendMessage(commandSender, value);
                }
            }
        } else {
            if (ctx instanceof PlayerEventContext playerEventContext) {
                for (CommandSender commandSender : playerEventContext.getPlayer()) {
                    for (String value : messages) {
                        Utils.sendMessage(commandSender, value);
                    }
                }
            }
            for (String value : messages) {
                Utils.log(value);
            }
        }
    }

    @Override
    public String toString(@Nullable TriggerContext ctx, boolean debug) {
        return "send message[s] " + this.message.toString(ctx, debug);
    }

}
