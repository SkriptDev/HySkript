package com.github.skriptdev.skript.api.utils;

import com.github.skriptdev.skript.plugin.HySk;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

/**
 * Utilities for working with files.
 */
public class FileUtils {

    public static void copyFromJar(String sourceInsideJar, Path targetDir) throws IOException, URISyntaxException {
        // Get the URI of the resource inside the JAR
        URI uri = HySk.class.getResource(sourceInsideJar).toURI();

        // If the resource is inside a JAR, we need to create a FileSystem for it
        try (FileSystem jarFileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            Path pathInJar = jarFileSystem.getPath(sourceInsideJar);

            // Walk the tree and copy everything
            Files.walk(pathInJar).forEach(source -> {
                try {
                    // Create the corresponding path in the target directory
                    Path destination = targetDir.resolve(pathInJar.relativize(source).toString());

                    if (Files.isDirectory(source)) {
                        if (!Files.exists(destination)) Files.createDirectories(destination);
                    } else {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy: " + source, e);
                }
            });
        }
}
}
