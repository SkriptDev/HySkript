package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExprActiveSlot implements Expression<Number> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprActiveSlot.class, Number.class, false,
                "active hot[ ]bar slot of %livingentities%",
                "active (utility|off[ ]hand) slot of %livingentities%",
                "active tool slot of %livingentities%")
            .name("Active Slot")
            .description("Get/set the active slot of a living entity.",
                "**Note**: This seems extremely borked on the server, so please use with caution.")
            .since("1.0.0")
            .register();
    }

    private int slot;
    private Expression<LivingEntity> entity;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.slot = matchedPattern;
        this.entity = (Expression<LivingEntity>) expressions[0];
        return true;
    }

    @Override
    public Number[] getValues(@NotNull TriggerContext ctx) {
        LivingEntity[] entityArray = this.entity.getArray(ctx);
        Byte[] s = new Byte[entityArray.length];
        for (int i = 0; i < entityArray.length; i++) {
            Inventory inventory = entityArray[i].getInventory();
            if (this.slot == 0) {
                s[i] = inventory.getActiveHotbarSlot();
            } else if (this.slot == 1) {
                s[i] = inventory.getActiveUtilitySlot();
            } else if (this.slot == 2) {
                s[i] = inventory.getActiveToolsSlot();
            }
        }
        return s;
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) return Optional.of(new Class<?>[]{Number.class});
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null) return;
        if (!(changeWith[0] instanceof Number number)) return;
        byte slot = number.byteValue();


        for (LivingEntity entity : this.entity.getArray(ctx)) {
            Inventory inventory = entity.getInventory();
            if (this.slot == 0) {
                byte clamp = (byte) Math.clamp(slot, 0, Inventory.DEFAULT_HOTBAR_CAPACITY - 1);
                inventory.setActiveHotbarSlot(clamp);
            } else if (this.slot == 1) {
                byte clamp = (byte) Math.clamp(slot, -1, Inventory.DEFAULT_UTILITY_CAPACITY - 1);
                inventory.setActiveUtilitySlot(clamp);
            } else if (this.slot == 2) {
                byte clamp = (byte) Math.clamp(slot, -1, Inventory.DEFAULT_TOOLS_CAPACITY - 1);
                inventory.setActiveToolsSlot(clamp);
            }
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = switch (this.slot) {
            case 0 -> "hotbar";
            case 1 -> "utility";
            case 2 -> "tool";
            default -> "unknown";
        };
        return "active " + type + " slot of " + this.entity.toString(ctx, debug);
    }

}
