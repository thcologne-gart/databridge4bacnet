package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Timer implements IRouteElement{
    private String uniqueId;
    private boolean fixedRate;
    private int delay;
    private int period;
    public Timer(String uniqueId, boolean fixedRate, int delay, int period) {
        this.uniqueId = uniqueId;
        this.fixedRate = fixedRate;
        this.delay = delay;
        this.period = period;
    }

    public Timer(String uniqueId, int period) {
        this(uniqueId, true, 0, period);
    }

    @Override
    public JsonElement getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("uniqueId", this.uniqueId);
        json.addProperty("fixedRate", this.fixedRate);
        json.addProperty("delay", this.delay);
        json.addProperty("period", this.period);
        return json;
    }
    public String toString() {
        return getAsJson().toString();
    }
    @Override
    public void addDatasink(String uniqueId) {}

    public String getUniqueId() {return this.uniqueId;}
    public int getPeriod() {return this.period;}
}
