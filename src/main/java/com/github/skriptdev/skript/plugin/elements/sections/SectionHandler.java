package com.github.skriptdev.skript.plugin.elements.sections;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.elements.sections.server.SecMapMarker;

public class SectionHandler {

    public static void register(SkriptRegistration registration) {
        SecApplyStatModifier.register(registration);
        SecCreateWorld.register(registration);
        SecDropItem.register(registration);
        SecExecuteInWorld.register(registration);
        SecParticle.register(registration);
        SecPlaySound.register(registration);
        SecSendNotification.register(registration);
        SecSendTitle.register(registration);
        SecSpawnNPC.register(registration);

        // SERVER
        SecMapMarker.register(registration);
    }

}
