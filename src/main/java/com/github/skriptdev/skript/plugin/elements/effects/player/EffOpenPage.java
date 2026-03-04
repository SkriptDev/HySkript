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

public class EffOpenPage extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffOpenPage.class, "open page %page% to %players%")
            .name("Open Page")
            .description("Opens a page to the specified players.")
            .examples("open page inventory to player",
                "open page map to all players")
            .since("1.3.0")
            .register();
    }

    private Expression<Page> page;
    private Expression<Player> players;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.page = (Expression<Page>) expressions[0];
        this.players = (Expression<Player>) expressions[1];
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        Page page = this.page.getSingle(ctx).orElse(null);
        if (page == null || page == Page.Custom) return;

        for (Player player : this.players.getArray(ctx)) {
            Ref<EntityStore> reference = player.getReference();
            if (reference == null) continue;

            Runnable inWorld = () -> {
                Store<EntityStore> store = reference.getStore();
                player.getPageManager().setPage(reference, store, page, true);
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
        return "open page " + this.page.toString(ctx, debug) + " to " + this.players.toString(ctx, debug);
    }

}
