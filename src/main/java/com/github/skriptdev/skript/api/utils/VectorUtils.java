package com.github.skriptdev.skript.api.utils;

import com.hypixel.hytale.math.vector.Rotation3f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class VectorUtils {

    public static final Vector3f ZERO_3f = new Vector3f(0, 0, 0);
    public static final Vector3d ZERO_3d = new Vector3d(0, 0, 0);

    public static Rotation3f rotFromVec3f(Vector3f vector3f) {
        return new Rotation3f(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public static Vector3f vecFromRot3f(Rotation3f rotation3f) {
        return new Vector3f(rotation3f.x(), rotation3f.y(), rotation3f.z());
    }

}
