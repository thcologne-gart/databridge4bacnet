package org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AssetInterfacesMappingConfigurationSubmodel extends Submodel implements ISubmodel{
    private static final Logger logger = LoggerFactory.getLogger(AssetInterfacesMappingConfigurationSubmodel.class);

    private final Map<String, AssetInterfaceMappingConfiguration> mappingConfiguration;

    public AssetInterfacesMappingConfigurationSubmodel(ISubmodel submodel) {
        if (submodel.getParent() == null) {
            logger.warn("Provided Submodel " + submodel.getIdentification().getId() + " has no parent set. Cannot be used to build routes if mappings don´t point to AAS.");
        }
        mappingConfiguration = new HashMap<>();
        Map<String, ISubmodelElement> configurations = ((ISubmodelElementCollection) submodel.getSubmodelElement("Configurations")).getSubmodelElements();
        for (String s : configurations.keySet()) {
            try {
                AssetInterfaceMappingConfiguration aimc = new AssetInterfaceMappingConfiguration((ISubmodelElementCollection) configurations.get(s).getLocalCopy(), submodel.getParent());
                mappingConfiguration.put(s, aimc);
            } catch (NullPointerException ignored) {
            }
        }
    }

    public ArrayList<String> getMappingNames() {
        return new ArrayList<>(mappingConfiguration.keySet());
    }

    public ArrayList<AssetInterfaceMappingConfiguration>  getMappingConfigurations() {
        return new ArrayList<>(mappingConfiguration.values());
    }
    public AssetInterfaceMappingConfiguration getMappingConfiguration(String id) {
        return mappingConfiguration.get(id);
    }

}
