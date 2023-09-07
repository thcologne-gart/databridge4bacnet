package org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;

import java.util.*;

public class AssetInterfacesDescriptionSubmodel extends Submodel implements ISubmodel{
    private final Map<String, AssetInterfaceDescription> interfaces;

    public AssetInterfacesDescriptionSubmodel(ISubmodel submodel) {
        interfaces = new HashMap<>();
        for (String s : submodel.getSubmodelElements().keySet()) {
            interfaces.put(s, new AssetInterfaceDescription((SubmodelElementCollection) submodel.getSubmodelElement(s)));
        }
    }

    public Map<String, AssetInterfaceDescription>  getInterfaces() {
        return interfaces;
    }
    public ISubmodelElementCollection getInterface(String id) {
        return interfaces.get(id);
    }

}
