package com.github.skriptdev.skript.plugin.elements.events;

import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.lang.CodeSection;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.entries.SectionConfiguration;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.registration.context.ContextValue.Usage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Command extends SkriptEvent {

    public static class ScriptCommandContext implements TriggerContext {

        private final String command;
        private final CommandSender sender;
        private final Player player;
        private final World world;

        public ScriptCommandContext(String command, CommandSender sender, Player player, World world) {
            this.command = command;
            this.sender = sender;
            this.player = player;
            this.world = world;
        }

        public String getCommand() {
            return this.command;
        }

        public CommandSender[] getSender() {
            return new CommandSender[]{sender};
        }

        public World[] getWorld() {
            return new World[]{world};
        }

        public Player[] getPlayer() {
            return new Player[]{this.player};
        }

        @Override
        public String getName() {
            return "command context";
        }
    }

    public static void register(SkriptRegistration registration) {
        registration.newEvent(Command.class,
                "[global] command <.+>",
                "player command <.+>",
                "world command <.+>")
            .setHandledContexts(ScriptCommandContext.class)
            .name("Command")
            .description("Create a command.",
                "- `Description` = The description for your command that will show in the commands gui (required).",
                "- `Permission` = The permission required to execute the command (optional).")
            .examples("command /kill:",
                "\tdescription: Kill all the players",
                "\ttrigger:",
                "\t\tkill all players",
                "",
                "player command /clear:",
                "\tpermission: my.script.command.clear",
                "\tdescription: Clear your inventory",
                "\ttrigger:",
                "\t\tclear inventory of player",
                "\t\tsend \"Your inventory has been cleared\" to player",
                "",
                "world command /spawn:",
                "\tdescription: Will teleport all players to the world spawn",
                "\ttrigger:",
                "\t\tteleport all players to spawn location of context-world")
            .since("INSERT VERSION")
            .register();

        registration.newContextValue(ScriptCommandContext.class, Player.class, true,
                "player", ScriptCommandContext::getPlayer)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

        registration.newContextValue(ScriptCommandContext.class, CommandSender.class, true,
                "sender", ScriptCommandContext::getSender)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

        registration.newContextValue(ScriptCommandContext.class, World.class, true,
                "world", ScriptCommandContext::getWorld)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

        registration.newContextValue(ScriptCommandContext.class, String.class, true,
                "command", ct -> new String[]{ct.getCommand()})
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

    }

    private final SectionConfiguration sec = new SectionConfiguration.Builder()
        .addOptionalKey("permission")
        .addOptionalKey("description")
        .addSection("trigger")
        .build();

    private String command;
    private int commandType;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.command = parseContext.getMatches().getFirst().group();
        if (this.command.startsWith("/")) {
            this.command = this.command.substring(1);
        }
        this.commandType = matchedPattern;
        return true;
    }

    @Override
    public List<Statement> loadSection(@NotNull FileSection section, @NotNull ParserState parserState, @NotNull SkriptLogger logger) {
        this.sec.loadConfiguration(null, section, parserState, logger);
        Optional<CodeSection> triggerSec = this.sec.getSection("trigger");
        if (triggerSec.isEmpty()) return List.of();

        CodeSection trigger = triggerSec.get();

        Optional<String> descOption = this.sec.getValue("description", String.class);
        if (descOption.isEmpty()) {
            logger.error("Description cannot be empty", ErrorType.SEMANTIC_ERROR);
            return List.of();
        }
        String description = descOption.get();

        AbstractCommand hyCommand = switch (this.commandType) {
            case 1 -> new AbstractPlayerCommand(this.command, description) {
                @Override
                protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store,
                                       @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {

                    CommandSender sender = commandContext.sender();
                    Player player = store.getComponent(ref, Player.getComponentType());
                    Statement.runAll(trigger, new ScriptCommandContext(Command.this.command, sender, player, world));

                }
            };
            case 2 -> new AbstractWorldCommand(this.command, description) {

                @Override
                protected void execute(@NotNull CommandContext commandContext, @NotNull World world, @NotNull Store<EntityStore> store) {
                    Statement.runAll(trigger, new ScriptCommandContext(Command.this.command, commandContext.sender(), null, world));
                }
            };
            default -> new AbstractCommand(this.command, description) {

                @Override
                protected @Nullable CompletableFuture<Void> execute(@NotNull CommandContext commandContext) {
                    CompletableFuture.runAsync(() -> {
                        CommandSender sender = commandContext.sender();
                        Player player = null;
                        if (sender instanceof Player p) player = p;
                        ScriptCommandContext ctx = new ScriptCommandContext(Command.this.command, sender, player, null);

                        Statement.runAll(trigger, ctx);
                    });
                    return null;
                }
            };
        };
        Optional<String> perm = sec.getValue("permission", String.class);
        perm.ifPresent(hyCommand::requirePermission);
        HySk.getInstance().getCommandRegistry().registerCommand(hyCommand);

        return List.of(trigger);
    }

    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return ctx instanceof ScriptCommandContext sctx && sctx.getCommand().equals(this.command);
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = switch (this.commandType) {
            case 1 -> "player";
            case 2 -> "world";
            default -> "global";
        };
        return type + " command /" + this.command;
    }

}
