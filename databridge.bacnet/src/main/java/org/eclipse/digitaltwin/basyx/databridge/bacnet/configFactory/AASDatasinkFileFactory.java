package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.ConfigManager;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceMappingConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.BacnetPollingConsumerElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.IRouteElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.annotations.FileFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceDescription;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.AASDatasinkElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.registry.AIDRegistry;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FileFactory(value="aas", type = "sink")
public class AASDatasinkFileFactory extends JsonFileFactory implements IFileFactory {
    private static Logger logger = LoggerFactory.getLogger(AASDatasinkFileFactory.class);
    private static final String DEFAULT_FILE_NAME = "aasserver";
    private String addressTargetSubmodel = null;
    private ArrayList<String> uniqueIds = new ArrayList<>();
    private static int indexUniqueId = 0;
    public AASDatasinkFileFactory() {
        super();
        this.setFileName(DEFAULT_FILE_NAME);
    }


    public AASDatasinkFileFactory(String directory) {
        super(DEFAULT_FILE_NAME, directory);
    }

    private void setAddressTargetSubmodelNormalized(String addressTargetSubmodel) {
        while (addressTargetSubmodel.endsWith("/")) {
            addressTargetSubmodel = addressTargetSubmodel.substring(0, addressTargetSubmodel.length()-1);
        }
        if (addressTargetSubmodel.endsWith("/submodelElements")) {
            addressTargetSubmodel = addressTargetSubmodel.replaceAll("/submodelElements", "");
        }
        if (!addressTargetSubmodel.endsWith("/submodel")) {
            if (addressTargetSubmodel.endsWith("/")) {
                addressTargetSubmodel += "submodel";
            } else {
                addressTargetSubmodel += "/submodel";
            }
        }
        this.addressTargetSubmodel = addressTargetSubmodel;
    }

    public AASDatasinkFileFactory(JsonArray content, String directory) {
        super(DEFAULT_FILE_NAME, content, directory);
    }


    private String identifyTargetSubmodelName() {
        String[] addressParts = addressTargetSubmodel.split("/");
        return addressParts[addressParts.length - 2];
    }

    public void addElement(AASDatasinkElement element) {
        if (!uniqueIds.contains(element.getUniqueId())) {
            this.content.add(element.getAsJson());
            uniqueIds.add(element.getUniqueId());
            indexUniqueId += 1;
        }
    }
    public AASDatasinkElement referenceToElement(IReference element, AIDRegistry registry) {

        String submodelEndpoint = "";
        StringBuilder idShortPathSb = new StringBuilder();
        StringBuilder uniqueId = new StringBuilder();

        String targetAasId = null;
        for (IKey key : element.getKeys()) {
            if (key.getType() == KeyElements.ASSETADMINISTRATIONSHELL) {
                targetAasId = key.getValue();
                uniqueId.append(targetAasId).append("/");
            } else if (key.getType() == KeyElements.SUBMODEL) {
                for (AASDescriptor aas : registry.lookupAll()) {
                    if (targetAasId != null && !aas.getIdentifier().getId().equals(targetAasId)) {
                        continue;
                    }
                    SubmodelDescriptor submodelDescriptor = aas.getSubmodelDescriptorFromIdentifierId(key.getValue());
                    if (submodelDescriptor != null) {
                        submodelEndpoint = submodelDescriptor.getFirstEndpoint();
                        uniqueId.append(submodelDescriptor.getIdShort());
                        break;
                    }
                }
            } else {
                idShortPathSb.append("/").append(key.getValue());
                uniqueId.append("/").append(key.getValue());
            }
        }
        String idShortPath = idShortPathSb.toString();
        if (idShortPath.startsWith("/")) {
            idShortPath = idShortPath.substring(1);
        }
        return new AASDatasinkElement(submodelEndpoint, idShortPath, uniqueId.toString());
    }


    @Override
    public void setFileName(String fileName) {
        super.setFileName(DEFAULT_FILE_NAME);
    }

    public void smcToElement() {

    }

    public void clearRouteElements() {
        this.content = new JsonArray();
    }

    public void createFile() throws IOException {
        super.createFile();
    }
    public static String getDefaultFileName() {return DEFAULT_FILE_NAME;}

    private String baseToUrl(String base) {
        base = base.replace("aas:", "");
        return normalizeUrl(base);
    }
    private String normalizeUrl(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() -1);
        }
        if (!url.endsWith("/api/v1/registry")) {
            url += "/api/v1/registry";
        }
        return url;
    }
    public void buildFilesFromMappingConfiguration(AssetInterfaceMappingConfiguration aimc, AIDRegistry registry) {
        buildFilesFromMappingConfiguration(aimc, registry, true);
    }
    public void buildFilesFromMappingConfiguration(AssetInterfaceMappingConfiguration aimc, AIDRegistry registry, Boolean keepExistingContent){

        AssetInterfaceDescription aid = new AssetInterfaceDescription(((ConnectedSubmodelElementCollection) getReferencedElement(aimc.getConnection(), registry)).getLocalCopy());
        String targetRegistryUrl = baseToUrl(aid.getBase());
        AIDRegistry targetRegistry = new AIDRegistry(targetRegistryUrl);

        RoutesFileFactory fileFactory = new RoutesFileFactory(this.getDirectory());
        TimerConsumerFileFactory timerConsumerFileFactory = new TimerConsumerFileFactory(this.getDirectory());

        if (keepExistingContent) {
            this.loadContentFromExistingFile();
            fileFactory.loadContentFromExistingFile();
            timerConsumerFileFactory.loadContentFromExistingFile();
        }

        SubmodelElementCollection mappings = aimc.getMappings();
        IReference source;
        IReference destination;
        String sourceUniqueId;
        String destinationUniqueId;

        AASDatasinkElement aasDatasourceElement;
        AASDatasinkElement aasDatasinkElement;

        for (ISubmodelElement rel : mappings.getValue()) {
            source = ((RelationshipElement) rel).getFirst();
            destination = ((RelationshipElement) rel).getSecond();
            aasDatasourceElement = this.referenceToElement(source, registry);
            aasDatasinkElement = this.referenceToElement(destination, targetRegistry);

            JsonObject triggerOptions = ConfigManager.getTriggerOptions();

            sourceUniqueId = "aas_element_" + indexUniqueId;
            aasDatasourceElement.setUniqueId(sourceUniqueId);
            this.addElement(aasDatasourceElement);

            destinationUniqueId = "aas_element_" + indexUniqueId;
            aasDatasinkElement.setUniqueId(destinationUniqueId);
            this.addElement(aasDatasinkElement);


            if (fileFactory.hasSource(sourceUniqueId)) {
                fileFactory.getRouteElement(sourceUniqueId).addDatasink(destinationUniqueId);
            } else {
                List<String> destinations = new ArrayList<>();
                destinations.add(destinationUniqueId);

                IRouteElement routeConfiguration = buildRouteElement(sourceUniqueId, new ArrayList<>(), destinations, (JsonObject) triggerOptions.get("lorem ipsum"), timerConsumerFileFactory);
                fileFactory.addRouteElement(routeConfiguration);
            }

        }
        try {
            fileFactory.createFile(true, true);
            this.createFile(true, true);
            timerConsumerFileFactory.createFile(true, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String uniqueIdBuilder(IReference relationshipElement) {
        StringBuilder sb = new StringBuilder();
        for (IKey key : relationshipElement.getKeys()) {
            sb.append(key.getValue()).append("/");
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        return "/";
    }

}
