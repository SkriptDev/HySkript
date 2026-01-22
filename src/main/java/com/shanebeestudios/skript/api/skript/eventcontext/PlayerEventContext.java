package com.shanebeestudios.skript.api.skript.eventcontext;

import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.syst3ms.skriptparser.lang.TriggerContext;

public class PlayerEventContext implements TriggerContext {

    private final Player player;
    private final int pattern;

    public PlayerEventContext(Player player, int pattern) {
        this.player = player;
        this.pattern = pattern;
    }

    public Player[] getPlayer() {
        return new Player[]{player};
    }

    public int getPattern() {
        return pattern;
    }

    @Override
    public String getName() {
        return "player-join";
    }
}
