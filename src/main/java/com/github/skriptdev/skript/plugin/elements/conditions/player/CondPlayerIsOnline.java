package com.github.skriptdev.skript.plugin.elements.conditions.player;

import com.github.skriptdev.skript.api.hytale.utils.PlayerUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import org.jetbrains.annotations.NotNull;

public class CondPlayerIsOnline extends PropertyConditional<Object> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondPlayerIsOnline.class, "players/playerrefs",
                ConditionalType.BE, "online")
            .name("Player is Online")
            .description("Check if a player is online.")
            .examples("if player is online:",
                "if player is not online:",
                "while player is online:")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return getPerformer().check(ctx, o -> {
            if (o instanceof Player player) {
                PlayerRef playerRef = PlayerUtils.getPlayerRef(player);
                return playerRef != null && playerRef.isValid();
            } else if (o instanceof PlayerRef playerRef) {
                return playerRef.isValid() && playerRef.getReference() != null;
            }
            return false;
        }, isNegated());
    }

}
