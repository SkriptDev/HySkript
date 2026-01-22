package com.shanebeestudios.skript.plugin.elements;

import com.shanebeestudios.skript.plugin.Skript;
import com.shanebeestudios.skript.plugin.elements.conditions.ConditionHandler;
import com.shanebeestudios.skript.plugin.elements.effects.EffectHandler;
import com.shanebeestudios.skript.plugin.elements.events.EventHandler;
import com.shanebeestudios.skript.plugin.elements.expressions.ExpressionHandler;
import com.shanebeestudios.skript.plugin.elements.listeners.ListenerHandler;
import com.shanebeestudios.skript.plugin.elements.types.Types;
import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.jetbrains.annotations.NotNull;

public class ElementRegistration {

    private final SkriptRegistration registration;
    private final ListenerHandler listenerHandler;

    public ElementRegistration(Skript skript) {
        this.registration = skript.getRegistration();
        this.listenerHandler = new ListenerHandler(skript, skript.getPlugin().getEventRegistry());
    }

    public void registerElements() {
        Parser.init(new String[0], new String[0], new String[0], true);

        // TYPES
        Types.register(this.registration);
        TypeManager.register(this.registration);

        // CONDITIONS
        ConditionHandler.register(this.registration);

        // EFFECTS
        EffectHandler.register(this.registration);

        // EXPRESSIONS
        ExpressionHandler.register(this.registration);

        // EVENTS
        EventHandler.register(this.registration);
    }

    public ListenerHandler getListenerHandler() {
        return this.listenerHandler;
    }

    public void clearTriggers() {
        this.listenerHandler.clearTriggers();
    }

    public void handleTrigger(@NotNull Trigger trigger) {
        this.listenerHandler.handleTrigger(trigger);
    }

    public void finishedLoading() {
        this.listenerHandler.finishedLoading();
    }

}
