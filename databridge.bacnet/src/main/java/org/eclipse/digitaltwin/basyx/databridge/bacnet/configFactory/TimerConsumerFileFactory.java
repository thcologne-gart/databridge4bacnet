package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.ConfigManager;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceDescription;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.AASDatasinkElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class TimerConsumerFileFactory extends JsonFileFactory {
    private static Logger logger = LoggerFactory.getLogger(TimerConsumerFileFactory.class);
    private static final String DEFAULT_FILE_NAME = "timerconsumer";
    private static final String DEFAULT_TIMER_NAME = "timer1";


    private static HashMap<String, Timer> existingTimer = new HashMap<>();
    private static HashMap<Integer, String> existingTimerDurations = new HashMap<>();

    public TimerConsumerFileFactory() {
        super();
        this.setFileName(DEFAULT_FILE_NAME);
        this.addTimer(DEFAULT_TIMER_NAME, ConfigManager.getDefaultTimerDuration());
    }

    public TimerConsumerFileFactory(JsonArray content, String directory) {
        super(DEFAULT_FILE_NAME, content, directory);
        this.addTimer(DEFAULT_TIMER_NAME, ConfigManager.getDefaultTimerDuration());
    }
    public TimerConsumerFileFactory(String directory) {
        super(DEFAULT_FILE_NAME, directory);
        this.addTimer(DEFAULT_TIMER_NAME, ConfigManager.getDefaultTimerDuration());
    }
    public void loadContentFromExistingFile() {
        try {
            this.content = (JsonArray) readJsonFile(this.getDirectory() + this.getFileName());
        } catch (FileNotFoundException e) {
            this.content = new JsonArray();
            this.addTimer(DEFAULT_TIMER_NAME, ConfigManager.getDefaultTimerDuration());
            logger.info("File '" + this.getDirectory() + this.getFileName() + "' does not excist.");
        }
    }
    public void addTimer(Timer timer) {
        this.content.add(timer.getAsJson());
        TimerConsumerFileFactory.existingTimer.put(timer.getUniqueId(), timer);
        TimerConsumerFileFactory.existingTimerDurations.put(timer.getPeriod(), timer.getUniqueId());
    }

    public String addTimer(int period) {
        if (existingTimerDurations.containsKey(period)) {
            return existingTimerDurations.get(period);
        }
        int counter = 1;
        String timerName;
        while (true) {
            timerName = "timer" + counter;
            if (!existingTimer.containsKey(timerName)) {
                addTimer(timerName, period);
                return timerName;
            }
            counter++;
        }
    }
    public void addTimer(String uniqueId, int period) {
        addTimer(new Timer(uniqueId, period));
    }
    public void addTimer(String uniqueId, boolean fixedRate, int delay, int period) {
        addTimer(new Timer(uniqueId, fixedRate, delay, period));
    }
    public JsonArray getContent() {return this.content;}
    public boolean timerExists(String uniqueId) {
        return TimerConsumerFileFactory.existingTimer.containsKey(uniqueId);
    }

}
