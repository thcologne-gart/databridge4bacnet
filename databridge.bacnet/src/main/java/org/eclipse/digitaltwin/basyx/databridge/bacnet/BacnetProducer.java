package org.eclipse.digitaltwin.basyx.databridge.bacnet;

import com.serotonin.bacnet4j.obj.ObjectProperties;
import com.serotonin.bacnet4j.obj.ObjectPropertyTypeDefinition;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet.BacnetCommunicator;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet.BacnetDatatypeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BacnetProducer extends DefaultProducer {
	private static final Logger LOG = LoggerFactory.getLogger(BacnetProducer.class);
    private BacnetEndpoint endpoint;
	private Class<? extends Encodable> valueType;


    public BacnetProducer(BacnetEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
		LOG.info("Creating Bacnet Producer for endpoint " + endpoint.getEndpointUri());
    }
	@Override
	public void process(Exchange exchange) throws Exception {
		ObjectPropertyTypeDefinition obj = ObjectProperties.getObjectPropertyTypeDefinition(
				ObjectType.forId(this.endpoint.getObjectType()),
				PropertyIdentifier.forId(this.endpoint.getPropertyId())
		);
		valueType = obj.getPropertyTypeDefinition().getClazz();

		Encodable value = BacnetDatatypeParser.parse(exchange.getMessage().getBody(), valueType);

		if (value == null) {
			throw new RuntimeException("Could not parse message to " + valueType + ". Message value type: " + exchange.getMessage().getBody().getClass().getName());
		}

		BacnetCommunicator.writeProperty(
				endpoint.getDeviceId(),
				new ObjectIdentifier(endpoint.getObjectType(), endpoint.getInstanceNr()),
				new PropertyIdentifier(endpoint.getPropertyId()),
				null,
				value,
				null
		);
	}


}
