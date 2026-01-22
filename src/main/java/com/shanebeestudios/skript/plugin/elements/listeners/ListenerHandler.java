package com.shanebeestudios.skript.plugin.elements.listeners;

import com.hypixel.hytale.event.EventRegistry;
import com.shanebeestudios.skript.plugin.Skript;
import com.shanebeestudios.skript.plugin.elements.events.EvtLoad;
import com.shanebeestudios.skript.plugin.elements.events.EvtPlayerJoin;
import com.shanebeestudios.skript.api.skript.eventcontext.ScriptLoadContext;
import io.github.syst3ms.skriptparser.event.EvtPeriodical;
import io.github.syst3ms.skriptparser.event.PeriodicalContext;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.util.ThreadUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ListenerHandler {

    private final Skript skript;
    private final PlayerJoinListener playerJoinListener;
    private final List<Trigger> onLoadTriggers = new ArrayList<>();
    private final List<Trigger> periodicalTriggers = new ArrayList<>();

    public ListenerHandler(Skript skript, EventRegistry registry) {
        this.skript = skript;
        this.playerJoinListener = new PlayerJoinListener(registry);
    }

    public void handleTrigger(Trigger trigger) {
        SkriptEvent event = trigger.getEvent();

        if (!this.skript.canHandleEvent(event))
            return;

        switch (event) {
            case EvtLoad ignored -> this.onLoadTriggers.add(trigger);
            case EvtPeriodical ignored -> this.periodicalTriggers.add(trigger);
            case EvtPlayerJoin evtPlayerJoin -> this.playerJoinListener.addTrigger(trigger, evtPlayerJoin.getPattern());
            default -> {
            }
        }
    }

    public void finishedLoading() {
        for (Trigger trigger : this.onLoadTriggers) {
            Statement.runAll(trigger, new ScriptLoadContext());
        }
        for (Trigger trigger : this.periodicalTriggers) {
            PeriodicalContext ctx = new PeriodicalContext();
            Duration dur = ((EvtPeriodical) trigger.getEvent()).getDuration().getSingle(ctx).orElseThrow(AssertionError::new);
            ThreadUtils.runPeriodically(() -> Statement.runAll(trigger, ctx), dur);
        }
    }

    public void clearTriggers() {
        this.onLoadTriggers.clear();
        this.periodicalTriggers.clear();
        this.playerJoinListener.clearTriggers();
    }

}
