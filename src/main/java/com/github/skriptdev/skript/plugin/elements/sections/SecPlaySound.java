package com.github.skriptdev.skript.plugin.elements.sections;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.lang.CodeSection;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.entries.SectionConfiguration;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SecPlaySound extends CodeSection {

    public static void register(SkriptRegistration reg) {
        reg.newSection(SecPlaySound.class, "play sound %soundevent%")
            .name("Play Sound")
            .description("Play a sound at a location or to players.",
                "**Entries**:",
                " - `to-players` = The players/playerRefs to send the sound to (must use either this or `location`).",
                " - `location` = The location to play the sound at (must use either this or `to-players`).",
                " - `volume` = The volume of the sound ([optional] default: 1.0).",
                " - `pitch` = The pitch of the sound ([optional] default: 1.0).",
                " - `3d` = Whether the sound should be 3D ([optional, `location` is required for this] default: false).",
                " - `sound-category` = The category of the sound ([optional] default: ambient).")
            .experimental("This seems a bit messy/buggy and may change in the future.")
            .since("INSERT VERSION")
            .register();
    }

    SectionConfiguration sectionConfig = new SectionConfiguration.Builder()
        .addOptionalExpression("to-players", PlayerRef.class, true)
        .addOptionalKey("location")
        .addOptionalLiteral("volume", Number.class)
        .addOptionalLiteral("pitch", Number.class)
        .addOptionalLiteral("3d", Boolean.class)
        .addOptionalLiteral("sound-category", SoundCategory.class)
        .build();

    private Expression<SoundEvent> soundEvent;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.soundEvent = (Expression<SoundEvent>) expressions[0];
        return true;
    }

    @Override
    public boolean loadSection(@NotNull FileSection section, @NotNull ParserState parserState, @NotNull SkriptLogger logger) {
        return this.sectionConfig.loadConfiguration(null, section, parserState, logger);
    }

    @Override
    public Optional<? extends Statement> walk(@NotNull TriggerContext ctx) {
        Optional<? extends Statement> nextStatement = getNext();

        SoundEvent soundEvent = this.soundEvent.getSingle(ctx).orElse(null);
        if (soundEvent == null) return nextStatement;
        int soundIndex = SoundEvent.getAssetMap().getIndex(soundEvent.getId());
        if (soundIndex < 0) return nextStatement;

        Location location = this.sectionConfig.getValue("location", Location.class).orElse(null);
        Expression<PlayerRef> playerExpr = this.sectionConfig.getExpression("to-players", PlayerRef.class).orElse(null);
        Number volume = this.sectionConfig.getValue("volume", Number.class).orElse(1.0f);
        Number pitch = this.sectionConfig.getValue("pitch", Number.class).orElse(1.0f);
        boolean is3d = this.sectionConfig.getValue("3d", Boolean.class).orElse(false);
        SoundCategory soundCategory = this.sectionConfig.getValue("sound-category", SoundCategory.class).orElse(SoundCategory.Ambient);

        if (playerExpr != null) {
            PlayerRef[] playerRefs = playerExpr.getArray(ctx);
            playSoundToPlayer(playerRefs, location, soundIndex, soundCategory, is3d, volume.floatValue(), pitch.floatValue());
        } else if (location != null) {
            playSoundAtLocation(location, soundIndex, soundCategory, is3d, volume.floatValue(), pitch.floatValue());
        }
        return nextStatement;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "";
    }

    public void playSoundToPlayer(@NotNull PlayerRef[] players, @Nullable Location location, int soundEvent, SoundCategory category, boolean is3d, float volume, float pitch) {
        for (PlayerRef player : players) {
            if (is3d && location != null) {
                Vector3d pos = location.getPosition();
                Ref<EntityStore> reference = player.getReference();
                if (reference == null || !reference.isValid()) continue;

                Store<EntityStore> store = reference.getStore();
                SoundUtil.playSoundEvent3dToPlayer(reference, soundEvent, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, store);
            } else {
                SoundUtil.playSoundEvent2dToPlayer(player, soundEvent, category, volume, pitch);
            }
        }
    }

    public void playSoundAtLocation(@NotNull Location location, int soundEvent, SoundCategory category, boolean is3d, float volume, float pitch) {
        String worldName = location.getWorld();
        if (worldName == null) return;

        World world = Universe.get().getWorld(worldName);
        if (world == null) return;

        Store<EntityStore> store = world.getEntityStore().getStore();
        if (is3d) {
            Vector3d pos = location.getPosition();
            SoundUtil.playSoundEvent3d(soundEvent, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, store);
        } else {
            SoundUtil.playSoundEvent2d(soundEvent, category, volume, pitch, store);
        }
    }

}
