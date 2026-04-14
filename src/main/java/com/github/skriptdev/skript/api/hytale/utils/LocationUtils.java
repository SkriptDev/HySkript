package com.github.skriptdev.skript.api.hytale.utils;

import com.hypixel.hytale.math.vector.Location;
import org.joml.Vector3d;

/**
 * Untilities for {@link Location Locations}
 */
public class LocationUtils {

    /**
     * Clone a location.
     * This prevents breaking references
     *
     * @param location Location to clone
     * @return New location instance
     */
    public static Location clone(Location location) {
        return new Location(location.getWorld(),
            new Vector3d(location.getPosition()),
            location.getRotation().clone());
    }

}
