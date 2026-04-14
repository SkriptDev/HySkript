package com.github.skriptdev.skript.api.skript.event;

import com.github.skriptdev.skript.api.hytale.utils.PlayerUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.syst3ms.skriptparser.lang.TriggerContext;

/**
 * Represents a {@link TriggerContext} which includes a {@link Player}
 */
public interface PlayerContext extends TriggerContext {

    Player getPlayer();

    default PlayerRef getPlayerRef() {
        return PlayerUtils.getPlayerRef(getPlayer());
    }

}
