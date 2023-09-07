package org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class BacnetCommunicator {
    private static final Logger logger = LoggerFactory.getLogger(BacnetCommunicator.class);

    private static final int DEFAULT_LOCAL_DEVICE_NUMBER = 12345;

    private static LocalDevice localDevice;

    private static String ipAddress;
    private static String broadcastAddress;
    private static boolean started = false;

    public static void start() throws Exception {
        start(DEFAULT_LOCAL_DEVICE_NUMBER);
    }
    public static void stop() throws Exception {
        localDevice.terminate();
    }

    public static void start(int localDeviceNumber) throws Exception {
        ipAddress = getOwnIpAddress();
        setBroadcastAddress();
        if (localDevice != null) {
            localDevice.terminate();
        }
        localDevice = createLocalDevice(localDeviceNumber);
        localDevice.initialize();
        started = true;
    }

    public static boolean isStarted() { return started; }

    public static LocalDevice getLocalDevice() {return localDevice;}

    public static ReadPropertyAck readProperty(int remoteDeviceNumber, ObjectIdentifier oid, PropertyIdentifier pid) throws BACnetException {
        return localDevice.send(RemoteDeviceHandler.getRemoteDevice(remoteDeviceNumber), new ReadPropertyRequest(oid, pid)).get();
    }

    public static void writeProperty(int remoteDeviceNumber, ObjectIdentifier oid, PropertyIdentifier pid, UnsignedInteger propertyArrayIndex, Encodable propertyValue, UnsignedInteger priority) throws BACnetException {
        localDevice.send(
                RemoteDeviceHandler.getRemoteDevice(remoteDeviceNumber),
                new WritePropertyRequest(oid, pid, propertyArrayIndex, propertyValue,priority)).get();
    }

    public static LocalDevice createLocalDevice(int localDeviceNumber) {
        IpNetwork network = new IpNetworkBuilder().
                withBroadcast(BacnetCommunicator.getBroadcastAddress(), 24).
                build();
        Transport transport = new DefaultTransport(network);
        return new LocalDevice(localDeviceNumber, transport);
    }

    public static String getOwnIpAddress() {
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            return datagramSocket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getIpAddress() {return ipAddress;}

    public static void setBroadcastAddress() {
        String[] ipParts = getIpAddress().split("\\.");
        broadcastAddress = ipParts[0] + "." + ipParts[1] + "." + ipParts[2] + ".255";
    }

    public static String getBroadcastAddress() {
        return broadcastAddress;
    }
}
