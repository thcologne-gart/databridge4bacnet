package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;

import com.google.gson.JsonArray;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceDescription;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceMappingConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.registry.AIDRegistry;

import java.io.IOException;

public interface IFileFactory {
    String DEFAULT_FILE_NAME = null;

    String getFileName();
    String getFileEnding();

    void createFile() throws IOException;

    void createFile(boolean formatJson, boolean override) throws IOException;

    static String getDefaultFileName() {
        return DEFAULT_FILE_NAME;
    }

    void loadContentFromExistingFile();

    void buildFilesFromMappingConfiguration(AssetInterfaceMappingConfiguration aimc, AIDRegistry registry);
    void buildFilesFromMappingConfiguration(AssetInterfaceMappingConfiguration aimc, AIDRegistry registry, Boolean keepExistingContent);

}
