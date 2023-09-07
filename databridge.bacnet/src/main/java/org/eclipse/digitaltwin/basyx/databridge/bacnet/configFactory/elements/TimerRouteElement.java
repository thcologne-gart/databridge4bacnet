package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.digitaltwin.basyx.databridge.core.configuration.route.core.RouteConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.core.configuration.route.timer.TimerRouteConfiguration;

import java.util.List;

public class TimerRouteElement extends TimerRouteConfiguration implements IRouteElement {
    private final String DEFAULT_TIMER_NAME = "timer1";

    public TimerRouteElement(String datasource, List<String> transformers, List<String> datasinks) {
        super(datasource, transformers, datasinks);
        this.setTimerName(DEFAULT_TIMER_NAME);
    }
    public TimerRouteElement(String datasource, List<String> transformers, List<String> datasinks, String timerName) {
        super(datasource, transformers, datasinks);
        this.setTimerName(timerName);
    }
    public TimerRouteElement(RouteConfiguration configuration) {
        super(configuration);
    }

    public JsonObject getAsJson() {
        JsonObject elem = new JsonObject();

        elem.addProperty("datasource", this.getDatasource());
        elem.addProperty("trigger", this.getRouteTrigger());

        elem.add("triggerData", new JsonObject());
        ((JsonObject) elem.get("triggerData")).addProperty("timerName", this.getTimerName());

        elem.add("transformers", new JsonArray());
        this.getTransformers().forEach((f) -> ((JsonArray) elem.get("transformers")).add(f));
        elem.add("datasinks", new JsonArray());
        this.getDatasinks().forEach((f) -> ((JsonArray) elem.get("datasinks")).add(f));

        return elem;
    }

    public String toString() {
        return getAsJson().toString();
    }

    public void addDatasink(String datasink) {
        this.getDatasinks().add(datasink);
    }
}
