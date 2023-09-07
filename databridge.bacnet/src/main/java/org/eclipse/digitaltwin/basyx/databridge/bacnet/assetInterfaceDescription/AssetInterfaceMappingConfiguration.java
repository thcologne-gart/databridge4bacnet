package org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription;

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
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.AASDatasinkFileFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.BacnetConsumerFileFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.RoutesFileFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.BacnetPollingConsumerElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.TimerRouteElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.registry.AIDRegistry;

import java.util.ArrayList;
import java.util.List;


public class AssetInterfaceMappingConfiguration extends SubmodelElementCollection implements ISubmodelElementCollection{
    private static final Logger logger = LoggerFactory.getLogger(AssetInterfaceMappingConfiguration.class);

    private final ISubmodelElementCollection smc;
    private final IReference connection;


    public AssetInterfaceMappingConfiguration(ISubmodelElementCollection submodelElementCollection, IReference parent) {
        this.smc = submodelElementCollection;
        this.connection = ((ReferenceElement) getConnectionDescription().getSubmodelElement("Connection")).getValue();

        String urlRegistryLocal = ConfigManager.getUrlRegistry();
        AssetInterfaceDescription aid = new AssetInterfaceDescription(((ConnectedSubmodelElementCollection) getReferencedElement(this.connection, new AIDRegistry(urlRegistryLocal))).getLocalCopy());

    }
    public AssetInterfaceMappingConfiguration(ISubmodelElementCollection submodelElementCollection) {
        this(submodelElementCollection, null);
    }

    public SubmodelElementCollection getConnectionDescription() {return (SubmodelElementCollection) this.smc.getSubmodelElement("ConnectionDescription");}

    public SubmodelElementCollection getMappings() {return (SubmodelElementCollection) this.smc.getSubmodelElement("Mappings");}
    public IReference getConnection() {return this.connection;}



    public ISubmodelElement getReferencedElement(IReference reference, AIDRegistry registry) {
        ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);

        ConnectedAssetAdministrationShell aas = null;
        ConnectedSubmodel submodel = null;
        ISubmodelElement currentElement = null;

        Identifier keyIdentifier;
        for (IKey key : reference.getKeys()) {
            if (key.getType().equals(KeyElements.ASSETADMINISTRATIONSHELL)) {
                keyIdentifier = new Identifier(IdentifierType.fromString(key.getIdType().toString()), key.getValue());
                try {
                    aas = manager.retrieveAAS(keyIdentifier);
                } catch (ResourceNotFoundException e) {
                    logger.warn("Referenced aas not found: " + keyIdentifier);
                }
            } else if (key.getType().equals(KeyElements.SUBMODEL)) {
                if (aas == null) {
                    logger.warn("Skipping reference due to missing AAS-key: " + reference);
                    break;
                }
                keyIdentifier = new Identifier(IdentifierType.fromString(key.getIdType().toString()), key.getValue());
                try {
                    submodel = (ConnectedSubmodel) aas.getSubmodel(keyIdentifier);
                } catch (ResourceNotFoundException e) {
                    logger.warn("Submodel not found in referenced aas: " + keyIdentifier);
                }
            } else if (key.getType().equals(KeyElements.SUBMODELELEMENTCOLLECTION)) {
                if (currentElement == null) {
                    currentElement = submodel.getSubmodelElement(key.getValue());
                } else {
                    currentElement = ((ConnectedSubmodelElementCollection) currentElement).getSubmodelElement(key.getValue());
                }
            }
        }
        return currentElement;
    }

    public void createRoutesFiles(AIDRegistry registry) {
        BacnetConsumerFileFactory bacnetConsumerFileFactory = new BacnetConsumerFileFactory(this, registry);

    }

}
