package com.github.skriptdev.skript.api.skript.variables;

import com.github.skriptdev.skript.api.utils.Utils;
import com.github.skriptdev.skript.plugin.HySk;
import com.google.gson.JsonElement;
import com.hypixel.hytale.server.core.util.BsonUtil;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.variables.VariableStorage;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.json.JsonWriterSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

public class JsonVariableStorage extends VariableStorage {

    public enum Type {
        JSON,
        BSON;
    }

    private File file;
    private Type type = null;
    private final BsonDocument bsonFile = new BsonDocument();

    public JsonVariableStorage(SkriptLogger logger, String name) {
        super(logger, name);
    }

    @Override
    protected boolean load(@NotNull FileSection section) {
        String fileType = getConfigurationValue(section, "file-type");
        if (fileType == null) {
            Utils.error("No 'file-type' specified for database '%s'!", this.name);
            return false;
        }
        this.type = switch (fileType.toLowerCase(Locale.ROOT)) {
            case "json" -> Type.JSON;
            case "bson" -> Type.BSON;
            default -> {
                Utils.error("Unknown file-type '%s' in database '%s'", fileType, this.name);
                yield null;
            }
        };
        Utils.log("Database '%s' loaded with filetype '%s'", this.name, this.type);
        return this.type != null;
    }

    @Override
    protected void allLoaded() {
        // Load variables here?!?!
    }

    @Override
    protected boolean requiresFile() {
        return true;
    }

    @Override
    protected @Nullable File getFile(@NotNull String fileName) {
        Path resolve = HySk.getInstance().getDataDirectory().resolve(fileName);
        File varFile = resolve.toFile();
        if (!varFile.exists()) {
            try {
                if (varFile.createNewFile()) {
                    Utils.log("Created " + fileName + " file!");
                } else {
                    Utils.error("Failed to create " + fileName + " file!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.file = varFile;
        return varFile;
    }

    @Override
    protected boolean save(@NotNull String name, @Nullable String type, @Nullable JsonElement value) {
        BsonDocument myDocument = BsonDocument.parse("{}");


        if (type != null && value != null) {
            try {
                BsonValue bsonValue = BsonUtil.translateJsonToBson(value);
                myDocument.put("type", new BsonString(type));

                    if (bsonValue instanceof BsonDocument doc) {
                        myDocument.put("value", doc);
                    } else {
                        myDocument.put("value", bsonValue);
                    }
            } catch (Exception e) {
                Utils.error("Failed to parse value: " + value);
            }
        } else {
            this.bsonFile.remove(name);
        }

        this.bsonFile.put(name, myDocument);
        try {
            writeBsonDocumentToFile(this.type, this.bsonFile, this.file);
        } catch (IOException e) {
            Utils.error("Failed to save variable file");
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static void writeBsonDocumentToFile(Type type, BsonDocument document, File file) throws IOException {
        if (type == Type.JSON) {
            FileWriter fileWriter = new FileWriter(file);
            JsonWriterSettings.Builder indent = JsonWriterSettings.builder().indent(true);
            fileWriter.write(document.toJson(indent.build()));
            fileWriter.close();
        } else {
        }
    }

}
