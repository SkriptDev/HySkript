package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Hotbar;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Tool;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Utility;
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
            LivingEntity livingEntity = entityArray[i];
            if (this.slot == 0) {
                Hotbar component = EntityUtils.getComponent(livingEntity, Hotbar.getComponentType());
                if (component != null) {
                    s[i] = component.getActiveSlot();
                } else {
                    s[i] = -1;
                }
            } else if (this.slot == 1) {
                Utility component = EntityUtils.getComponent(livingEntity, Utility.getComponentType());
                if (component != null) {
                    s[i] = component.getActiveSlot();
                } else {
                    s[i] = -1;
                }
            } else if (this.slot == 2) {
                Tool component = EntityUtils.getComponent(livingEntity, Tool.getComponentType());
                if (component != null) {
                    s[i] = component.getActiveSlot();
                } else {
                    s[i] = -1;
                }
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
            if (this.slot == 0) {
                byte clamp = (byte) Math.clamp(slot, 0, InventoryComponent.DEFAULT_HOTBAR_CAPACITY - 1);
                Hotbar component = EntityUtils.getComponent(entity, Hotbar.getComponentType());
                if (component != null) {
                    component.setActiveSlot(clamp);
                    component.markDirty();

                }
            } else if (this.slot == 1) {
                byte clamp = (byte) Math.clamp(slot, -1, InventoryComponent.DEFAULT_UTILITY_CAPACITY - 1);
                Utility component = EntityUtils.getComponent(entity, Utility.getComponentType());
                if (component != null) {
                    component.setActiveSlot(clamp);
                    component.markDirty();
                }
            } else if (this.slot == 2) {
                byte clamp = (byte) Math.clamp(slot, -1, InventoryComponent.DEFAULT_TOOLS_CAPACITY - 1);
                Tool component = EntityUtils.getComponent(entity, Tool.getComponentType());
                if (component != null) {
                    component.setActiveSlot(clamp);
                    component.markDirty();
                }
            }
            entity.invalidateEquipmentNetwork();
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
