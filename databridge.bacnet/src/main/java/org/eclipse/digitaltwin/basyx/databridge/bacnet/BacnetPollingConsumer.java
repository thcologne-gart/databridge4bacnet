package org.eclipse.digitaltwin.basyx.databridge.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.ErrorAPDUException;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.support.DefaultMessage;
import org.apache.camel.support.PollingConsumerSupport;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet.BacnetCommunicator;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet.RemoteDeviceHandler;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet4j.ObjectIdentifierProprietary;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet4j.ObjectTypeProprietary;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet4j.PropertyIdentifierProprietary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BacnetPollingConsumer extends PollingConsumerSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BacnetPollingConsumer.class);

    private final BacnetEndpoint endpoint;
    private RemoteDevice remoteDevice;
    private LocalDevice localDevice;

    public BacnetPollingConsumer(BacnetEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.remoteDevice = RemoteDeviceHandler.getRemoteDevice(endpoint.getDeviceId());
    }

    public BacnetEndpoint getEndpoint() {
        return (BacnetEndpoint)super.getEndpoint();
    }

    public Exchange receive() {
        return this.doReceive(-1);
    }

    public Exchange receive(long timeout) {
        return this.doReceive((int)timeout);
    }

    public Exchange receiveNoWait() {
        return this.doReceive(-1);
    }

    protected Exchange doReceive(int timeout) {
        Exchange exchange = this.endpoint.createExchange();
        Message message = new DefaultMessage(exchange);

        try {
            if (!BacnetCommunicator.isStarted()) {
                BacnetCommunicator.start();
            }

            int remoteDeviceNumber = endpoint.getDeviceId();

            ReadPropertyAck ack;
            if (ObjectTypeProprietary.isProprietary(endpoint.getObjectType()) || PropertyIdentifierProprietary.isProprietary(endpoint.getPropertyId())) {
                ObjectIdentifierProprietary oid = new ObjectIdentifierProprietary(endpoint.getObjectType(), endpoint.getInstanceNr());
                PropertyIdentifierProprietary pid = new PropertyIdentifierProprietary(endpoint.getPropertyId());
                try {
                    ack = BacnetCommunicator.readProperty(remoteDeviceNumber, oid, pid);
                    message.setBody(ack.getValue());
                } catch (ErrorAPDUException e) {
                    message.setBody("Unknown Property");
                }
            } else {
                ObjectIdentifier oid = new ObjectIdentifier(endpoint.getObjectType(), endpoint.getInstanceNr());
                PropertyIdentifier pid = PropertyIdentifier.forId(endpoint.getPropertyId());
                try {
                    ack = BacnetCommunicator.readProperty(remoteDeviceNumber, oid, pid);
                    message.setBody(ack.getValue());
                } catch (ErrorAPDUException e) {
                    message.setBody("Unknown Property");
                }
            }
            exchange.setIn(message);
        } catch (BACnetException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exchange;
    }


    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception {
    }
}
