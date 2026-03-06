package com.github.skriptdev.skript.plugin.elements.effects.player;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EffKick extends Effect {

    public static void register(SkriptRegistration registration) {
        registration.newEffect(EffKick.class, "kick %players/playerrefs%",
                "kick %players/playerrefs% (for reason|due to|because) %string/message%")
            .name("Kick Player")
            .description("Kicks the specified players with an optional reason.")
            .examples("kick all players due to \"Cheating!\"",
                "kick player due to \"Bad chat message!\"",
                "kick all players")
            .since("1.0.0")
            .register();
    }

    private Expression<?> players;
    private Expression<?> reason;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.players = expressions[0];
        if (matchedPattern == 1) {
            this.reason = expressions[1];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        Message reason = Message.raw("You were kicked.");
        if (this.reason != null) {
            Optional<?> single = this.reason.getSingle(ctx);
            if (single.isPresent()) {
                Object o = single.get();
                if (o instanceof Message message) {
                    reason = message;
                } else if (o instanceof String s) {
                    reason = Message.raw(s);
                }
            }
        }

        for (Object o : this.players.getArray(ctx)) {
            kick(o, reason);
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String reason = this.reason == null ? "" : " for reason " + this.reason.toString(ctx, debug);
        return "kick " + this.players.toString(ctx, debug) + reason;
    }

    @SuppressWarnings("removal")
    public static void kick(Object player, Message reason) {
        if (player instanceof PlayerRef ref) {
            ref.getPacketHandler().disconnect(reason);
        } else if (player instanceof Player p) {
            p.getPlayerRef().getPacketHandler().disconnect(reason);
        }
    }

}
