package com.github.skriptdev.skript.plugin.elements.conditions.ref;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.item.PreventPickup;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CondCanBePickedUp extends PropertyConditional<Ref<EntityStore>> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyConditional(CondCanBePickedUp.class,
                "refs", ConditionalType.CAN, "be picked up")
            .name("Ref - Can Be Picked Up")
            .description("Check if a ref can be picked up.",
                "This generally refers to dropped items.",
                "This can be set, preventing items from being picked up.")
            .since("1.5.0")
            .register();
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return getPerformer().check(ctx, ref -> {
            Store<EntityStore> store = ref.getStore();
            return store.getComponent(ref, PreventPickup.getComponentType()) == null;
        }, isNegated());
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.optionalArrayOf(Boolean.class);
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Boolean bool)) {
            return;
        }
        for (Ref<EntityStore> ref : getPerformer().getArray(ctx)) {
            Store<EntityStore> store = ref.getStore();
            if (bool) {
                store.removeComponentIfExists(ref, PreventPickup.getComponentType());
            } else {
                store.ensureComponent(ref, PreventPickup.getComponentType());
            }
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return getPerformer().toString(ctx, debug) + " can be picked up";
    }

}
