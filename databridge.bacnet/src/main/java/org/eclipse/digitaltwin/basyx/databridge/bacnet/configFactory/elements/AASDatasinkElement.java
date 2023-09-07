package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements;

import com.google.gson.JsonObject;
import org.eclipse.digitaltwin.basyx.databridge.aas.configuration.AASDatasinkConfiguration;

public class AASDatasinkElement extends AASDatasinkConfiguration implements IDatasinkElement {
    public AASDatasinkElement() {}

    public AASDatasinkElement(String submodelEndpoint, String idShortPath, String uniqueId) {
        super(submodelEndpoint, idShortPath, uniqueId, "BaSyx");
        if (!submodelEndpoint.endsWith("submodel")) {
            if (!submodelEndpoint.endsWith("/")) {
                submodelEndpoint += "/";
            }
            this.setSubmodelEndpoint(submodelEndpoint + "submodel");
        }
    }
    public AASDatasinkElement(String aasEndpoint, String propertyPath) {
        this(aasEndpoint, propertyPath, null);
    }

    public JsonObject getAsJson() {
        JsonObject elem = new JsonObject();

        elem.addProperty("uniqueId", this.getUniqueId());
        elem.addProperty("submodelEndpoint", this.getSubmodelEndpoint());
        elem.addProperty("idShortPath", this.getIdShortPath());

        return elem;
    }

    public String toString() {
        return getAsJson().toString();
    }

    public void addIdShort(String idShort) {
        setIdShortPath(getIdShortPath() + "/" + idShort);
        setUniqueId(getUniqueId() + "/" + idShort);
    }

    public void removeLastIdShort() {
        String currentIdShortPath = getIdShortPath();
        setIdShortPath(currentIdShortPath.substring(0, currentIdShortPath.lastIndexOf("/")));
        String currentUniqueId = getUniqueId();
        setUniqueId(currentUniqueId.substring(0, currentUniqueId.lastIndexOf("/")));
    }
}
