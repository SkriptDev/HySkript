package com.github.skriptdev.skript.plugin.elements.command;

import com.github.skriptdev.skript.api.skript.command.ScriptCommandBuilder;
import com.github.skriptdev.skript.api.skript.command.ScriptCommandParent;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Structure;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.registration.context.ContextValue.Usage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScriptCommand extends Structure implements ScriptCommandParent {

    @Override
    public void loadChild(ScriptCommandBuilder parent, SkriptLogger logger) {
        // UNUSED IN TOP LEVEL COMMAND
    }

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

        public CommandSender getSender() {
            return this.sender;
        }

        public World getWorld() {
            return this.world;
        }

        public Player getPlayer() {
            if (this.player == null && this.sender instanceof Player p) return p;
            return this.player;
        }

        @Override
        public String getName() {
            return "command context";
        }
    }

    public static void register(SkriptRegistration registration) {
        registration.newEvent(ScriptCommand.class,
                "*[global] command <.+>",
                "*player command <.+>",
                "*world command <.+>")
            .setHandledContexts(ScriptCommandContext.class)
            .name("Command")
            .description("Create a command.",
                "**Command Format**:",
                "- `<global/player/world> command /command_name (args)`",
                "",
                "**Argument Formats**:",
                "- `<type>`",
                "- `<name:type>`",
                "- `<type:\"description\">`",
                "- `<name:type:\"description\">`",
                "- <> = Makes the argument required.",
                "- [<>] = Makes the argument optional.",
                "- Type = The type of argument to use (required).",
                "- Name = The name of the argument, this will be used to create local variables (optional).",
                "- Description = The description of the argument, this is show in the command GUI (optional).",
                "",
                "**Entries**:",
                "- `Description` = The description for your command that will show in the commands gui (optional).",
                "- `Permission` = The permission required to execute the command (optional).",
                "- `Aliases` = A list of aliases for the command (optional).")
            .examples("command /kill:",
                "\tdescription: Kill all the players",
                "\ttrigger:",
                "\t\tkill all players",
                "",
                "command /home [<name:string:\"Name of home\">]:",
                "\ttrigger:",
                "\t\tif {_name} is set:",
                "\t\t\tteleport player to {homes::%{_name}%}",
                "\t\telse:",
                "\t\t\tteleport player to {homes::default}",
                "",
                "command /broadcast <message:string:\"Message to broadcast\">:",
                "\ttrigger:",
                "\t\tbroadcast {_message}",
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
            .since("1.0.0")
            .register();

        registration.newSingleContextValue(ScriptCommandContext.class, Player.class,
                "player", ScriptCommandContext::getPlayer)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

        registration.newSingleContextValue(ScriptCommandContext.class, CommandSender.class,
                "sender", ScriptCommandContext::getSender)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

        registration.newSingleContextValue(ScriptCommandContext.class, World.class,
                "world", ScriptCommandContext::getWorld)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

        registration.newSingleContextValue(ScriptCommandContext.class, String.class,
                "command", ScriptCommandContext::getCommand)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

    }

    private ScriptCommandBuilder commandBuilder;

    private int commandType;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.commandType = matchedPattern;
        this.commandBuilder = ScriptCommandBuilder.create(matchedPattern, parseContext.getLogger());
        return this.commandBuilder.parseCommandLine(parseContext.getMatches().getFirst().group());
    }

    @Override
    public List<Statement> loadSection(@NotNull FileSection section, @NotNull ParserState parserState, @NotNull SkriptLogger logger) {
        List<Statement> statements = this.commandBuilder.setupCommand(section, parserState, logger);
        this.commandBuilder.build(null);
        return statements;
    }


    @Override
    public boolean check(@NotNull TriggerContext ctx) {
        return ctx instanceof ScriptCommandContext sctx && sctx.getCommand().equals(this.commandBuilder.getCommandName());
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        String type = switch (this.commandType) {
            case 1 -> "player";
            case 2 -> "world";
            default -> "global";
        };
        return type + " command /" + this.commandBuilder.getCommandName();
    }

}
