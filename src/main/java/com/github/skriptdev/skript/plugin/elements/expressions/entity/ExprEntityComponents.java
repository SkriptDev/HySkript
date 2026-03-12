package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// Just a little something for debugging
public class ExprEntityComponents implements Expression<String> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprEntityComponents.class, String.class, false,
                "components of %entity/ref%")
            .noDoc()
            .register();
    }

    private Expression<?> entity;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.entity = expressions[0];
        return true;
    }

    @SuppressWarnings({"unchecked", "PatternVariableHidesField"})
    @Override
    public String[] getValues(@NotNull TriggerContext ctx) {
        List<String> components = new ArrayList<>();
        Object object = this.entity.getSingle(ctx).orElse(null);
        if (object == null) return null;

        Ref<EntityStore> reference;
        if (object instanceof Entity entity) {
            reference = entity.getReference();
        } else if (object instanceof Ref<?> ref) {
            reference = (Ref<EntityStore>) ref;
        } else {
            return null;
        }
        assert reference != null;

        Archetype<EntityStore> archetype = reference.getStore().getArchetype(reference);
        for (int i = 0; i < archetype.length(); i++) {
            ComponentType<EntityStore, ?> type = archetype.get(i);
            if (type == null) continue;
            components.add(type.getTypeClass().getSimpleName());

        }

        return components.toArray(String[]::new);
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "components of " + this.entity.toString(ctx, debug);
    }

}
