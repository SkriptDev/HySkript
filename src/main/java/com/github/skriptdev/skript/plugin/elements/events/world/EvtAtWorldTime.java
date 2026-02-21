package com.github.skriptdev.skript.plugin.elements.events.world;

import com.github.skriptdev.skript.api.skript.event.WorldContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.lang.VariableString;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.event.StartOnLoadEvent;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.Time;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EvtAtWorldTime extends SkriptEvent implements StartOnLoadEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtAtWorldTime.class,
                "*at %*time% [world time] in world %*string%")
            .setHandledContexts(WorldTimeContext.class)
            .name("At World Time")
            .description("Triggered every ingame day at a given time.",
                "Internally this is handled by the minute.")
            .examples("at 10:00 pm in world \"default\":",
                "\tkill all players in event-world")
            .since("INSERT VERSION")
            .register();
    }

    private Literal<Time> time;
    private World world;
    private ScheduledFuture<?> scheduledFuture;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.time = (Literal<Time>) expressions[0];
        VariableString name = (VariableString) expressions[1];
        String worldName = name.toString(TriggerContext.DUMMY);
        World world = Universe.get().getWorld(worldName);
        if (world == null) {
            parseContext.getLogger().error("Invalid world '" + worldName + "'", ErrorType.SEMANTIC_ERROR);
            return false;
        }
        this.world = world;
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof WorldTimeContext;
    }

    @Override
    public void onInitialLoad(Trigger trigger) {
        int duration = getDuration(this.world);
        if (duration <= 0) return;

        Optional<? extends Time> single = this.time.getSingle();
        if (single.isEmpty()) return;

        Time time = single.get();

        // Run the scheduler at an in-world minute interval
        this.scheduledFuture = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() ->
            this.world.execute(() -> {
                Store<EntityStore> store = this.world.getEntityStore().getStore();
                WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());

                Time worldTime = Time.of(worldTimeResource.getGameDateTime().toLocalTime());

                // Check every in-world minute for a match
                if (time.getHour() != worldTime.getHour() || time.getMinute() != worldTime.getMinute()) return;

                TriggerMap.callTriggersByContext(new WorldTimeContext(this.world));
            }), 0, duration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void unload() {
        if (this.scheduledFuture == null) return;
        this.scheduledFuture.cancel(true);
    }

    private int getDuration(World world) {
        int daySec = world.getDaytimeDurationSeconds();
        int nightSec = world.getNighttimeDurationSeconds();
        double i = (((double) (daySec + nightSec)) / WorldTimeResource.SECONDS_PER_DAY);
        return (int) (i * 1000 * 60);
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "at " + this.time.toString(ctx, debug) + " in world " + this.world.getName();
    }

    public record WorldTimeContext(World world) implements WorldContext {
        @Override
        public World getWorld() {
            return this.world;
        }

        @Override
        public String getName() {
            return "world time context";
        }
    }

}
