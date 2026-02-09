package com.github.skriptdev.skript.plugin.elements.effects.other;

import com.github.skriptdev.skript.api.skript.event.PlayerContext;
import com.github.skriptdev.skript.api.utils.Utils;
import com.github.skriptdev.skript.plugin.elements.command.ScriptCommand.ScriptCommandContext;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.jetbrains.annotations.NotNull;

public class EffSendMessage extends Effect {

    public static void register(SkriptRegistration registration) {
        registration.newEffect(EffSendMessage.class,
                "send [message[s]] %objects% [to %-messagereceivers%]")
            .name("Send Message")
            .description("Sends a message to a command sender such as a player or the console.",
                "If a receiver is not specified:",
                " - If run in a player event, the message will be sent to the player.",
                " - If run in a command, the message will be sent to the command sender.",
                " - Else the message will be sent to the console.",
                "See [Message Format](https://github.com/SkriptDev/HySkript/wiki/Tutorial-Message-Format) on the wiki" +
                    "for info about formatting messages.")
            .examples("send \"Welcome to the server\" to player",
                "send formatted \"<yellow>My script has loaded\"")
            .since("1.0.0")
            .register();
    }

    private Expression<?> messages;
    private Expression<IMessageReceiver> senders;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull ParseContext parseContext) {
        this.messages = exprs[0];
        if (exprs.length > 1) { // TODO whatttt?!?!?
            this.senders = (Expression<IMessageReceiver>) exprs[1];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        Object[] messages = this.messages.getValues(ctx);
        if (messages == null || messages.length == 0) return;

        if (this.senders != null) {
            for (IMessageReceiver commandSender : this.senders.getArray(ctx)) {
                sendMessage(commandSender, messages);
            }
        } else {
            if (ctx instanceof PlayerContext playerContext) {
                sendMessage(playerContext.getPlayer(), messages);
            } else if (ctx instanceof ScriptCommandContext commandContext) {
                sendMessage(commandContext.getSender(), messages);
            } else {
                sendMessage(ConsoleSender.INSTANCE, messages);
            }
        }
    }

    private void sendMessage(IMessageReceiver commandSender, Object[] objects) {
        for (Object value : objects) {
            if (value instanceof String string) {
                Utils.sendMessage(commandSender, string);
            } else if (value instanceof Message message) {
                commandSender.sendMessage(message);
            } else {
                Utils.sendMessage(commandSender, TypeManager.toString(new Object[]{value}));
            }
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String to = this.senders != null ? " to " + this.senders.toString(ctx, debug) : "";
        return "send message[s] " + this.messages.toString(ctx, debug) + to;
    }

}
