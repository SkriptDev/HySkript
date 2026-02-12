package com.github.skriptdev.skript.plugin.elements.effects.block;

import com.github.skriptdev.skript.api.hytale.Block;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EffDamageBlock extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffDamageBlock.class, "block damage %blocks% by %number%",
                "make %livingEntity% block damage %blocks% by %number%",
                "make %livingEntity% block damage %blocks% by %number% using %itemstack%")
            .name("Damage Block")
            .description("Damages the specified blocks by the specified amount.",
                "You can optionally include a LivingEntity to destroy the block,",
                "and an ItemStack to simulate the damage as if the entity was holding that item.")
            .examples("block damage {_block} by 10",
                "make player block damage {_block} by 10",
                "make player block damage target block of player by 10 using hotbar item of player")
            .experimental("Currently the ItemStack thing doesn't work.")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<LivingEntity> livingEntity;
    private Expression<Block> blocks;
    private Expression<Number> damage;
    private Expression<ItemStack> itemstack;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        if (matchedPattern == 0) {
            this.blocks = (Expression<Block>) expressions[0];
            this.damage = (Expression<Number>) expressions[1];
        } else if (matchedPattern == 1) {
            this.livingEntity = (Expression<LivingEntity>) expressions[0];
            this.blocks = (Expression<Block>) expressions[1];
            this.damage = (Expression<Number>) expressions[2];
        } else if (matchedPattern == 2) {
            this.livingEntity = (Expression<LivingEntity>) expressions[0];
            this.blocks = (Expression<Block>) expressions[1];
            this.damage = (Expression<Number>) expressions[2];
            this.itemstack = (Expression<ItemStack>) expressions[3];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        Number number = this.damage.getSingle(ctx).orElse(null);
        if (number == null) return;

        LivingEntity performer = null;
        if (this.livingEntity != null) {
            performer = this.livingEntity.getSingle(ctx).orElse(null);
        }
        ItemStack itemStack = null;
        if (this.itemstack != null) {
            itemStack = this.itemstack.getSingle(ctx).orElse(null);
        }

        float damage = number.floatValue();
        for (Block block : this.blocks.getArray(ctx)) {
            block.damage(performer, itemStack, damage);
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String performer = this.livingEntity == null ? "" : "make " + this.livingEntity.toString(ctx, debug) + " ";
        String item = this.itemstack == null ? "" : "using " + this.itemstack.toString(ctx, debug);
        return performer + "block damage " + this.blocks.toString(ctx, debug) + " by " + this.damage.toString(ctx, debug) + item;
    }

}
