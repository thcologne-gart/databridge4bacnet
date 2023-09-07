package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.IRouteElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.TimerRouteElement;

import java.io.IOException;
import java.util.*;

public class RoutesFileFactory extends JsonFileFactory {
    private static Logger logger = LoggerFactory.getLogger(RoutesFileFactory.class);

    private static final String DEFAULT_FILE_NAME = "routes";

    private Map<String, IRouteElement> routeMap = new HashMap<>();

    public RoutesFileFactory() {
        super();
        this.setFileName(DEFAULT_FILE_NAME);
    }
    public RoutesFileFactory(JsonArray content, String directory) {
        super(DEFAULT_FILE_NAME, content, directory);
    }

    public RoutesFileFactory(String directory) {
        super(DEFAULT_FILE_NAME, directory);
    }

    @Override
    public void setFileName(String fileName) {
        super.setFileName(fileName);
    }

    public void addRouteElement(IRouteElement routeElement) {
        this.content.add(routeElement.getAsJson());
        this.routeMap.put(((JsonObject) routeElement.getAsJson()).get("datasource").getAsString(), routeElement);
    }

    public IRouteElement getRouteElement(String uniqueId) {
        return this.routeMap.get(uniqueId);
    }

    public boolean hasSource(String uniqueId) {
        return this.routeMap.containsKey(uniqueId);
    }

    public void createRoutes(JsonArray datasources, JsonArray datasinks) {
        createRoutes(datasources, datasinks, "timer");
    }
    public void createRoutes(JsonArray datasources, JsonArray datasinks, String trigger) {
        if (trigger.equals("timer")) {
            createRoutes(datasources, datasinks, createDefaultTimer());
        }
    }

    private JsonObject createDefaultTimer() {
        JsonObject defaultTimer = new JsonObject();
        defaultTimer.addProperty("uniqueId", "timer1");
        defaultTimer.addProperty("fixedRate", true);
        defaultTimer.addProperty("delay", 0);
        defaultTimer.addProperty("period", 10000);
        return defaultTimer;
    }

    public void createRoutes(JsonArray datasources, JsonArray datasinks, JsonObject timer){
        if (datasources.size() != datasinks.size()) {
            throw new IllegalArgumentException("Size of arrays does not match. " + datasources.size() + " datasources vs. " + datasinks.size() + " datasinks");
        }
        for (int i = 0; i < datasources.size(); i++) {
            List<String> datasinkList = new ArrayList<>();
            datasinkList.add(((JsonObject) datasinks.get(i)).get("uniqueId").getAsString());

            TimerRouteElement timerRouteElement = new TimerRouteElement(
                    ((JsonObject) datasources.get(i)).get("uniqueId").getAsString(),
                    new ArrayList<>(),
                    datasinkList
            );
            timerRouteElement.setTimerName(timer.get("uniqueId").getAsString());
            this.content.add(timerRouteElement.getAsJson());
        }
    }


    public void clearRouteElements() {
        this.content = new JsonArray();
    }



    public void createFile() throws IOException {
        super.createFile();
    }

    public void createFile(boolean formatJson, boolean override) throws IOException {
        super.createFile(formatJson, override);
    }

    public static String getDefaultFileName() {return DEFAULT_FILE_NAME;}
}
