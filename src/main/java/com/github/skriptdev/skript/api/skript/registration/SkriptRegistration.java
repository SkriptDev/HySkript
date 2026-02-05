package com.github.skriptdev.skript.api.skript.registration;

import com.github.skriptdev.skript.api.utils.Utils;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;

import java.util.List;

/**
 * An extension of {@link io.github.syst3ms.skriptparser.registration.SkriptRegistration} with additional features.
 */
public class SkriptRegistration extends io.github.syst3ms.skriptparser.registration.SkriptRegistration {

    public SkriptRegistration(SkriptAddon registerer) {
        super(registerer);
    }

    @Override
    public List<LogEntry> register() {
        for (LogEntry logEntry : super.register()) {
            Utils.log(null, logEntry);
        }
        return List.of();
    }

}
