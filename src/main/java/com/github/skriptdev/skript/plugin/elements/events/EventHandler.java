package com.github.skriptdev.skript.plugin.elements.events;


import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;

public class EventHandler {

    public static void register(SkriptRegistration registration) {
        EvtDeath.register(registration);
        EvtLoad.register(registration);
        EvtPlayerChat.register(registration);
        EvtPlayerJoin.register(registration);
        SimpleEvents.register(registration);
    }

}
