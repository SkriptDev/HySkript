package com.github.skriptdev.skript.api.skript.addon;

import com.github.skriptdev.skript.api.utils.Utils;
import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.util.RawJsonReader;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonLoader {

    private int addonCount = 0;

    public void loadAddonsFromFolder() {
        Utils.log("Loading addons...");
        Path resolve = HySk.getInstance().getDataDirectory().resolve("addons");
        File addonFolder = resolve.toFile();
        if (!addonFolder.exists()) {
            if (!addonFolder.mkdirs()) {
                Utils.error("Failed to create addons folder");
                return;
            }
        } else if (!addonFolder.isDirectory()) {
            Utils.error("Addons folder is not a directory");
            return;
        }
        File[] files = addonFolder.listFiles();
        if (files == null) {
            Utils.error("Failed to list files in addons folder");
            return;
        }

        for (File file : Arrays.stream(files)
            .filter(File::isFile)
            .filter(f -> f.getName().endsWith(".jar"))
            .toList()) {
            loadAddon(file);
        }
        String plural = this.addonCount == 1 ? "" : "s";
        Utils.log("Finished loading %s addon%s!", this.addonCount, plural);
    }

    private void loadAddon(File file) {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry jarEntry = jarFile.getJarEntry("manifest.json");
            if (jarEntry == null) {
                Utils.error("Manifest.json not found in addon " + file.getName());
                return;
            }

            InputStream inputStream = jarFile.getInputStream(jarEntry);
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            char[] buffer = RawJsonReader.READ_BUFFER.get();
            RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);

            Manifest manifest = Manifest.CODEC.decodeJson(rawJsonReader, new ExtraInfo());
            if (manifest == null) {
                Utils.error("Failed to decode manifest.json in addon " + file.getName());
                return;
            }

            URL[] urls = {file.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls, HySk.getInstance().getClassLoader());
            Class<?> externalClass;
            try {
                externalClass = classLoader.loadClass(manifest.getMainClass());
            } catch (ClassNotFoundException e) {
                Utils.error("Main class not found in addon " + file.getName());
                return;
            }
            Object mainClassIntance;
            try {
                mainClassIntance = externalClass.getDeclaredConstructor(String.class).newInstance(manifest.getName());
            } catch (ReflectiveOperationException e) {
                Utils.error("Failed to create instance of addon " + file.getName(), ErrorType.EXCEPTION);
                return;
            }
            if (mainClassIntance instanceof HySkriptAddon addon) {
                addon.setManifest(manifest);
                addon.start();
                this.addonCount++;
                // Finalize registration and logging
                for (LogEntry logEntry : addon.getSkriptRegistration().register()) {
                    Utils.log(null, logEntry);
                }
            }
        } catch (IOException e) {
            Utils.error("Failed to load addon " + file.getName(), ErrorType.EXCEPTION);
        }
    }

    public void shutdownAddons() {
        for (SkriptAddon addon : SkriptAddon.getAddons()) {
            if (addon instanceof HySkriptAddon hySkriptAddon) {
                hySkriptAddon.shutdown();
            }
        }
    }

}
