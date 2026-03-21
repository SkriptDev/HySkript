package com.github.skriptdev.skript.plugin.elements.sections.entity;

import com.github.skriptdev.skript.api.hytale.utils.EntityUtils;
import com.github.skriptdev.skript.api.skript.event.RefContext;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.CodeSection;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.variables.Variables;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class SecSpawnDisplay extends CodeSection {

    public static void register(SkriptRegistration reg) {
        reg.newSection(SecSpawnDisplay.class,
                "spawn display item %item% at %location%",
                "spawn display block %blocktype% at %location%",
                "spawn display model %modelasset% at %location%")
            .name("Spawn Display")
            .description("Spawn an item at a location.")
            .examples("spawn display item ingredient_poop at {_loc}:",
                "\tset {_e} to event-ref",
                "\tset scale of {_e} to 10")
            .since("1.5.0")
            .register();
    }

    int pattern;
    private Expression<?> model;
    private Expression<Location> location;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        this.pattern = matchedPattern;
        this.model = expressions[0];
        this.location = (Expression<Location>) expressions[1];

        ParserState parserState = parseContext.getParserState();
        List<Class<? extends TriggerContext>> triggerContexts = new ArrayList<>(parserState.getCurrentContexts().stream().toList());
        triggerContexts.add(ModelContext.class);
        parserState.setCurrentContexts(new HashSet<>(triggerContexts));

        return true;
    }

    @Override
    public Optional<? extends Statement> walk(@NotNull TriggerContext ctx) {
        Optional<? extends Statement> nextStatement = getNext();

        Object o = this.model.getSingle(ctx).orElse(null);
        Location location = this.location.getSingle(ctx).orElse(null);
        if (o == null || location == null) return nextStatement;

        Ref<EntityStore> ref = EntityUtils.spawnModel(o, location);
        Optional<? extends Statement> first = getFirst();

        if (ref != null && first.isPresent()) {
            ModelContext modelContext = new ModelContext(ref);
            Variables.copyLocalVariables(ctx, modelContext);
            Statement.runAll(first.get(), modelContext);
            Variables.copyLocalVariables(modelContext, ctx);
            Variables.clearLocalVariables(modelContext);
        }
        return nextStatement;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = switch (this.pattern) {
            case 1 -> "block";
            case 2 -> "model";
            default -> "item";
        };
        return "spawn display " + type + " " + this.model.toString(ctx, debug) + " at " + this.location.toString(ctx, debug);
    }

    public static class ModelContext implements RefContext<EntityStore> {
        private final Ref<EntityStore> ref;

        public ModelContext(Ref<EntityStore> ref) {
            this.ref = ref;
        }

        public Ref<EntityStore> getRef() {
            return this.ref;
        }

        @Override
        public String getName() {
            return "model context";
        }
    }

}
