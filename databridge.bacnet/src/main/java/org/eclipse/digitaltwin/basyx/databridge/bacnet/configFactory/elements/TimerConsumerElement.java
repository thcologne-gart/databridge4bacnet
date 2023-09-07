package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements;

import com.google.gson.JsonObject;
import org.eclipse.digitaltwin.basyx.databridge.timer.configuration.TimerConsumerConfiguration;

public class TimerConsumerElement extends TimerConsumerConfiguration implements IConsumerElement {
    public TimerConsumerElement() {}

    public TimerConsumerElement(String uniqueId, String serverUrl,int serverPort, boolean fixedRate, int delay, int period) {
        super(uniqueId, serverUrl, serverPort, fixedRate, delay, period);
    }
    public TimerConsumerElement(String uniqueId, boolean fixedRate, int delay, int period) {
        super(uniqueId, "foo", 0, fixedRate, delay, period);
    }
    public JsonObject getAsJson() {
        JsonObject elem = new JsonObject();

        elem.addProperty("uniqueId", this.getUniqueId());
        elem.addProperty("fixedRate", this.getFixedRate());
        elem.addProperty("delay", this.getDelay());
        elem.addProperty("period", this.getPeriod());

        return elem;
    }

    public String toString() {
        return getAsJson().toString();
    }
}
