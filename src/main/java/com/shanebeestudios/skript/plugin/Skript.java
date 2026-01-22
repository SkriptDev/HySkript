package com.shanebeestudios.skript.plugin;

import com.shanebeestudios.skript.api.skript.ScriptsLoader;
import com.shanebeestudios.skript.api.utils.Utils;
import com.shanebeestudios.skript.plugin.elements.ElementRegistration;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class Skript extends SkriptAddon {

    private final HySk hySk;
    private final Path scriptsPath;
    private final SkriptLogger logger;
    private SkriptRegistration registration;
    private ElementRegistration elementRegistration;
    private ScriptsLoader scriptsLoader;

    public Skript(HySk hySk) {
        this.hySk = hySk;
        this.scriptsPath = hySk.getDataDirectory().resolve("scripts");
        this.logger = new SkriptLogger();

        Utils.log("Setting up HySkript!");
        setup();
    }

    private void setup() {
        this.registration = new SkriptRegistration(this);
        this.elementRegistration = new ElementRegistration(this);
        this.elementRegistration.registerElements();

        // FINALIZE SETUP
        this.registration.register();

        Utils.log("HySkript setup complete!");

        // LOAD SCRIPTS
        this.scriptsLoader = new ScriptsLoader(this);
        this.scriptsLoader.loadScripts(this.scriptsPath, false);
    }

    public HySk getPlugin() {
        return this.hySk;
    }

    public Path getScriptsPath() {
        return this.scriptsPath;
    }

    public SkriptLogger getLogger() {
        return this.logger;
    }

    public SkriptRegistration getRegistration() {
        return this.registration;
    }

    public ElementRegistration getElementRegistration() {
        return this.elementRegistration;
    }

    public ScriptsLoader getScriptsLoader() {
        return this.scriptsLoader;
    }

    @Override
    public void handleTrigger(@NotNull Trigger trigger) {
        this.elementRegistration.handleTrigger(trigger);
    }

}
