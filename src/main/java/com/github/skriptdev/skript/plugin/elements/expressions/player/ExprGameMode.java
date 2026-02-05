package com.github.skriptdev.skript.plugin.elements.expressions.player;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprGameMode extends PropertyExpression<Player, GameMode> {

    public static void register(SkriptRegistration registration) {
        registration.newPropertyExpression(ExprGameMode.class, GameMode.class, "game[(-| )]mode", "players")
            .name("GameMode of a player")
            .description("Returns the game mode of a player.")
            .examples("set {_gm} to game-mode of context-player")
            .since("1.0.0")
            .register();
    }

    @Override
    public @Nullable GameMode getProperty(@NotNull Player player) {
        return player.getGameMode();
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) return Optional.of(new Class<?>[]{GameMode.class});
        return Optional.empty();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeMode != ChangeMode.SET || changeWith == null) return;

        GameMode newGameMode = changeWith[0] instanceof GameMode gm ? gm : null;
        if (newGameMode == null) {
            return;
        }

        for (Player player : getOwner().getArray(ctx)) {
            if (player == null) continue;

            Ref<EntityStore> ref = player.getReference();
            Runnable gamemodeRunnable = () -> Player.setGameMode(ref, newGameMode, ref.getStore());

            World world = player.getWorld();
            if (world.isInThread()) {
                gamemodeRunnable.run();
            } else {
                world.execute(gamemodeRunnable);
            }
        }
    }

    @Override
    public Class<? extends GameMode> getReturnType() {
        return GameMode.class;
    }

}
