package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Hotbar;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Tool;
import com.hypixel.hytale.server.core.inventory.InventoryComponent.Utility;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExprHeldItem implements Expression<ItemStack> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprHeldItem.class, ItemStack.class, true,
                "(held|hot[ ]bar) item of %livingentities%",
                "(utility|off[ ]hand) item of %livingentities%",
                "tool [item] of %livingentities%")
            .name("Held Item")
            .description("Get/set the item in the hotbar, utility or tool slot of a living entity.",
                "**Slots**:",
                "- **Hotbar**: The item in your main hand.",
                "- **Utility**: The off-hand slot, used for secondary items like shields or tools." +
                    "When setting, if you don't actively have your utility slot in use, the first slot will be set and used.",
                "- **Tool**: Not really sure what this is.")
            .examples("set held item of player to itemstack of ingredient_poop",
                "set utility item of player to itemstack of furniture_crude_torch")
            .since("1.0.0")
            .register();
    }

    private int pattern;
    private Expression<LivingEntity> entities;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.pattern = matchedPattern;
        this.entities = (Expression<LivingEntity>) expressions[0];
        return true;
    }

    @Override
    public ItemStack[] getValues(@NotNull TriggerContext ctx) {
        LivingEntity[] entities = this.entities.getArray(ctx);
        ItemStack[] items = new ItemStack[entities.length];

        for (int i = 0; i < entities.length; i++) {
            LivingEntity entity = entities[i];
            if (this.pattern == 0) {
                Hotbar component = EntityUtils.getComponent(entity, Hotbar.getComponentType());
                if (component == null) continue;
                items[i] = component.getActiveItem();
            } else if (this.pattern == 1) {
                Utility component = EntityUtils.getComponent(entity, Utility.getComponentType());
                if (component == null) continue;
                items[i] = component.getActiveItem();
            } else if (this.pattern == 2) {
                Tool component = EntityUtils.getComponent(entity, Tool.getComponentType());
                if (component == null) continue;
                items[i] = component.getActiveItem();
            }
        }
        return items;
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) return Optional.of(new Class<?>[]{ItemStack.class});
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        ItemStack itemStack = ItemStack.EMPTY;
        if (changeWith != null && changeWith.length > 0 && changeWith[0] instanceof ItemStack stack) {
            itemStack = stack;
        }

        for (LivingEntity livingEntity : this.entities.getArray(ctx)) {

            if (this.pattern == 0) {
                Hotbar hotbar = EntityUtils.getComponent(livingEntity, Hotbar.getComponentType());
                if (hotbar == null) continue;

                ItemContainer container = hotbar.getInventory();
                byte activeHotbarSlot = hotbar.getActiveSlot();
                container.setItemStackForSlot(activeHotbarSlot, itemStack);
            } else if (this.pattern == 1) {
                Utility utility = EntityUtils.getComponent(livingEntity, Utility.getComponentType());
                if (utility == null) continue;

                ItemContainer container = utility.getInventory();
                byte activeUtilitySlot = utility.getActiveSlot();
                if (activeUtilitySlot < 0) {
                    activeUtilitySlot = 0;
                    utility.setActiveSlot((byte) 0);
                }
                container.setItemStackForSlot(activeUtilitySlot, itemStack);
            } else if (this.pattern == 2) {
                Tool tool = EntityUtils.getComponent(livingEntity, Tool.getComponentType());
                if (tool == null) continue;

                ItemContainer container = tool.getInventory();
                byte activeToolSlot = tool.getActiveSlot();
                if (activeToolSlot < 0) {
                    tool.setUsingToolsItem(true);
                    tool.setActiveSlot((byte) 1);
                    activeToolSlot = 1;
                }
                container.setItemStackForSlot(activeToolSlot, itemStack);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.entities.isSingle();
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = switch (this.pattern) {
            case 0 -> "hotbar";
            case 1 -> "utility";
            case 2 -> "tool";
            default -> "unknown";
        };
        return type + " item of " + this.entities.toString(ctx, debug);
    }

}
