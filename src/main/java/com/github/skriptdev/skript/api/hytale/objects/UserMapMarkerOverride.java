package com.github.skriptdev.skript.api.hytale.objects;

import com.hypixel.hytale.protocol.packets.worldmap.ContextMenuItem;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarker;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Override of {@link UserMapMarker}.
 * <br>Hytale lacks y-coordinates in map markers, so this class adds them.
 * <br>This class also adds context menu items.
 */
public class UserMapMarkerOverride extends UserMapMarker {

    private float blockY;
    private MapMarker cachedMarker;
    private final List<ContextMenuItem> contextMenuItems = new ArrayList<>();

    public void setPosition(Vector3d pos) {
        this.blockY = (float) pos.y();
        super.setPosition((float) pos.x(), (float) pos.z());
    }

    public void addContextMenuItem(ContextMenuItem contextMenuItem) {
        this.contextMenuItems.add(contextMenuItem);
    }

    @Override
    public MapMarker toProtocolMarker() {
        if (this.cachedMarker == null) {
            this.cachedMarker = super.toProtocolMarker();

            assert this.cachedMarker.transform.position != null;
            this.cachedMarker.transform.position.y = this.blockY;
            if (!this.contextMenuItems.isEmpty()) {
                this.cachedMarker.contextMenuItems = this.contextMenuItems.toArray(new ContextMenuItem[0]);
            }
        }

        return this.cachedMarker;
    }

}
