package com.github.skriptdev.skript.api.skript.registration;

import com.hypixel.hytale.assetstore.JsonAsset;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class AssetStoreRegistry<AS> {

    private final Map<String, AS> assetStoreValues = new TreeMap<>();

    public static <K extends String, T extends JsonAsset<K>> SkriptRegistration.TypeRegistrar<T> register(
        SkriptRegistration registration,
        Class<T> c,
        DefaultAssetMap<K, T> assetMap,
        String name,
        String pattern) {

        AssetStoreRegistry<T> store = new AssetStoreRegistry<>();
        assetMap.getAssetMap().forEach((key, value) -> store.assetStoreValues.put(key.toLowerCase(Locale.ROOT), value));
        return registration.newType(c, name, pattern)
            .usage(String.join(", ", store.assetStoreValues.keySet()))
            .literalParser(s -> store.assetStoreValues.get(s.toLowerCase(Locale.ROOT).replace(" ", "_")))
            .toStringFunction(Object::toString);
    }

}
