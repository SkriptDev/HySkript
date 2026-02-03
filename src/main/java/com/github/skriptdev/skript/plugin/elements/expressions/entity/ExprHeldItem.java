package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
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
            Inventory inventory = entities[i].getInventory();
            if (this.pattern == 0) {
                items[i] = inventory.getActiveHotbarItem();
            } else if (this.pattern == 1) {
                items[i] = inventory.getUtilityItem();
            } else if (this.pattern == 2) {
                items[i] = inventory.getActiveToolItem();
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
        if (changeWith == null) return;

        ItemStack itemStack = ((ItemStack) changeWith[0]);

        for (LivingEntity livingEntity : this.entities.getArray(ctx)) {
            Inventory inventory = livingEntity.getInventory();
            if (this.pattern == 0) {
                ItemContainer hotbar = inventory.getHotbar();
                byte activeHotbarSlot = inventory.getActiveHotbarSlot();
                hotbar.setItemStackForSlot(activeHotbarSlot, itemStack);
            } else if (this.pattern == 1) {
                ItemContainer utility = inventory.getUtility();
                byte activeUtilitySlot = inventory.getActiveUtilitySlot();
                if (activeUtilitySlot < 0) {
                    activeUtilitySlot = 0;
                    inventory.setActiveToolsSlot((byte) 0);
                }
                utility.setItemStackForSlot(activeUtilitySlot, itemStack);
            } else if (this.pattern == 2) {
                ItemContainer tools = inventory.getTools();
                byte activeToolSlot = inventory.getActiveToolsSlot();
                if (activeToolSlot < 0) {
                    inventory.setUsingToolsItem(true);
                    inventory.setActiveToolsSlot((byte) 1);
                    activeToolSlot = 1;
                }
                tools.setItemStackForSlot(activeToolSlot, itemStack);
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
