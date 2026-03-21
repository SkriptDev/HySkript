package com.github.skriptdev.skript.plugin.elements.expressions.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityReferenceUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExprEntityAnimations implements Expression<String> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprEntityAnimations.class, String.class,
                false, "animations of %entities/refs/modelassets%")
            .name("Entity Animations")
            .description("Get the animations of an Entity/Ref.",
                "If using Entity/Ref this will return the possible animations of their current model component.")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<?> owners;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.owners = expressions[0];
        return true;
    }

    @Override
    public String[] getValues(@NotNull TriggerContext ctx) {
        List<String> animations = new ArrayList<>();
        for (Object owner : this.owners.getArray(ctx)) {
            if (owner instanceof ModelAsset modelAsset) {
                Model model = Model.createRandomScaleModel(modelAsset);
                animations.addAll(model.getAnimationSetMap().keySet());
            } else {
                Ref<EntityStore> ref = EntityReferenceUtils.getRef(owner);
                if (ref == null) {
                    return null;
                } else {
                    Store<EntityStore> store = ref.getStore();
                    ModelComponent component = store.getComponent(ref, ModelComponent.getComponentType());
                    if (component == null) return null;

                    animations.addAll(component.getModel().getAnimationSetMap().keySet());
                }
            }
        }

        return animations.stream().sorted().toArray(String[]::new);
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "animations of " + this.owners.toString(ctx, debug);
    }

}
