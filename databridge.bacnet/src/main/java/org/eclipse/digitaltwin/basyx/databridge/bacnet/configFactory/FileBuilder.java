package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.ConfigManager;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.annotations.FileFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceMappingConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfacesMappingConfigurationSubmodel;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.registry.AIDRegistry;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.rmi.registry.Registry;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class FileBuilder {
    private static final Logger logger = LoggerFactory.getLogger(FileBuilder.class);
    private static final ArrayList<String> mandatoryFiles = new ArrayList<>();

    static {
        mandatoryFiles.add("routes.json");
        mandatoryFiles.add("aasserver.json");
        mandatoryFiles.add("timerconsumer.json");
    }
    private static HashMap<Class<?>, ArrayList<AssetInterfaceMappingConfiguration>> aimcSubmodelsMap = new HashMap<>();

    private static String getLastElementIdShort(IReference reference) {
        List<IKey> keys = reference.getKeys();
        IKey lastKey = keys.get(keys.size()-1);
        return lastKey.getValue();
    }

    private static void getMandatoryFiles(String configPath) {
        String urlRegistry = ConfigManager.getUrlRegistry();

        AIDRegistry registry = new AIDRegistry(urlRegistry);
        ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);
        JsonObject mappingConfigs = registry.getSubmodelsWithSemanticId(jsonToReference(ConfigManager.getSemanticIdAIMC()));
        mappingConfigs = filterSubmodels(mappingConfigs, ConfigManager.getAasToMap());

        for (String aasId : mappingConfigs.keySet()) {
            IdentifierType aasIdType = IdentifierType.fromString(((JsonObject) mappingConfigs.get(aasId)).get("idType").getAsString());

            for (JsonElement smIdentifier : ((JsonArray) ((JsonObject) mappingConfigs.get(aasId)).get("submodels"))) {
                IdentifierType smIdType = IdentifierType.fromString(((JsonObject) smIdentifier).get("idType").getAsString());
                ConnectedSubmodel connectedSubmodel = (ConnectedSubmodel) manager.retrieveSubmodel(new Identifier(aasIdType, aasId), new Identifier(smIdType, ((JsonObject) smIdentifier).get("id").getAsString()));
                AssetInterfacesMappingConfigurationSubmodel aimcSM = new AssetInterfacesMappingConfigurationSubmodel(connectedSubmodel.getLocalCopy());

                for (String idShort : aimcSM.getMappingNames()) {
                    AssetInterfaceMappingConfiguration aimc = aimcSM.getMappingConfiguration(idShort);
                    InterfaceDescription var1 = InterfaceDescription.forIdShort(getLastElementIdShort(aimc.getConnection()));
                    if (var1 == null) {
                        logger.info("No matching enum InterfaceDescription for idShort '" + getLastElementIdShort(aimc.getConnection()) + "'");
                        continue;
                    }
                    String protocolName = var1.getProtocolName();

                    Reflections reflections = new Reflections(FileBuilder.class.getPackage().getName());
                    Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(FileFactory.class);

                    IFileFactory factory;
                    for (Class<?> factoryClass : annotated) {
                        FileFactory fileFactory = factoryClass.getAnnotation(FileFactory.class);
                        if (fileFactory.value().equals(protocolName)) {
                            if (!aimcSubmodelsMap.containsKey(factoryClass)) {
                                 aimcSubmodelsMap.put(factoryClass, new ArrayList<>());
                            }
                            aimcSubmodelsMap.get(factoryClass).add(aimc);

                            try {
                                factory = (IFileFactory) factoryClass.getDeclaredConstructor(String.class).newInstance(configPath);
                                if (!mandatoryFiles.contains(factory.getFileName())) {
                                    mandatoryFiles.add(factory.getFileName());
                                }

                            } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                                     NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void createRoutes(String configPath) {
        long timeStart = System.currentTimeMillis();

        String urlRegistry = ConfigManager.getUrlRegistry();
        AIDRegistry registry = new AIDRegistry(urlRegistry);

        IFileFactory factory;
        for (Class<?> clazz : aimcSubmodelsMap.keySet()) {
            for (AssetInterfaceMappingConfiguration aimc : aimcSubmodelsMap.get(clazz)) {
                try {
                    factory = (IFileFactory) clazz.getDeclaredConstructor(String.class).newInstance(configPath);
                    factory.buildFilesFromMappingConfiguration(aimc, registry);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        long timeEnd = System.currentTimeMillis();
        logger.info("Creating files took " + (timeEnd-timeStart) + "ms");
    }

    private static void createRoutesAlt(String configPath) {
        long timeStart = System.currentTimeMillis();

        String urlRegistry = ConfigManager.getUrlRegistry();

        AIDRegistry registry = new AIDRegistry(urlRegistry);
        ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);
        JsonObject mappingConfigs = registry.getSubmodelsWithSemanticId(jsonToReference(ConfigManager.getSemanticIdAIMC()));
        mappingConfigs = filterSubmodels(mappingConfigs, ConfigManager.getAasToMap());

        for (String aasId : mappingConfigs.keySet()) {
            IdentifierType aasIdType = IdentifierType.fromString(((JsonObject) mappingConfigs.get(aasId)).get("idType").getAsString());

            for (JsonElement smIdentifier : ((JsonArray) ((JsonObject) mappingConfigs.get(aasId)).get("submodels"))) {
                IdentifierType smIdType = IdentifierType.fromString(((JsonObject) smIdentifier).get("idType").getAsString());
                ConnectedSubmodel aimcSm = (ConnectedSubmodel) manager.retrieveSubmodel(new Identifier(aasIdType, aasId), new Identifier(smIdType, ((JsonObject) smIdentifier).get("id").getAsString()));
                AssetInterfacesMappingConfigurationSubmodel aimcSM = new AssetInterfacesMappingConfigurationSubmodel(aimcSm.getLocalCopy());

                for (String idShort : aimcSM.getMappingNames()) {
                    AssetInterfaceMappingConfiguration aimcNeu = aimcSM.getMappingConfiguration(idShort);
                    MappingConfiguration var1 = MappingConfiguration.forIdShort(idShort);
                    if (var1 == null) {
                        logger.info("No matching enum MappingConfiguration for idShort '" + idShort + "'");
                        continue;
                    }
                    String protocolName = var1.getProtocolName();

                    Reflections reflections = new Reflections(FileBuilder.class.getPackage().getName());
                    Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(FileFactory.class);

                    IFileFactory factory;
                    for (Class<?> factoryClass : annotated) {
                        FileFactory fileFactory = factoryClass.getAnnotation(FileFactory.class);
                        if (fileFactory.value().equals(protocolName)) {
                            try {
                                factory = (IFileFactory) factoryClass.getDeclaredConstructor(String.class).newInstance(configPath);
                                factory.buildFilesFromMappingConfiguration(aimcNeu, registry);
                            } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                                     NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                    }
                }
            }

        }

        long timeEnd = System.currentTimeMillis();
        logger.info("Creating files took " + (timeEnd-timeStart) + "ms");
    }

    private static JsonObject filterSubmodels(JsonObject mappingConfigs, JsonArray aasToMap) {
        if (aasToMap == null || aasToMap.size() == 0) {
            return mappingConfigs;
        }
        JsonObject filtered = new JsonObject();
        for (JsonElement aas : aasToMap) {
            if (mappingConfigs.has(aas.getAsString())) {
                filtered.add(aas.getAsString(), mappingConfigs.get(aas.getAsString()));
            }
        }
        return filtered;
    }
    private static Reference jsonToReference(JsonObject json) {
        Key key = new Key(
                KeyElements.fromString(json.get("type").getAsString()),
                json.get("local").getAsBoolean(),
                json.get("value").getAsString(),
                KeyType.fromString(json.get("idType").getAsString())
        );
        return new Reference(key);
    }


    private static void moveFilesToArchive(String pathDirectory)  {
        pathDirectory = preparePathEnding(pathDirectory);
        assureArchiveExists(pathDirectory);

        for (String file : mandatoryFiles) {
            File sourceFile = new File(pathDirectory + file);
            if (!sourceFile.isFile()) {
                continue;
            }
            Path source = sourceFile.toPath();

            String pathTarget = assureFileNotInArchive(pathDirectory + "archive" + File.separator + file);

            File targetFile = new File(pathTarget);
            Path target = targetFile.toPath();

            try {
                Files.move(source, target);
                logger.info("Moved file to archive: '" + file + "' as '" + targetFile.getName() + "'");
            } catch (IOException e) {
                logger.info("Failed to move file to archive: '" + file);
            }
            try {
                Instant instant = Instant.now();
                Files.setLastModifiedTime(target, FileTime.from(instant));
            } catch (IOException e) {
                logger.info("Failed to set current time on archived file '" + targetFile.getName() + "'");
            }
        }
    }

    private static String assureFileNotInArchive(String path) {
        int i = 0;
        while (true) {
            File f = new File(path);
            if (f.isFile()) {
                i++;
                int var1;
                int var3;
                if (i == 1) {
                    var1 = path.lastIndexOf(".");
                    path = path.substring(0, var1) + "_(" + i + ")" + path.substring(var1);
                } else {
                    var1 = path.lastIndexOf("_(");
                    var3 = path.lastIndexOf(")");
                    path = path.substring(0, var1) + "_(" + i + path.substring(var3);
                }
            } else {
                return path;
            }
        }
    }

    private static void assureArchiveExists(String pathDirectory) {
        pathDirectory = preparePathEnding(pathDirectory);
        File f = new File(pathDirectory + "archive");
        if (!f.isDirectory()) {
            new File(pathDirectory + "archive").mkdirs();
            logger.info("Created archive-directory in: " + pathDirectory);
        }
    }
    private static boolean allConfigsExisting(String configPath) {
        for (String file : mandatoryFiles) {
            if (!checkIfFileExists(configPath,file)) {
                return false;
            }
        }
        return true;
    }

    private static String preparePathEnding(String path) {
        String separator = System.getProperty("file.separator");
        if (path.endsWith(separator)) {
            return path;
        }
        return path + separator;
    }

    public static void assureFilesExist(String path) {
        getMandatoryFiles(path);
        if (!allConfigsExisting(path) || ConfigManager.ignoreExistingJsons()) {
            moveFilesToArchive(path);
            createRoutes(path);
        }
    }

    private static boolean checkIfFileExists(String pathDirectory, String filename)  {
        pathDirectory = preparePathEnding(pathDirectory);
        File f = new File(pathDirectory + filename);
        boolean exists = f.isFile();
        if (exists) {
            logger.info("Found file: " + pathDirectory + filename);
        } else {
            logger.info("File not found: " + pathDirectory + filename);
        }
        return exists;
    }
}
