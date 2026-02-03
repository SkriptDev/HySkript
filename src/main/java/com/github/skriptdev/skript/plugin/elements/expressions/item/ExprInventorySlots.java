package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExprInventorySlots implements Expression<Number> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprInventorySlots.class, Number.class, false,
                "slots (of|in) %inventory/itemcontainer%")
            .name("Inventory Slots")
            .description("Returns all slots in an Inventory or ItemContainer as numbers.")
            .examples("loop slots of inventory of player:",
                "loop slots of hotbar item container of inventory of player:")
            .since("1.0.0")
            .register();
    }

    private Expression<?> object;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.object = expressions[0];
        return true;
    }

    @Override
    public Number[] getValues(@NotNull TriggerContext ctx) {
        Optional<?> single = this.object.getSingle(ctx);
        if (single.isEmpty()) return null;
        Object object = single.get();

        List<Number> slots = new ArrayList<>();
        if (object instanceof Inventory inventory) {
            int containersSize = inventory.getCombinedEverything().getContainersSize();
            for (int i = 0; i < containersSize; i++) {
                slots.add(i);
            }
        } else if (object instanceof ItemContainer itemContainer) {
            short capacity = itemContainer.getCapacity();
            for (short i = 0; i < capacity; i++) {
                slots.add(i);
            }
        }

        return slots.toArray(Number[]::new);
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "slots of " + this.object.toString(ctx, debug);
    }

}
