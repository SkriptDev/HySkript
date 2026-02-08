package com.github.skriptdev.skript.api.skript.config;

import com.github.skriptdev.skript.api.utils.Utils;
import com.github.skriptdev.skript.plugin.Skript;
import io.github.syst3ms.skriptparser.config.Config;
import io.github.syst3ms.skriptparser.config.Config.ConfigSection;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * Config for Skript
 */
public class SkriptConfig {

    private final Config config;
    private final boolean debug;
    private final int maxTargetBlockDistance;
    private final ConfigSection effectCommands;
    private final ConfigSection databases;

    public SkriptConfig(Skript skript) {
        Path skriptConfigPath = skript.getPlugin().getDataDirectory().resolve("config.sk");
        SkriptLogger logger = new SkriptLogger(true);
        this.config = new Config(skriptConfigPath, "/config.sk", logger);

        // Set up debug mode
        this.debug = this.config.getBoolean("debug");
        Utils.setDebug(this.debug);

        // Set up max-target-block-distance
        this.maxTargetBlockDistance = this.config.getInt("max-target-block-distance");
        if (this.maxTargetBlockDistance == -1) {
            // This would happen if the config is missing this value
            // TODO update config
        } else if (this.maxTargetBlockDistance < 0) {
            logger.error("max-target-block-distance must be greater than or equal to 0", ErrorType.STRUCTURE_ERROR);
        }

        // Set up effect commands
        this.effectCommands = this.config.getConfigSection("effect-commands");
        if (this.effectCommands == null) {
            logger.error("Effect commands section not found in config.sk", ErrorType.STRUCTURE_ERROR);
            // TODO update config
        }

        // Set up databases
        this.databases = this.config.getConfigSection("databases");
        if (this.databases == null) {
            logger.error("Databases section not found in config.sk", ErrorType.STRUCTURE_ERROR);
            // TODO update config
        }


        logger.finalizeLogs();
        for (LogEntry logEntry : logger.close()) {
            Utils.log(null, logEntry);
        }
    }

    @SuppressWarnings("unused")
    public Config getBaseConfig() {
        return this.config;
    }

    /**
     * Get whether debug mode is enabled
     *
     * @return Whether debug mode is enabled
     */
    public boolean getDebug() {
        return this.debug;
    }

    /**
     * Get the maximum target block distance.
     *
     * @return Maximum target block distance.
     */
    public int getMaxTargetBlockDistance() {
        return this.maxTargetBlockDistance;
    }

    /**
     * Get the effect commands section.
     *
     * @return Effect commands section.
     */
    public @Nullable ConfigSection getEffectCommands() {
        return this.effectCommands;
    }

    /**
     * Get the databases section.
     *
     * @return Databases section.
     */
    public @Nullable ConfigSection getDatabases() {
        return this.databases;
    }

}
