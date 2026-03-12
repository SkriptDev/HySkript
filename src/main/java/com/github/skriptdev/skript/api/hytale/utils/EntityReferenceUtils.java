package com.github.skriptdev.skript.api.hytale.utils;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities for {@link EntityStore} {@link Ref References}
 */
@SuppressWarnings("unused")
public class EntityReferenceUtils {

    private static final List<ReferenceType<?>> TYPES = new ArrayList<>();
    private static final Map<Class<? extends Component<?>>, ReferenceType<?>> TYPES_MAP = new HashMap<>();

    public static final ReferenceType<Player> PLAYER = new ReferenceType<>(
        "player", Player.class, Player.getComponentType());
    public static final ReferenceType<NPCEntity> NPC_ENTITY = new ReferenceType<>(
        "npcentity", NPCEntity.class, NPCEntity.getComponentType());
    public static final ReferenceType<ItemComponent> ITEM = new ReferenceType<>(
        "item", ItemComponent.class, ItemComponent.getComponentType());

    public static List<ReferenceType<?>> getTypes() {
        return TYPES;
    }

    public static ReferenceType<?> getType(Class<? extends Component<?>> componentClass) {
        return TYPES_MAP.get(componentClass);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable Ref<EntityStore> getRef(Object o) {
        if (o instanceof Ref<?> ref && ref.isValid()) {
            return (Ref<EntityStore>) ref;
        } else if (o instanceof Entity entity) {
            Ref<EntityStore> reference = entity.getReference();
            if (reference != null && reference.isValid()) return reference;
        }
        return null;
    }

    public static List<Ref<EntityStore>> getRefsInSphere(@Nonnull Vector3d pos, double radius, @Nonnull Store<EntityStore> store) {
        List<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
        EntityModule entityModule = EntityModule.get();
        SpatialResource<Ref<EntityStore>, EntityStore> entities = store.getResource(entityModule.getEntitySpatialResourceType());
        entities.getSpatialStructure().collect(pos, (float) radius, results);
        SpatialResource<Ref<EntityStore>, EntityStore> players = store.getResource(entityModule.getPlayerSpatialResourceType());
        players.getSpatialStructure().collect(pos, (float) radius, results);
        SpatialResource<Ref<EntityStore>, EntityStore> items = store.getResource(entityModule.getItemSpatialResourceType());
        items.getSpatialStructure().collect(pos, (float) radius, results);
        return results;
    }

    @Nonnull
    public static List<Ref<EntityStore>> getRefsInBox(@Nonnull Vector3d min, @Nonnull Vector3d max, @Nonnull Store<EntityStore> store) {
        List<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
        EntityModule entityModule = EntityModule.get();
        SpatialResource<Ref<EntityStore>, EntityStore> entities = store.getResource(entityModule.getEntitySpatialResourceType());
        entities.getSpatialStructure().collectBox(min, max, results);
        SpatialResource<Ref<EntityStore>, EntityStore> players = store.getResource(entityModule.getPlayerSpatialResourceType());
        players.getSpatialStructure().collectBox(min, max, results);
        SpatialResource<Ref<EntityStore>, EntityStore> items = store.getResource(entityModule.getItemSpatialResourceType());
        items.getSpatialStructure().collectBox(min, max, results);
        return results;
    }

    public static class ReferenceType<E extends Component<EntityStore>> {


        private final String name;
        private final Class<E> componentClass;
        private final ComponentType<EntityStore, ?> componentType;

        public ReferenceType(String name, Class<E> componentClass, ComponentType<EntityStore, ?> componentType) {
            this.name = name;
            this.componentClass = componentClass;
            this.componentType = componentType;
            EntityReferenceUtils.TYPES.add(this);
            EntityReferenceUtils.TYPES_MAP.put(componentClass, this);
        }

        public String getName() {
            return this.name;
        }

        public Class<E> getComponentClass() {
            return this.componentClass;
        }

        @SuppressWarnings("unchecked")
        public @Nullable E getComponent(Ref<EntityStore> ref) {
            Store<EntityStore> store = ref.getStore();
            return (E) store.getComponent(ref, this.componentType);
        }
    }

    /**
     * Get a component from an Object (Entity/Ref).
     *
     * @param object     Object to get component from
     * @param type       Component type to get
     * @param <ECS_TYPE> EntityStore Type
     * @param <T>        Type of returned component
     * @return Component from entity if available otherwise null
     */
    @SuppressWarnings("unchecked")
    public static <ECS_TYPE, T extends Component<ECS_TYPE>> @Nullable T getComponent(Object object, ComponentType<ECS_TYPE, T> type) {
        Ref<ECS_TYPE> reference = (Ref<ECS_TYPE>) getRef(object);
        if (reference == null) return null;

        Store<ECS_TYPE> store = reference.getStore();
        return store.getComponent(reference, type);
    }

    /**
     * Get a component from an Object (Entity/Ref) or create it if not present.
     *
     * @param object Object to get component from
     * @param type   Component type to get
     * @param <ECS>  EntityStore Type
     * @param <T>    Type of returned component
     * @return Component from entity if available otherwise will create/add a new one
     */
    @SuppressWarnings("unchecked")
    public static <ECS, T extends Component<ECS>> @NotNull T ensureAndGetComponent(Object object, ComponentType<ECS, T> type) {
        Ref<ECS> reference = (Ref<ECS>) getRef(object);
        if (reference == null) {
            throw new IllegalStateException("Object '" + object + "' does not have a reference");
        }

        Store<ECS> store = reference.getStore();
        return store.ensureAndGetComponent(reference, type);
    }

    /**
     * Add a component on an Object (Entity/Ref).
     *
     * @param object    Object (Entity/Ref) to add component to
     * @param type      Type of component to add
     * @param component Component to add
     * @param <ECS>     EntityStore Type
     * @param <T>       Type of component
     */
    @SuppressWarnings("unchecked")
    public static <ECS, T extends Component<ECS>> void addComponent(Object object, ComponentType<ECS, T> type, Component<ECS> component) {
        Ref<ECS> reference = (Ref<ECS>) getRef(object);
        if (reference == null) {
            throw new IllegalStateException("Object '" + object + "' does not have a reference");
        }
        reference.getStore().addComponent(reference, type, (T) component);
    }

    /**
     * Put a component on an Object (Entity/Ref).
     *
     * @param object    Object (Entity/Ref) to put component on
     * @param type      Type of component to put
     * @param component Component to put
     * @param <ECS>     EntityStore Type
     * @param <T>       Type of component
     */
    @SuppressWarnings("unchecked")
    public static <ECS, T extends Component<ECS>> void putComponent(Object object, ComponentType<ECS, T> type, Component<ECS> component) {
        Ref<ECS> reference = (Ref<ECS>) getRef(object);
        if (reference == null) {
            throw new IllegalStateException("Object '" + object + "' does not have a reference");
        }
        reference.getStore().putComponent(reference, type, (T) component);
    }

    /**
     * Try to remove a component from an Object (Entity/Ref).
     *
     * @param object Object (Entity/Ref) to remove component from
     * @param type   Type of component to remove
     * @param <ECS>  Store type
     * @param <T>    Component type
     */
    @SuppressWarnings("unchecked")
    public static <ECS, T extends Component<ECS>> void tryRemoveComponent(Object object, ComponentType<ECS, T> type) {
        Ref<ECS> reference = (Ref<ECS>) getRef(object);
        if (reference == null) {
            throw new IllegalStateException("Object '" + object + "' does not have a reference");
        }
        reference.getStore().tryRemoveComponent(reference, type);
    }

}
