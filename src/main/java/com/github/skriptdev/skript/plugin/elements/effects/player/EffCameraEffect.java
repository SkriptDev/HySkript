package com.github.skriptdev.skript.plugin.elements.effects.player;

import com.github.skriptdev.skript.api.hytale.utils.PlayerUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.protocol.packets.camera.CameraShakeEffect;
import com.hypixel.hytale.server.core.asset.type.camera.CameraEffect;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EffCameraEffect extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffCameraEffect.class,
                "apply camera effect %cameraeffect% to %players/playerrefs%",
                "apply camera effect %cameraeffect% with intensity %number% to %players/playerrefs%")
            .name("Apply Camera Effect")
            .description("Applies a camera effect to the specified players with an optional intensity.")
            .examples("apply camera effect Impact_Strong to player",
                "apply camera effect Mace_Explode with intensity 2 to player")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<CameraEffect> cameraEffect;
    private Expression<Number> intensity;
    private Expression<?> players;


    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        if (matchedPattern == 0) {
            this.cameraEffect = (Expression<CameraEffect>) expressions[0];
            this.players = expressions[1];
        } else {
            this.cameraEffect = (Expression<CameraEffect>) expressions[0];
            this.intensity = (Expression<Number>) expressions[1];
            this.players = expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        CameraEffect effect = this.cameraEffect.getSingle(ctx).orElse(null);
        if (effect == null) return;

        CameraShakeEffect cameraShakePacket = null;
        if (this.intensity != null) {
            Number number = this.intensity.getSingle(ctx).orElse(null);
            if (number != null) {
                float v = number.floatValue();
                cameraShakePacket = effect.createCameraShakePacket(v);
            }
        }
        if (cameraShakePacket == null) {
            cameraShakePacket = effect.createCameraShakePacket();
        }

        for (Object object : this.players.getArray(ctx)) {
            PacketHandler packetHandler;
            if (object instanceof Player player) {
                PlayerRef playerRef = PlayerUtils.getPlayerRef(player);
                if (playerRef == null) continue;
                packetHandler = playerRef.getPacketHandler();
            } else if (object instanceof PlayerRef playerRef) {
                packetHandler = playerRef.getPacketHandler();
            } else {
                continue;
            }

            packetHandler.writeNoCache(cameraShakePacket);
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String intensity = this.intensity != null ? " with intensity " + this.intensity.toString(ctx, debug) : "";
        return "apply camera effect " + this.cameraEffect.toString(ctx, debug) + intensity
            + " to " + this.players.toString(ctx, debug);
    }

}
