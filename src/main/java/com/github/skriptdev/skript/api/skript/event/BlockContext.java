package com.github.skriptdev.skript.api.skript.event;

import com.github.skriptdev.skript.api.hytale.Block;
import io.github.syst3ms.skriptparser.lang.TriggerContext;

/**
 * Represents a {@link TriggerContext} which includes a {@link Block}
 */
public interface BlockContext extends TriggerContext {

    Block getBlock();

}
