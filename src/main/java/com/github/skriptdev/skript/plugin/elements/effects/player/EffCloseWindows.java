package com.github.skriptdev.skript.plugin.elements.effects.player;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EffCloseWindows extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffCloseWindows.class, "close [all] [open] windows of %players%")
            .name("Close Windows")
            .description("Close all open windows/pages of a player.")
            .examples("close open windows of player")
            .since("1.3.0")
            .register();
    }

    private Expression<Player> players;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.players = (Expression<Player>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        for (Player player : this.players.getArray(ctx)) {
            Ref<EntityStore> reference = player.getReference();
            if (reference == null) continue;

            Runnable inWorld = () -> {
                Store<EntityStore> store = reference.getStore();
                player.getWindowManager().closeAllWindows(reference, store);
                player.getPageManager().setPage(reference, store, Page.None, false);
            };

            World world = player.getWorld();
            if (world == null) continue;
            if (world.isInThread()) {
                inWorld.run();
            } else {
                world.execute(inWorld);
            }
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "close all open windows of " + this.players.toString(ctx, debug);
    }

}
