package com.github.skriptdev.skript.api.skript.registration;

import io.github.syst3ms.skriptparser.registration.SkriptRegistration;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class EnumRegistry<E extends Enum<E>> {

    private final Map<String, E> values = new TreeMap<>();

    public static <T extends Enum<T>> SkriptRegistration.TypeRegistrar<T> register(SkriptRegistration registration,
                                                                            Class<T> enumClass,
                                                                            String name,
                                                                            String pattern) {
        if (enumClass == null || !enumClass.isEnum()) {
            throw new IllegalArgumentException("Cannot register null enum");
        }
        EnumRegistry<T> eEnumRegistry = new EnumRegistry<>();
        for (T e : enumClass.getEnumConstants()) {
            eEnumRegistry.values.put(e.name(), e);
        }
        return registration.newType(enumClass, name, pattern)
            .usage(String.join(", ", eEnumRegistry.values.keySet()))
            .literalParser(s -> eEnumRegistry.values.get(s.toLowerCase(Locale.ROOT).replace(" ", "_")))
            .toStringFunction(Enum::name);
    }

}
