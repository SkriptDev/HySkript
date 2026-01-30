package com.github.skriptdev.skript.plugin.elements.events;


import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.elements.events.entity.EvtEntityDamage;
import com.github.skriptdev.skript.plugin.elements.events.entity.EvtEntityDeath;
import com.github.skriptdev.skript.plugin.elements.events.entity.EvtEntityRemove;
import com.github.skriptdev.skript.plugin.elements.events.entity.EvtLivingEntityInvChange;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerAddToWorld;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerBreakBlock;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerChat;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerDrainFromWorld;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerDropItem;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerDropItemRequest;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerJoin;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerMouseClick;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerMouseMove;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerSetupConnect;
import com.github.skriptdev.skript.plugin.elements.events.player.EvtPlayerSetupDisconnect;
import com.github.skriptdev.skript.plugin.elements.events.server.EvtBoot;
import com.github.skriptdev.skript.plugin.elements.events.server.EvtShutdown;
import com.github.skriptdev.skript.plugin.elements.events.skript.EvtLoad;

public class EventHandler {

    public static void register(SkriptRegistration registration) {
        // ENTITY
        EvtEntityDamage.register(registration);
        EvtEntityRemove.register(registration);
        EvtEntityDeath.register(registration);
        EvtLivingEntityInvChange.register(registration);

        // PLAYER
        EvtPlayerAddToWorld.register(registration);
        EvtPlayerBreakBlock.register(registration);
        EvtPlayerChat.register(registration);
        EvtPlayerDrainFromWorld.register(registration);
        EvtPlayerDropItem.register(registration);
        EvtPlayerDropItemRequest.register(registration);
        EvtPlayerJoin.register(registration);
        EvtPlayerMouseClick.register(registration);
        EvtPlayerMouseMove.register(registration);
        EvtPlayerSetupConnect.register(registration);
        EvtPlayerSetupDisconnect.register(registration);

        // SERVER
        EvtBoot.register(registration);
        EvtShutdown.register(registration);

        // SKRIPT
        EvtLoad.register(registration);
    }

}
