package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupDisconnectEvent;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EvtPlayerSetupDisconnect extends SkriptEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerSetupDisconnect.class, "player setup disconnect")
            .name("Player Setup Disconnect")
            .description("Called when a player is disconnecting from the server.")
            .since("1.0.0")
            .setHandledContexts(PlayerSetupDisconnectContext.class)
            .register();

        reg.addSingleContextValue(PlayerSetupDisconnectContext.class, String.class,
            "name", PlayerSetupDisconnectContext::getUsername);
        reg.addSingleContextValue(PlayerSetupDisconnectContext.class, UUID.class,
            "uuid", PlayerSetupDisconnectContext::getUuid);
        reg.addSingleContextValue(PlayerSetupDisconnectContext.class, String.class,
            "reason", PlayerSetupDisconnectContext::getDisconnectReason);
    }

    private static EventRegistration<Void, PlayerSetupDisconnectEvent> LISTENER;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        if (LISTENER == null) {
            LISTENER = HySk.getInstance().getEventRegistry().registerGlobal(PlayerSetupDisconnectEvent.class, event -> {
                PlayerSetupDisconnectContext context = new PlayerSetupDisconnectContext(event);
                TriggerMap.callTriggersByContext(context);
            });
        }
        return true;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return triggerContext instanceof PlayerSetupDisconnectContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "player setup disconnects";
    }

    private record PlayerSetupDisconnectContext(PlayerSetupDisconnectEvent event) implements TriggerContext {

        public String getUsername() {
            return this.event.getUsername();
        }

        public UUID getUuid() {
            return this.event.getUuid();
        }

        public String getDisconnectReason() {
            return this.event.getDisconnectReason().getServerDisconnectReason();
        }

        @Override
        public String getName() {
            return "player setup disconnect context";
        }
    }

}
