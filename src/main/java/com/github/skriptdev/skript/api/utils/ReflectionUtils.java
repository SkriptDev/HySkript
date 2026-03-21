package com.github.skriptdev.skript.api.utils;

import com.hypixel.hytale.server.core.modules.accesscontrol.AccessControlModule;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Utilities for reflection operations.
 */
public class ReflectionUtils {

    private static HytaleBanProvider BAN_PROVIDER;

    /**
     * @hidden
     */
    public static void init() {
        AccessControlModule accessControlModule = AccessControlModule.get();
        try {
            Field banProvider = AccessControlModule.class.getDeclaredField("banProvider");
            banProvider.setAccessible(true);
            BAN_PROVIDER = (HytaleBanProvider) banProvider.get(accessControlModule);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Utils.error("Failed to get HytaleBanProvider: %s", e.getMessage());
        }
    }

    /**
     * Get access to the HytaleBanProvider.
     * <br>This is currently private with no getter.
     *
     * @return HytaleBanProvider
     */
    public static @Nullable HytaleBanProvider getBanProvider() {
        return BAN_PROVIDER;
    }

    /**
     * Set world override time durations of a world.
     * <br>I didn't want to do this, but Hytale doesn't have a setter and this is private.
     *
     * @param world   World to change times for
     * @param seconds Seconds to override
     * @param day     Whether to override daytime or nighttime duration
     */
    public static void setWorldTimeOverrides(@NotNull World world, @Nullable Integer seconds, boolean day) {
        WorldConfig worldConfig = world.getWorldConfig();
        try {
            Field field;
            if (day) {
                field = worldConfig.getClass().getDeclaredField("daytimeDurationSecondsOverride");
            } else {
                field = worldConfig.getClass().getDeclaredField("nighttimeDurationSecondsOverride");
            }
            field.setAccessible(true);
            field.set(worldConfig, seconds);
        } catch (NoSuchFieldException | IllegalAccessException ignore) {

        }
    }

}
