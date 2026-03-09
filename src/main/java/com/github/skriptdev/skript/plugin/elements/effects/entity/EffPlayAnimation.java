package com.github.skriptdev.skript.plugin.elements.effects.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityReferenceUtils;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EffPlayAnimation extends Effect {

    public static void register(SkriptRegistration reg) {
        reg.newEffect(EffPlayAnimation.class,
                "play animation %string% on %entities/refs%",
                "play %animationslot% animation %string% on %entities/refs%")
            .name("Play Animation")
            .description("Plays an animation on the specified entity.",
                "If the animation slot is not specified, the action slot will be used.")
            // TODO add note about emotes when they're available in update 4
            .examples("play animation \"Death\" on player",
                "play action animation \"Eat\" on target entity of player",
                "play movement animation \"Jump\" on player")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<String> animation;
    private Expression<AnimationSlot> slot;
    private Expression<?> owners;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        if (matchedPattern == 0) {
            this.animation = (Expression<String>) expressions[0];
            this.owners = expressions[1];
        } else {
            this.slot = (Expression<AnimationSlot>) expressions[0];
            this.animation = (Expression<String>) expressions[1];
            this.owners = expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext ctx) {
        String animation = this.animation.getSingle(ctx).orElse(null);
        if (animation == null) return;

        AnimationSlot slot = AnimationSlot.Action;
        if (this.slot != null) {
            Optional<? extends AnimationSlot> single = this.slot.getSingle(ctx);
            if (single.isPresent()) slot = single.get();
        }

        for (Object o : this.owners.getArray(ctx)) {
            Ref<EntityStore> ref = EntityReferenceUtils.getRef(o);
            if (ref == null) continue;

            Store<EntityStore> store = ref.getStore();

            AnimationUtils.playAnimation(ref, slot, animation, true, store);
        }
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        if (this.slot != null) {
            return "play animation " + this.animation.toString(ctx, debug) +
                " in " + this.slot.toString(ctx, debug) + " slot on " + this.owners.toString(ctx, debug);
        }
        return "play animation " + this.animation.toString(ctx, debug) +
            " on " + this.owners.toString(ctx, debug);
    }

}
