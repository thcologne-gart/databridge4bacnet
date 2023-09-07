package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements;

import com.google.gson.JsonObject;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configuration.BacnetPollingConsumerConfiguration;

public class BacnetPollingConsumerElement extends BacnetPollingConsumerConfiguration implements IConsumerElement {

    public BacnetPollingConsumerElement() {super();}

    public BacnetPollingConsumerElement(String uniqueId, String ipAddress, int deviceId, int objectType, int instanceNr, int propertyId) {
        super(
                uniqueId,
                ipAddress,
                deviceId,
                objectType,
                instanceNr,
                propertyId
            );
    }



    public JsonObject getAsJson() {
        JsonObject elem = new JsonObject();

        elem.addProperty("uniqueId", this.getUniqueId());
        elem.addProperty("ipAddress", this.getIpAddress());
        elem.addProperty("deviceId", this.getDeviceId());
        elem.addProperty("objectType", this.getObjectType());
        elem.addProperty("instanceNr", this.getInstanceNr());
        elem.addProperty("propertyId", this.getPropertyId());

        return elem;
    }

    public String toString() {
        return getAsJson().toString();
    }
}
