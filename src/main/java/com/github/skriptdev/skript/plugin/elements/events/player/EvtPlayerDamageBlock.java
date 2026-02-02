package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.hytale.Block;
import com.github.skriptdev.skript.api.skript.event.CancellableContext;
import com.github.skriptdev.skript.api.skript.event.PlayerContext;
import com.github.skriptdev.skript.api.skript.event.SystemEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtPlayerDamageBlock extends SystemEvent<EntityEventSystem<EntityStore, DamageBlockEvent>> {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerDamageBlock.class,
                "player damage block", "block damaged by player")
            .setHandledContexts(DamageBlockContext.class)
            .name("Block Damage")
            .description("Called when a block is damaged by a player.")
            .since("INSERT VERSION")
            .register();

        reg.addContextValue(DamageBlockContext.class, Block.class, true, "block", DamageBlockContext::getBlock);
        reg.addContextValue(DamageBlockContext.class, BlockType.class, true, "blocktype", DamageBlockContext::getBlockType);
        reg.addContextValue(DamageBlockContext.class, ItemStack.class, true, "itemstack-in-hand", DamageBlockContext::getItemStackInHand);
        reg.addContextValue(DamageBlockContext.class, Number.class, true, "damage", DamageBlockContext::getDamage);
        reg.addContextValue(DamageBlockContext.class, Number.class, true, "current-damage", DamageBlockContext::getCurrentDamage);
    }

    private static DamageBlockSystem SYSTEM;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        if (SYSTEM == null) {
            SYSTEM = new DamageBlockSystem();
            applySystem(SYSTEM);
        }
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof DamageBlockContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "player damage block";
    }

    public record DamageBlockContext(DamageBlockEvent event, Player player)
        implements PlayerContext, CancellableContext {

        @Override
        public Player[] getPlayer() {
            return new Player[]{this.player};
        }

        public ItemStack[] getItemStackInHand() {
            return new ItemStack[]{this.event.getItemInHand()};
        }

        public Block[] getBlock() {
            Vector3i targetBlock = this.event.getTargetBlock();
            World world = this.player.getWorld();
            if (world == null) return null;
            return new Block[]{new Block(world, targetBlock, this.event.getBlockType())};
        }

        public BlockType[] getBlockType() {
            return new BlockType[]{this.event.getBlockType()};
        }

        public Number[] getDamage() {
            return new Number[]{this.event.getDamage()};
        }

        public Number[] getCurrentDamage() {
            return new Number[]{this.event.getCurrentDamage()};
        }

        @Override
        public boolean isCancelled() {
            return this.event.isCancelled();
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.event.setCancelled(cancelled);
        }

        @Override
        public String getName() {
            return "damage block context";
        }
    }

    public static class DamageBlockSystem extends EntityEventSystem<EntityStore, DamageBlockEvent> {

        protected DamageBlockSystem() {
            super(DamageBlockEvent.class);
        }

        @Override
        public void handle(int i, @NotNull ArchetypeChunk<EntityStore> archetypeChunk, @NotNull Store<EntityStore> store,
                           @NotNull CommandBuffer<EntityStore> commandBuffer, @NotNull DamageBlockEvent event) {

            if (event.getBlockType() == BlockType.EMPTY) return;

            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) return;

            DamageBlockContext context = new DamageBlockContext(event, player);
            TriggerMap.callTriggersByContext(context);
        }

        @Override
        public @Nullable Query<EntityStore> getQuery() {
            return Player.getComponentType();
        }
    }

}
