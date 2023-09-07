package org.eclipse.digitaltwin.basyx.databridge.bacnet.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class AIDRegistry extends AASRegistryProxy {
    private static final Logger logger = LoggerFactory.getLogger(AIDRegistry.class);

    private final String DEFAULT_AID_SEMANTIC_ID = "";
    private final KeyType DEFAULT_AID_ID_TYPE = KeyType.IRI;
    private final String DEFAULT_AIMC_SEMANTIC_ID = "";
    private final KeyType DEFAULT_AIMC_ID_TYPE = KeyType.IRI;


    private ArrayList<SubmodelDescriptor> aidSubmodels;
    private ArrayList<SubmodelDescriptor> aimcSubmodels;

    public AIDRegistry(String registryUrl) {
        super(registryUrl);
        this.aidSubmodels = findSubmodelsWithSemanticId(this.DEFAULT_AID_SEMANTIC_ID, this.DEFAULT_AID_ID_TYPE);
        this.aimcSubmodels = findSubmodelsWithSemanticId(this.DEFAULT_AIMC_SEMANTIC_ID, this.DEFAULT_AIMC_ID_TYPE);
    }

    public ArrayList<AASDescriptor> findAASContainingSubmodel(String semanticIdSubmodel, KeyType idType) {
        ArrayList<AASDescriptor> allAAS =  (ArrayList<AASDescriptor>) this.lookupAll();
        ArrayList<AASDescriptor> matchingAAS = new ArrayList<>();
        for (AASDescriptor aas : allAAS) {
            for (SubmodelDescriptor sm : aas.getSubmodelDescriptors()) {
                ArrayList<IKey> keys = (ArrayList<IKey>) sm.getSemanticId().getKeys();
                for (IKey key : keys) {
                    if (key.getValue().equals(semanticIdSubmodel) && key.getIdType().equals(idType)) {
                        matchingAAS.add(aas);
                    }
                }
            }
        }
        return matchingAAS;
    }

    public JsonObject getSubmodelsWithSemanticId(IReference reference) {
        ArrayList<AASDescriptor> allAAS =  (ArrayList<AASDescriptor>) this.lookupAll();
        JsonObject matchingSubmodels = new JsonObject();
        String aasId;
        for (AASDescriptor aas : allAAS) {
            aasId = aas.getIdentifier().getId();
            for (SubmodelDescriptor sm : aas.getSubmodelDescriptors()) {
                if (sm.getSemanticId().equals(reference)) {
                    if (!matchingSubmodels.has(aasId)) {
                        JsonObject json = new JsonObject();
                        json.addProperty("idType", aas.getIdentifier().getIdType().getStandardizedLiteral());
                        json.add("submodels", new JsonArray());
                        matchingSubmodels.add(aasId, json);
                    }
                    JsonObject smId = new JsonObject();
                    smId.addProperty("id", sm.getIdentifier().getId());
                    smId.addProperty("idType", sm.getIdentifier().getIdType().getStandardizedLiteral());
                    ((JsonArray) ((JsonObject) matchingSubmodels.get(aasId)).get("submodels")).add(smId);
                }
            }
        }
        return matchingSubmodels;
    }
    public JsonObject getSubmodelsWithSemanticIdAlt(IReference reference) {
        ArrayList<AASDescriptor> allAAS =  (ArrayList<AASDescriptor>) this.lookupAll();
        JsonObject matchingSubmodels = new JsonObject();
        String aasId;
        for (AASDescriptor aas : allAAS) {
            aasId = aas.getIdentifier().getId();
            for (SubmodelDescriptor sm : aas.getSubmodelDescriptors()) {
                if (sm.getSemanticId().equals(reference)) {
                    if (!matchingSubmodels.has(aasId)) {
                        JsonObject json = new JsonObject();
                        json.addProperty("idType", aas.getIdentifier().getIdType().getStandardizedLiteral());
                        json.add("submodels", new JsonArray());
                        matchingSubmodels.add(aasId, json);
                    }
                    JsonObject smId = new JsonObject();
                    smId.addProperty("id", sm.getIdentifier().getId());
                    smId.addProperty("idType", sm.getIdentifier().getIdType().getStandardizedLiteral());
                    ((JsonArray) ((JsonObject) matchingSubmodels.get(aasId)).get("submodels")).add(smId);
                }
            }
        }
        return matchingSubmodels;
    }
    public ArrayList<SubmodelDescriptor> findSubmodelsWithSemanticId(IReference reference) {
        ArrayList<AASDescriptor> allAAS =  (ArrayList<AASDescriptor>) this.lookupAll();
        ArrayList<SubmodelDescriptor> matchingSubmodels = new ArrayList<>();
        for (AASDescriptor aas : allAAS) {
            for (SubmodelDescriptor sm : aas.getSubmodelDescriptors()) {
                if (sm.getSemanticId().equals(reference)) {
                    matchingSubmodels.add(sm);
                }
            }
        }
        return matchingSubmodels;
    }
    public ArrayList<SubmodelDescriptor> findSubmodelsWithSemanticId(Key refKey) {
        ArrayList<AASDescriptor> allAAS =  (ArrayList<AASDescriptor>) this.lookupAll();
        ArrayList<SubmodelDescriptor> matchingSubmodels = new ArrayList<>();
        for (AASDescriptor aas : allAAS) {
            for (SubmodelDescriptor sm : aas.getSubmodelDescriptors()) {                 
                ArrayList<IKey> keys = (ArrayList<IKey>) sm.getSemanticId().getKeys();
                for (IKey key : keys) {
                    if (key.equals(refKey)) {
                        matchingSubmodels.add(sm);
                    }
                }
            }
        }
        return matchingSubmodels;
    }
    public ArrayList<SubmodelDescriptor> findSubmodelsWithSemanticId(String semanticId, KeyType idType) {
        ArrayList<AASDescriptor> allAAS =  (ArrayList<AASDescriptor>) this.lookupAll();
        ArrayList<SubmodelDescriptor> matchingSubmodels = new ArrayList<>();
        for (AASDescriptor aas : allAAS) {
            for (SubmodelDescriptor sm : aas.getSubmodelDescriptors()) {
                ArrayList<IKey> keys = (ArrayList<IKey>) sm.getSemanticId().getKeys();
                for (IKey key : keys) {
                    if (key.getValue().equals(semanticId) && key.getIdType().equals(idType)) {
                        matchingSubmodels.add(sm);
                    }
                }
            }
        }
        return matchingSubmodels;
    }

    public ArrayList<SubmodelDescriptor> getAidSubmodels() {return this.aidSubmodels;}
    public ArrayList<SubmodelDescriptor> getAimcSubmodels() {return this.aimcSubmodels;}


    public void setSubmodelParents() {
        ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(this);
        ArrayList<IAssetAdministrationShell> allAAS =  (ArrayList<IAssetAdministrationShell>) manager.retrieveAASAll();
        for (IAssetAdministrationShell aas : allAAS) {
            for (ISubmodel sm : aas.getSubmodels().values()) {
                if (sm.getParent() == null) {
                    aas.addSubmodel(((ConnectedSubmodel) sm).getLocalCopy());
                    logger.info("Added parent to Submodel: " + sm.getIdShort());
                }
            }
        }
    }

    public ArrayList<ISubmodel> findSubmodelsWithSemanticIdAlt(String semanticId, KeyType idType) {
        ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(this);

        ArrayList<IAssetAdministrationShell> allAAS =  (ArrayList<IAssetAdministrationShell>) manager.retrieveAASAll();
        ArrayList<ISubmodel> matchingSubmodels = new ArrayList<>();
        for (IAssetAdministrationShell aas : allAAS) {
            for (ISubmodel sm : aas.getSubmodels().values()) {
                ArrayList<IKey> keys = (ArrayList<IKey>) sm.getSemanticId().getKeys();
                if (keys.size() == 0) {
                    logger.info("Submodel without semanticId: " + sm.getIdentification().getId());
                }
                for (IKey key : keys) {
                    if (key.getValue().equals(semanticId) && key.getIdType().equals(idType)) {
                        matchingSubmodels.add(sm);
                    }
                }
            }
        }


        return matchingSubmodels;
    }

}