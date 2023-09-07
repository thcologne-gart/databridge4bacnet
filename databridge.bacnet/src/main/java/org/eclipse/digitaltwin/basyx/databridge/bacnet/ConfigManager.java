package org.eclipse.digitaltwin.basyx.databridge.bacnet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.JsonFileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

public abstract class ConfigManager {
    private static Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static JsonObject databridgeConfig;
    private static String directory;
    private static String separator = System.getProperty("file.separator");

    private static final int DEFAULT_TIMER_PERIOD = 60000;

    public static void init(String configPath) {
        if (!configPath.endsWith(separator)) {
            configPath += separator;
        }
        directory = configPath;
        loadConfig();
    }

    public static JsonArray getAasToMap() {
        if (databridgeConfig.has("aasToMap")) {
            return (JsonArray) databridgeConfig.get("aasToMap");
        }
        return new JsonArray();
    }
    public static JsonObject getSemanticIdAIMC() {
        return getSemanticId("semanticIdAimc");
    }
    public static JsonObject getSemanticIdAID() {
        return getSemanticId("semanticIdAid");
    }

    public static JsonArray getBacnetPropertiesToRead() {
        if (databridgeConfig.has("bacnetPropertiesToRead")) {
            return  (JsonArray) databridgeConfig.get("bacnetPropertiesToRead");
        } else {
            // if no bacnetPropertiesToRead are provided it defaults to the three mandatory Properties
            JsonArray bacnetPropertiesToRead = new JsonArray();
            bacnetPropertiesToRead.add(75); // objectIdentifier
            bacnetPropertiesToRead.add(77); // objectName
            bacnetPropertiesToRead.add(79); // objectType
            return bacnetPropertiesToRead;
        }
    }
    private static JsonObject getSemanticId(String key) {
        if (databridgeConfig.has(key)) {
            return (JsonObject) databridgeConfig.get(key);
        }
        throw new RuntimeException("No " + key + " provided in databridgeConfig file");
    }

    public static JsonObject getTriggerOptions() {
        if (databridgeConfig.has("triggerOptions")) {
            return (JsonObject) databridgeConfig.get("triggerOptions");
        }
        return new JsonObject();
    }
    private static void findDatabridgeConfig() {
        try {
            databridgeConfig = (JsonObject) JsonFileFactory.readJsonFile(getDirectory() + "databridgeConfig.json");
        } catch (FileNotFoundException e) {
            databridgeConfig = new JsonObject();
            logger.warn("File '" + getDirectory() + "databridgeConfig.json' does not excist.");
        }
    }

    public static String getUrlRegistry() {
        return databridgeConfig.get("urlRegistry").getAsString();
    }
    public static String getDirectory() {
        return directory;
    }

    public static void loadConfig() {
        loadConfig("databridgeConfig.json");
    }

    public static void loadConfig(String filename) {
        try {
            databridgeConfig = (JsonObject) JsonFileFactory.readJsonFile(directory + filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File '" + getDirectory() + filename + "' does not excist.");
        }
    }
    public static int getDefaultTimerDuration() {
        if (databridgeConfig.has("defaultTimerPeriod")) {
            return databridgeConfig.get("defaultTimerPeriod").getAsInt();
        }
        return DEFAULT_TIMER_PERIOD;
    }

    public static boolean ignoreExistingJsons() {
        if (databridgeConfig.has("ignoreExistingJsons")) {
            return databridgeConfig.get("ignoreExistingJsons").getAsBoolean();
        }
        return false;
    }
}
