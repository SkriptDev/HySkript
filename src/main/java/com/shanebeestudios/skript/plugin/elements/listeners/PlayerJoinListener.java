package com.shanebeestudios.skript.plugin.elements.listeners;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.shanebeestudios.skript.api.skript.eventcontext.PlayerEventContext;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinListener {

    private final List<Trigger> join = new ArrayList<>();
    private final List<Trigger> ready = new ArrayList<>();
    private final List<Trigger> quit = new ArrayList<>();

    public PlayerJoinListener(EventRegistry registry) {
        registry.register(PlayerConnectEvent.class, this::onConnect);
        registry.registerGlobal(PlayerReadyEvent.class, this::onReady);
        registry.register(PlayerDisconnectEvent.class, this::onQuit);
    }

    public void addTrigger(Trigger trigger, int type) {
        switch (type) {
            case 0 -> this.join.add(trigger);
            case 1 -> this.ready.add(trigger);
            case 2 -> this.quit.add(trigger);
        }
    }

    public void clearTriggers() {
        this.join.clear();
        this.ready.clear();
        this.quit.clear();
    }

    public void onConnect(PlayerConnectEvent event) {
        Player player = event.getHolder().getComponent(Player.getComponentType());
        for (Trigger trigger : this.join) {
            Statement.runAll(trigger, new PlayerEventContext(player, 0));
        }

    }

    public void onReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        for (Trigger trigger : this.ready) {
            Statement.runAll(trigger, new PlayerEventContext(player, 1));
        }

    }

    public void onQuit(PlayerDisconnectEvent event) {
        // TODO
    }

}
