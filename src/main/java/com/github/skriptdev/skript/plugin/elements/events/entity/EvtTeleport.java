package com.github.skriptdev.skript.plugin.elements.events.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.event.PlayerContext;
import com.github.skriptdev.skript.api.skript.event.SystemEvent;
import com.github.skriptdev.skript.api.skript.event.WorldContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.context.ContextValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Collections;
import java.util.Set;

public class EvtTeleport extends SystemEvent<EvtTeleport.TeleportSystem> {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtTeleport.class,
                "teleport", "npc teleport", "player teleport")
            .name("Teleport")
            .description("Called when an entity/player teleports.")
            .examples("on player teleport:",
                "\tsend \"Ok byeeee....\" to player")
            .since("1.4.0")
            .setHandledContexts(TeleportContext.class)
            .register();

        reg.newSingleContextValue(TeleportContext.class, Location.class,
                "location", TeleportContext::getLocation)
            .description("The location of the entity after the teleport.")
            .register();
        reg.newSingleContextValue(TeleportContext.class, Location.class,
                "location", TeleportContext::getPastLocation)
            .setState(ContextValue.State.PAST)
            .description("The location of the entity before the teleport.")
            .register();
        reg.newSingleContextValue(TeleportContext.class, World.class,
                "world", TeleportContext::getPastWorld)
            .setState(ContextValue.State.PAST)
            .description("The world of the entity before the teleport.")
            .register();
        reg.newSingleContextValue(TeleportContext.class, Entity.class,
            "entity", TeleportContext::getEntity)
            .description("The entity that was teleported.")
            .register();
    }

    private static TeleportSystem SYSTEM;
    private int pattern;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.pattern = matchedPattern;
        if (SYSTEM == null) {
            SYSTEM = new TeleportSystem();
            applySystem(SYSTEM);
        }
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        if (!(ctx instanceof TeleportContext teleportContext)) return false;
        if (this.pattern == 0) return true;
        else return this.pattern == teleportContext.getPattern();
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "teleport";
    }

    public static class TeleportContext implements PlayerContext, WorldContext {

        private final int pattern;
        private final Teleport teleport;
        private final Entity entity;

        public TeleportContext(int pattern, Teleport teleport, Entity entity) {
            this.pattern = pattern;
            this.teleport = teleport;
            this.entity = entity;
        }

        public int getPattern() {
            return this.pattern;
        }

        public Entity getEntity() {
            return this.entity;
        }

        @Override
        public Player getPlayer() {
            if (this.entity instanceof Player player) {
                return player;
            }
            return null;
        }

        @Override
        public World getWorld() {
            return this.teleport.getWorld();
        }

        public World getPastWorld() {
            return this.entity.getWorld();
        }

        public Location getLocation() {
            return new Location(getWorld().getName(), this.teleport.getPosition(), this.teleport.getRotation());
        }

        public Location getPastLocation() {
            World world = this.entity.getWorld();
            assert world != null;
            TransformComponent component = EntityUtils.getComponent(this.entity, TransformComponent.getComponentType());
            assert component != null;
            Vector3d position = component.getPosition();
            Rotation3f rotation = component.getRotation();
            return new Location(world.getName(), position, rotation);
        }

        @Override
        public String getName() {
            return "teleport-context";
        }
    }

    public static class TeleportSystem extends RefChangeSystem<EntityStore, Teleport> {

        @Override
        public @NotNull ComponentType<EntityStore, Teleport> componentType() {
            return Teleport.getComponentType();
        }

        @Override
        public @NotNull Set<Dependency<EntityStore>> getDependencies() {
            return Collections.singleton(RootDependency.first());
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        public void onComponentAdded(@NotNull Ref<EntityStore> ref, @NotNull Teleport teleport,
                                     @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> buffer) {

            NPCEntity npc = buffer.getComponent(ref, NPCEntity.getComponentType());
            Player player = buffer.getComponent(ref, Player.getComponentType());

            Entity entity;
            int pattern;
            if (player != null) {
                pattern = 2;
                entity = player;
            } else if (npc != null) {
                pattern = 1;
                entity = npc;
            } else {
                return;
            }

            TeleportContext teleportContext = new TeleportContext(pattern, teleport, entity);
            TriggerMap.callTriggersByContext(teleportContext);
        }

        @Override
        public void onComponentSet(@NotNull Ref<EntityStore> ref, @Nullable Teleport teleport,
                                   @NotNull Teleport t1, @NotNull Store<EntityStore> store,
                                   @NotNull CommandBuffer<EntityStore> commandBuffer) {

        }

        @Override
        public void onComponentRemoved(@NotNull Ref<EntityStore> ref, @NotNull Teleport teleport,
                                       @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {

        }

        @Override
        public @Nullable Query<EntityStore> getQuery() {
            return Query.or(NPCEntity.getComponentType(), Player.getComponentType());
        }
    }

}
