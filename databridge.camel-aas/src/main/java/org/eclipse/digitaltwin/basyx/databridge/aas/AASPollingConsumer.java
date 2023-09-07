package org.eclipse.digitaltwin.basyx.databridge.aas;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.support.DefaultMessage;
import org.apache.camel.support.PollingConsumerSupport;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Sets the name
 *
 * @author Thomas
 */

public class AASPollingConsumer extends PollingConsumerSupport {
    private static final Logger LOG = LoggerFactory.getLogger(AASPollingConsumer.class);
    private AASEndpoint endpoint;

    public AASPollingConsumer(AASEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        endpoint.connectToElement();
    }
    @Override
    public Exchange receive() {
        return doReceive(-1);
    }

    @Override
    public Exchange receiveNoWait() {
        return doReceive(-1);
    }

    @Override
    public Exchange receive(long timeout) {
        return doReceive((int) timeout);
    }

    protected Exchange doReceive(int timeout) {
        Exchange exchange = this.endpoint.createExchange();
        Message message = new DefaultMessage(exchange);

        Object value;
        try {
            value = this.endpoint.getPropertyValue();
        } catch (IOException e) {
            value = null;
        }
        message.setBody(value);

        ValueType valueType = this.endpoint.getPropertyValueType();
        message.setHeader("valueType", valueType.getStandardizedLiteral());
        exchange.setIn(message);
        return exchange;
    }
}
