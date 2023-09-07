package org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class RemoteDeviceHandler {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDeviceHandler.class);


    private static final int DEFAULT_TIMEOUT = 10000;
    private static final Map<Integer, RemoteDevice> remoteDeviceMap = new HashMap<>();


    public static Map<Integer, RemoteDevice> getRemoteDevices() {
        return remoteDeviceMap;
    }
    public static RemoteDevice getRemoteDevice(int remoteDeviceNumber) {
        return remoteDeviceMap.get(remoteDeviceNumber);
    }


    public static void searchDevices(LocalDevice localDevice, JsonArray deviceNumbers) {
        searchDevices(localDevice, deviceNumbers, DEFAULT_TIMEOUT);
    }

    public static void searchDevices(LocalDevice localDevice, JsonArray deviceNumbers, int waitingTime) {
        remoteDeviceMap.clear();
        logger.info("Search for devices started");
        for (JsonElement deviceNumber : deviceNumbers) {
            remoteDeviceMap.put(deviceNumber.getAsInt(), null);
        }
        doSearch(localDevice, waitingTime);
    }


    public static void doSearch(LocalDevice localDevice, int waitingTime) {
        long starttime = System.currentTimeMillis();
        Listener listener = new Listener();
        try {
            if (!localDevice.isInitialized()) {
                localDevice.initialize();
            }

            localDevice.getEventHandler().addListener(listener);
            localDevice.sendGlobalBroadcast(new WhoIsRequest());

            while (remoteDeviceMap.containsValue(null)) {
                Thread.sleep(1000);
                if (System.currentTimeMillis() > starttime + waitingTime) {
                    logger.warn("Not all devices responded in time, stopping search after " + waitingTime/1000 + "s");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            localDevice.getEventHandler().removeListener(listener);
            logger.info("Search for devices ended.");
        }
    }

    static class Listener extends DeviceEventAdapter {
        @Override
        public void iAmReceived(final RemoteDevice d) {
            if (remoteDeviceMap.containsKey(d.getInstanceNumber())) {
                remoteDeviceMap.put(d.getInstanceNumber(), d);
                logger.info("Device found: " + d);
            }
        }
    }

}
