package org.eclipse.digitaltwin.basyx.databridge.bacnet;

import org.apache.camel.*;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(firstVersion = "1.0.0-SNAPSHOT", scheme = "bacnet", title = "BACnet", syntax = "bacnet:bacnetUri",
             category = {Category.JAVA})
public class BacnetEndpoint extends DefaultEndpoint {

	private static final Logger logger = LoggerFactory.getLogger(BacnetEndpoint.class);

	@UriPath
	@Metadata(required = true)
	private String name;
	@UriParam
	@Metadata(required = true)
	private String ipAddress;
	@UriParam
	@Metadata(required = true)
	private int deviceId;
	@UriParam
	@Metadata(required = true)
	private int objectType;
	@UriParam
	@Metadata(required = true)
	private int instanceNr;
	@UriParam
	@Metadata(required = true)
	private int propertyId;

	public BacnetEndpoint() {
    }

	public BacnetEndpoint(String uri, BacnetComponent component) {
        super(uri, component);
    }

    @Override
	public Producer createProducer() throws Exception {
		return new BacnetProducer(this);
    }

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return null;
	}

	@Override
	public PollingConsumer createPollingConsumer() throws Exception {
		return new BacnetPollingConsumer(this);
	}
	
	public String getFullProxyUrl() {
		String elemUrl = "";
		logger.info("Proxy URL: " + elemUrl);
		return elemUrl;
	}


	public String getIpAddress() {
		return this.ipAddress;}
	public void setIpAddress(String ipAddress) {this.ipAddress = ipAddress;}

	public String getBroadcastAddress() {
		String[] ipParts = getIpAddress().split("\\.");
		return ipParts[0] + "." + ipParts[1] + "." + ipParts[2] + ".255";

	}
	public int getDeviceId() {return this.deviceId;}
	public void setDeviceId(int deviceId) {this.deviceId = deviceId;}

	public int getPropertyId() {return this.propertyId;}
	public void setPropertyId(int propertyId) {this.propertyId = propertyId;}
	public int getObjectType() {return this.objectType;}
	public void setObjectType(int objectType) {this.objectType = objectType;}
	public int getInstanceNr() {return this.instanceNr;}
	public void setInstanceNr(int instanceNr) {this.instanceNr = instanceNr;}

}
