package com.github.skriptdev.skript.plugin.command;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.github.skriptdev.skript.api.utils.Utils;
import com.github.skriptdev.skript.plugin.HySk;
import com.github.skriptdev.skript.plugin.Skript;
import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.docs.Documentation;
import io.github.syst3ms.skriptparser.lang.CodeSection;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.pattern.PatternElement;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.registration.SyntaxInfo;
import io.github.syst3ms.skriptparser.registration.context.ContextValue;
import io.github.syst3ms.skriptparser.types.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkriptCommand extends AbstractCommandCollection {

    public SkriptCommand(CommandRegistry registry) {
        super("skript", "Skript commands");
        addAliases("sk");

        addSubCommand(reloadCommand());
        addSubCommand(docsCommand());

        registry.registerCommand(this);
    }

    private AbstractCommand reloadCommand() {
        return new AbstractCommand("reload", "Reloads all scripts.") {
            @Override
            protected CompletableFuture<Void> execute(@NotNull CommandContext commandContext) {
                return CompletableFuture.runAsync(() -> {
                    Skript skript = HySk.getInstance().getSkript();
                    skript.getElementRegistration().clearTriggers();
                    skript.getScriptsLoader().loadScripts(skript.getScriptsPath(), true);
                });
            }
        };
    }

    private AbstractCommand docsCommand() {
        return new AbstractCommand("docs", "Print docs to file.") {
            @Override
            protected CompletableFuture<Void> execute(@NotNull CommandContext commandContext) {
                return CompletableFuture.runAsync(() -> print());
            }
        };
    }

    private @NotNull File getFile(String name) {
        File file = HySk.getInstance().getDataDirectory().resolve("docs/" + name + ".md").toFile();
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    throw new RuntimeException("Unable to create docs directory.");
                } else {
                    Utils.log("Created docs directory.");
                }
            }
            try {
                if (!file.createNewFile()) {
                    Utils.error("Failed to create " + name + ".md file!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    private void print() {
        Skript skript = HySk.getInstance().getSkript();
        SkriptRegistration registration = skript.getRegistration();

        try {
            Utils.log("Printing documentation");

            // EXPRESSIONS
            Utils.log("Printing expressions and conditions");
            File file = getFile("expressions");
            PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
            PrintWriter condWriter = new PrintWriter(getFile("conditions"), StandardCharsets.UTF_8);
            printExpressions(writer, condWriter, registration);
            printExpressions(writer, condWriter, Parser.getMainRegistration());

            condWriter.close();
            writer.close();

            // EFFECTS
            Utils.log("Printing effects");
            file = getFile("effects");

            writer = new PrintWriter(file, StandardCharsets.UTF_8);
            printEffects(writer, registration);
            printEffects(writer, Parser.getMainRegistration());

            writer.close();

            // EVENTS
            Utils.log("Printing events");
            file = getFile("events");
            writer = new PrintWriter(file, StandardCharsets.UTF_8);
            printEvents(writer, registration);
            printEvents(writer, Parser.getMainRegistration());
            writer.close();

            // TYPES
            Utils.log("Printing types");
            file = getFile("types");
            writer = new PrintWriter(file, StandardCharsets.UTF_8);
            printTypes(writer, registration);
            printTypes(writer, Parser.getMainRegistration());
            writer.close();

            // SECTIONS
            Utils.log("Printing sections");
            file = getFile("sections");
            writer = new PrintWriter(file, StandardCharsets.UTF_8);
            printSections(writer, registration);
            printSections(writer, Parser.getMainRegistration());
            writer.close();

            Utils.log("Documentation printed!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printEvents(PrintWriter writer, SkriptRegistration registration) {
        List<ContextValue<?, ?>> contextValues = registration.getContextValues();

        registration.getEvents().forEach(event -> {
            Documentation documentation = event.getDocumentation();
            printDocumentation("Event", writer, documentation, event.getPatterns());

            List<ContextValue<?, ?>> valuesForThisEvent = new ArrayList<>();
            contextValues.forEach(contextValue -> {
                if (event.getContexts().contains(contextValue.getContext())) {
                    valuesForThisEvent.add(contextValue);
                }
            });
            if (!valuesForThisEvent.isEmpty()) {
                writer.println("- **ContextValues**:");
                valuesForThisEvent.forEach(contextValue -> writer.println("   - `context-" + contextValue.getPattern() + "`"));
            }
        });
    }

    private void printExpressions(PrintWriter exprWriter, PrintWriter condWriter, SkriptRegistration registration) {
        registration.getExpressions().forEach((aClass, expressionInfos) -> {
            expressionInfos.forEach(expressionInfo -> {
                Documentation documentation = expressionInfo.getDocumentation();
                if (expressionInfo.getSyntaxClass().getSimpleName().startsWith("Cond")) {
                    printDocumentation("Condition", condWriter, documentation, expressionInfo.getPatterns());
                    condWriter.println("- **Return Type**: " + expressionInfo.getReturnType());
                } else {
                    printDocumentation("Expression", exprWriter, documentation, expressionInfo.getPatterns());
                    exprWriter.println("- **Return Type**: " + expressionInfo.getReturnType());
                }
            });
            exprWriter.println();
        });
    }

    private void printEffects(PrintWriter writer, SkriptRegistration registration) {
        for (SyntaxInfo<? extends Effect> effect : registration.getEffects()) {
            Documentation documentation = effect.getDocumentation();
            printDocumentation("Effect", writer, documentation, effect.getPatterns());
        }
    }

    private void printTypes(PrintWriter writer, SkriptRegistration registration) {
        List<Type<?>> types = new ArrayList<>(registration.getTypes());
        types.sort(Comparator.comparing(Type::getBaseName));
        types.forEach(type -> {
            Documentation documentation = type.getDocumentation();
            printDocumentation("Type", writer, documentation, List.of());
        });
    }

    private void printSections(PrintWriter writer, SkriptRegistration registration) {
        for (SyntaxInfo<? extends CodeSection> section : registration.getSections()) {
            Documentation documentation = section.getDocumentation();
            printDocumentation("Section", writer, documentation, section.getPatterns());
        }
    }

    private void printDocumentation(String type, PrintWriter writer, Documentation documentation, List<PatternElement> patterns) {
        writer.println("### " + type + ": " + documentation.getName());
        String[] description = documentation.getDescription();
        if (description != null) {
            writer.println("- **Description**:");
            for (String s : description) {
                writer.println("  " + s);
            }
        }
        if (documentation.getUsage() != null) {
            writer.println("- **Usage**:  ");
            String usage = documentation.getUsage();
            if (usage.length() > 200) {
                // Asset store stuff gets really long, so plop them on another page
                // GitHub's wiki pages seem to have a limit
                Utils.log("Creating asset store link for: " + documentation.getName());
                writer.println("[Click Here](https://github.com/SkriptDev/HySkript/wiki/usage-" + documentation.getName().replace(" ", "-") + ")");
                File usageFile = getFile("usage-" + documentation.getName());
                try {
                    PrintWriter usageWriter = new PrintWriter(usageFile, StandardCharsets.UTF_8);
                    usageWriter.println("### Usage: " + documentation.getName());
                    usageWriter.println("```");
                    for (String s : usage.split(", ")) {
                        usageWriter.println(s);
                    }
                    usageWriter.println("```");
                    usageWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                writer.println("`");
                writer.println(documentation.getUsage());
                writer.println("`");
            }
        }
        if (!patterns.isEmpty()) {
            writer.println("- **Patterns**:");
            patterns.forEach(pattern -> writer.println("   - `" + pattern + "`"));
        }
        if (documentation.getExamples() != null) {
            writer.println("- **Examples**:  ");
            writer.println("```applescript");
            for (String s : documentation.getExamples()) {
                writer.println(s);
            }
            writer.println("```");
        }
        if (documentation.getSince() != null) {
            writer.println("- **Since**: " + documentation.getSince());
        }
    }

}
