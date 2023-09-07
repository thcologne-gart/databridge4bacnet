package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
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
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.ConfigManager;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.annotations.FileFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceDescription;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceMappingConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.registry.AIDRegistry;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@FileFactory(value="bacnet")
public class BacnetConsumerFileFactory extends JsonFileFactory implements IFileFactory {
    private static Logger logger = LoggerFactory.getLogger(BacnetConsumerFileFactory.class);
    private static final String DEFAULT_FILE_NAME = "bacnetconsumer";

    private Map<String, String> mapSmcIdToUniqueId = new HashMap<>();

    public BacnetConsumerFileFactory() {
        super();
        this.setFileName(DEFAULT_FILE_NAME);
    }
    public BacnetConsumerFileFactory(JsonArray content, String directory) {
        super(DEFAULT_FILE_NAME, content, directory);
    }
    public BacnetConsumerFileFactory(String directory) {
        super(DEFAULT_FILE_NAME, directory);
    }

    public BacnetConsumerFileFactory(AssetInterfaceMappingConfiguration aimc, AIDRegistry registry) {
        this(DEFAULT_FILE_NAME);
        buildFilesFromMappingConfiguration(aimc, registry);
    }

    private JsonArray stringToArray(String s) {
        JsonArray array = new JsonArray();
        if (s.startsWith("(") || s.startsWith("[") || s.startsWith("{")) {
            s = s.substring(1);
        }
        if (s.endsWith(")") || s.endsWith("]") || s.endsWith("}")) {
            s = s.substring(0, s.length() - 1);
        }
        s = s.replaceAll(", ", ",").replaceAll("\"", "").replaceAll("'", "");
        for (String substring : s.split(",")) {

            try {
                array.add(PropertyIdentifier.forName(substring).intValue());
            } catch (BACnetRuntimeException e) {
                try {
                    array.add(Integer.parseInt(substring.replaceAll("@prop_", "")));
                } catch (NumberFormatException ee) {
                    StringBuilder sb = new StringBuilder();
                    for (char c : substring.toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            sb.append("-").append(Character.toLowerCase(c));
                        } else {
                            sb.append(c);
                        }
                    }
                    try {
                        array.add(PropertyIdentifier.forName(sb.toString()).intValue());
                    } catch (BACnetRuntimeException eee) {
                        logger.warn("Could not build PropertyIdentifier for '" + substring + "'");

                    }
                }
            }
        }
        return array;
    }

    private JsonArray assureDefaultPropsContained(int[] defaultProps, JsonArray props) {
        JsonArray propsNew = new JsonArray();
        for (int i : defaultProps) {
            propsNew.add(i);
            for (JsonElement e : props) {
                if (e.getAsInt() != i) {
                    propsNew.add(e.getAsInt());
                }
            }
        }
        return propsNew;
    }
    private JsonArray getBacnetPropertiesToRead(ISubmodelElementCollection smc) {
        JsonArray bacnetPropertiesToRead;
        if (smc.getSubmodelElements().containsKey("bacnet:PropertyList")) {
            bacnetPropertiesToRead = stringToArray(smc.getSubmodelElement("bacnet:PropertyList").getValue().toString());
        } else {
            bacnetPropertiesToRead = ConfigManager.getBacnetPropertiesToRead();
        }
        return bacnetPropertiesToRead;
    }

    public static int identifyDevice(AssetInterfaceDescription aid) {
        int deviceId = -1;
        Map<String, ISubmodelElement> properties = aid.getPropertiesSmc().getSubmodelElements();
        for (String objectId : properties.keySet()) {
            if (objectId.contains("device")) {
                deviceId= (new BigInteger(((SubmodelElementCollection) properties.get(objectId)).getSubmodelElement("bacnet:InstanceNumber").getValue().toString())).intValue();
                break;
            }
        }
        if (deviceId == -1) {
            logger.warn("No DeviceObject found in AID. Returning id -1");
        }
        return deviceId;
    }

    public static BacnetPollingConsumerElement smcToElement(ISubmodelElementCollection smc, int deviceId, String ipAddress, int propertyId) {
        String objectType = smc.getSubmodelElement("bacnet:ObjectType").getValue().toString();
        int instanceNumber = -1;
        instanceNumber = (new BigInteger(smc.getSubmodelElement("bacnet:InstanceNumber").getValue().toString())).intValue();

        int objectTypeNr;
        try {
            objectTypeNr = objectTypeIdForName(objectType);
        } catch (BACnetRuntimeException e) {
            try {
                objectTypeNr = Integer.parseInt(objectType);
            } catch (NumberFormatException exception) {
                objectTypeNr = 0;
            }
        }

        return new BacnetPollingConsumerElement(
                "bacnet/" + deviceId + "/" + smc.getIdShort() + "/" + getPropertyName(propertyId),
                ipAddress,
                deviceId,
                objectTypeNr,
                instanceNumber,
                propertyId
        );
    }

    private static String getPropertyName(int propertyId) {
        StringBuilder name = new StringBuilder();
        boolean makeUpper = false;
        String propName = PropertyIdentifier.nameForId(propertyId);
        if (propName == null) {
            return name.append("@prop_").append(propertyId).toString();
        }
        for (char c : propName.toCharArray()) {
            if (makeUpper) {
                name.append(Character.toUpperCase(c));
                makeUpper = false;
            } else {
                name.append(c);
            }
            if (c == '-') {
                makeUpper = true;
            }
        }
        return name.toString().replaceAll("-", "");
    }
    private static int objectTypeIdForName(String name) throws BACnetRuntimeException {
        try {
            return ObjectType.forName(name).bigIntegerValue().intValue();
        } catch (BACnetRuntimeException e) {
            StringBuilder sb = new StringBuilder();
            for (char c : name.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    sb.append("-" + Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }
            return ObjectType.forName(sb.toString()).bigIntegerValue().intValue();
        }
    }

    @Override
    public void setFileName(String fileName) {
        super.setFileName(DEFAULT_FILE_NAME);
    }

    public void addElement(BacnetPollingConsumerElement bacnetElement) {
        this.content.add(bacnetElement.getAsJson());
    }


    private IKey getReferenceEndpoint(IReference reference) {
        return reference.getKeys().get(reference.getKeys().size() - 1);
    }

    public void buildFilesFromMappingConfiguration(AssetInterfaceMappingConfiguration aimc, AIDRegistry registry) {
        buildFilesFromMappingConfiguration(aimc, registry, true);
    }
    public void buildFilesFromMappingConfiguration(AssetInterfaceMappingConfiguration aimc, AIDRegistry registry, Boolean keepExistingContent){

        AssetInterfaceDescription aid = new AssetInterfaceDescription(((ConnectedSubmodelElementCollection) getReferencedElement(aimc.getConnection(), registry)).getLocalCopy());
        String ipAddress = aid.getIpAddress();
        int deviceId = BacnetConsumerFileFactory.identifyDevice(aid);

        AASDatasinkFileFactory aasDatasinkFileFactory = new AASDatasinkFileFactory(this.getDirectory());
        RoutesFileFactory fileFactory = new RoutesFileFactory(this.getDirectory());
        TimerConsumerFileFactory timerConsumerFileFactory = new TimerConsumerFileFactory(this.getDirectory());

        if (keepExistingContent) {
            this.loadContentFromExistingFile();
            aasDatasinkFileFactory.loadContentFromExistingFile();
            fileFactory.loadContentFromExistingFile();
            timerConsumerFileFactory.loadContentFromExistingFile();
        }

        SubmodelElementCollection mappings = aimc.getMappings();
        IReference source;
        IReference destination;
        String sourceUniqueId;
        String destinationUniqueId;

        AASDatasinkElement aasDatasinkElement;

        ISubmodelElementCollection smc;
        JsonArray bacnetPropertiesToRead;

        for (ISubmodelElement rel : mappings.getValue()) {
            source = ((RelationshipElement) rel).getFirst();
            destination = ((RelationshipElement) rel).getSecond();
            aasDatasinkElement = aasDatasinkFileFactory.referenceToElement(destination, registry);

            smc = aid.getProperty(getReferenceEndpoint(source).getValue());
            bacnetPropertiesToRead = getBacnetPropertiesToRead(smc);

            JsonObject triggerOptions = ConfigManager.getTriggerOptions();

            for (JsonElement propertyId : bacnetPropertiesToRead) {
                BacnetPollingConsumerElement bacnetPollingConsumerElement = BacnetConsumerFileFactory.smcToElement(
                        smc,
                        deviceId,
                        ipAddress,
                        propertyId.getAsInt()
                );
                sourceUniqueId = bacnetPollingConsumerElement.getUniqueId();
                this.addElement(bacnetPollingConsumerElement);

                aasDatasinkElement.addIdShort(getPropertyName(propertyId.getAsInt()));
                destinationUniqueId = aasDatasinkElement.getUniqueId();
                aasDatasinkFileFactory.addElement(aasDatasinkElement);
                aasDatasinkElement.removeLastIdShort();


                if (fileFactory.hasSource(sourceUniqueId)) {
                    fileFactory.getRouteElement(sourceUniqueId).addDatasink(destinationUniqueId);
                } else {
                    List<String> destinations = new ArrayList<>();
                    destinations.add(destinationUniqueId);

                    IRouteElement routeConfiguration = buildRouteElement(sourceUniqueId, new ArrayList<>(), destinations, (JsonObject) triggerOptions.get(getPropertyName(propertyId.getAsInt())), timerConsumerFileFactory);
                    fileFactory.addRouteElement(routeConfiguration);
                }
            }
        }
        try {
            fileFactory.createFile(true, true);
            aasDatasinkFileFactory.createFile(true, true);
            timerConsumerFileFactory.createFile(true, true);
            this.createFile(true, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void clearRouteElements() {
        this.content = new JsonArray();
    }

    public void createFile() throws IOException {
        super.createFile();
    }

    public Map<String, String> getMapSmcIdToUniqueId() {return this.mapSmcIdToUniqueId;}

    public String getUniqueId(String idShortSmc) {return this.mapSmcIdToUniqueId.get(idShortSmc);}

    public static String getDefaultFileName() {return DEFAULT_FILE_NAME;}

}
