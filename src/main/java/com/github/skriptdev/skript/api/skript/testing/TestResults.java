package com.github.skriptdev.skript.api.skript.testing;

import com.github.skriptdev.skript.plugin.HySk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestResults {

    private boolean success = true;
    private final Map<String, List<String>> successMap = new HashMap<>();
    private final Map<String, List<String>> failureMap = new HashMap<>();

    public boolean isSuccess() {
        return success;
    }

    public Map<String, List<String>> getSuccessMap() {
        return successMap;
    }

    public Map<String, List<String>> getFailureMap() {
        return failureMap;
    }

    public void addSuccess(String test, String value) {
        this.successMap.computeIfAbsent(test, _ -> new ArrayList<>()).add(value);
    }

    public void addFailure(String test, String value) {
        this.success = false;
        this.failureMap.computeIfAbsent(test, _ -> new ArrayList<>()).add(value);
    }

    public void clear() {
        this.success = true;
        this.successMap.clear();
        this.failureMap.clear();
    }

    @SuppressWarnings({"CallToPrintStackTrace"})
    public void printToProperties() {
        Path resolve = HySk.getInstance().getDataDirectory().resolve("test-results.json");

        try {
            Files.createDirectories(resolve.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories for " + resolve.toAbsolutePath(), e);
        }

        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        try (BufferedWriter writer = Files.newBufferedWriter(resolve, StandardCharsets.UTF_8)) {
            gson.toJson(this, writer);
            System.out.println("Test-Results successfully written to " + resolve.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
