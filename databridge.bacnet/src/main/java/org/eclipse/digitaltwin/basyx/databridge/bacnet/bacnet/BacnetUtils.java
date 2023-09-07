package org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.Address;

import java.util.concurrent.TimeoutException;

public class BacnetUtils {
    private static final int DEFAULT_TIMEOUT = 10000;
    private static int remoteDeviceNumber;
    private static RemoteDevice foundDevice;

    private BacnetUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static RemoteDevice findRemoteDevice(LocalDevice localDevice, int remoteDeviceNumber) {
        return findRemoteDevice(localDevice, remoteDeviceNumber, DEFAULT_TIMEOUT);
    }

    public static RemoteDevice findRemoteDevice(LocalDevice localDevice, int remoteDeviceNumber, int waitingTime) {
        BacnetUtils.remoteDeviceNumber = remoteDeviceNumber;
        BacnetUtils.foundDevice = null;
        long starttime = System.currentTimeMillis();
        try {
            localDevice.initialize();
            localDevice.getEventHandler().addListener(new Listener());
            localDevice.sendGlobalBroadcast(new WhoIsRequest());

            while (BacnetUtils.foundDevice == null) {
                Thread.sleep(1000);
                if (System.currentTimeMillis() > starttime + waitingTime) {
                    throw new TimeoutException("No response from device " + remoteDeviceNumber + " (waited " + waitingTime/1000 + "s)");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foundDevice;
    }

    static class Listener extends DeviceEventAdapter {
        @Override
        public void iAmReceived(final RemoteDevice d) {
            if (d.getInstanceNumber() == BacnetUtils.remoteDeviceNumber) {
                BacnetUtils.foundDevice = d;
            }
        }
    }

    public static RemoteDevice createRemoteDevice(final LocalDevice localDevice, final int instanceNumber) {
        return new RemoteDevice(localDevice, instanceNumber);
    }
    public static RemoteDevice createRemoteDevice(final LocalDevice localDevice, final int instanceNumber, final Address address) {
        return new RemoteDevice(localDevice, instanceNumber, address);
    }
    public static LocalDevice createLocalDevice(final int deviceNumber, IpNetwork network) {
        Transport transport = new DefaultTransport(network);
        return createLocalDevice(deviceNumber, transport);
    }
    public static LocalDevice createLocalDevice(final int deviceNumber, Transport transport) {
        return new LocalDevice(deviceNumber, transport);
    }

    public static IpNetwork createNetwork(String ipAddress, int networkPrefixLength) {
        return new IpNetworkBuilder().withBroadcast(ipAddress, networkPrefixLength).build();
    }
    public static IpNetwork createNetwork(String ipAddress) {
        return createNetwork(ipAddress, 24);
    }
}
