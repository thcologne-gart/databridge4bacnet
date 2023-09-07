package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.digitaltwin.basyx.databridge.core.configuration.route.core.RouteConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.core.configuration.route.event.EventRouteConfiguration;

import java.util.List;

public class EventRouteElement extends EventRouteConfiguration implements IRouteElement {
    public EventRouteElement(String datasource, List<String> transformers, List<String> datasinks) {
        super(datasource, transformers, datasinks);
    }

    public EventRouteElement(RouteConfiguration configuration) {
        super(configuration);
    }

    public JsonObject getAsJson() {
        JsonObject elem = new JsonObject();

        elem.addProperty("datasource", this.getDatasource());
        elem.addProperty("trigger", this.getRouteTrigger());

        elem.add("triggerData", new JsonObject());
        this.getTriggerData().forEach((k, v) -> ((JsonObject) elem.get("triggerData")).addProperty(k, v.toString()));
        elem.add("transformers", new JsonArray());
        this.getTransformers().forEach((f) -> ((JsonArray) elem.get("transformers")).add(f));
        elem.add("datasinks", new JsonArray());
        this.getDatasinks().forEach((f) -> ((JsonArray) elem.get("datasinks")).add(f));

        return elem;
    }

    public String toString() {
        return getAsJson().toString();
    }

    @Override
    public void addDatasink(String uniqueId) {

    }
}
