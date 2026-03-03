package com.github.skriptdev.skript.plugin.elements.sections;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.elements.sections.entity.SecApplyStatModifier;
import com.github.skriptdev.skript.plugin.elements.sections.entity.SecDropItem;
import com.github.skriptdev.skript.plugin.elements.sections.entity.SecSpawnNPC;
import com.github.skriptdev.skript.plugin.elements.sections.player.SecPlaySound;
import com.github.skriptdev.skript.plugin.elements.sections.player.SecSendNotification;
import com.github.skriptdev.skript.plugin.elements.sections.player.SecSendTitle;
import com.github.skriptdev.skript.plugin.elements.sections.server.SecMapMarker;
import com.github.skriptdev.skript.plugin.elements.sections.world.SecCreateWorld;
import com.github.skriptdev.skript.plugin.elements.sections.world.SecExecuteInWorld;
import com.github.skriptdev.skript.plugin.elements.sections.world.SecParticle;

public class SectionHandler {

    public static void register(SkriptRegistration registration) {
        // ENTITY
        SecApplyStatModifier.register(registration);
        SecDropItem.register(registration);
        SecSpawnNPC.register(registration);

        // PLAYER
        SecPlaySound.register(registration);
        SecSendNotification.register(registration);
        SecSendTitle.register(registration);

        // SERVER
        SecMapMarker.register(registration);

        // WORLD
        SecCreateWorld.register(registration);
        SecExecuteInWorld.register(registration);
        SecParticle.register(registration);
    }

}
