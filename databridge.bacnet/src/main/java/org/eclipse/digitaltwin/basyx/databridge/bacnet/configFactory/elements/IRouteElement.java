package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements;

import com.google.gson.JsonElement;

public interface IRouteElement {
    JsonElement getAsJson();
    String toString();

    void addDatasink(String uniqueId);
}
