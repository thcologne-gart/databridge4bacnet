package org.eclipse.digitaltwin.basyx.databridge.bacnet.configuration;

import org.eclipse.digitaltwin.basyx.databridge.core.configuration.entity.DataSourceConfiguration;

public class BacnetPollingConsumerConfiguration extends DataSourceConfiguration {
	private String ipAddress;
	private int deviceId;
	private int objectType;
	private int instanceNr;
	private int propertyId = -1;



	public BacnetPollingConsumerConfiguration() {  }

	public BacnetPollingConsumerConfiguration(String uniqueId, String ipAddress, int deviceId, int objectType, int instanceNr, int propertyId) {
		super();
		this.setUniqueId(uniqueId);
		this.ipAddress = ipAddress;
		this.deviceId = deviceId;
		this.objectType = objectType;
		this.instanceNr = instanceNr;
		this.propertyId = propertyId;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public int getObjectType() {
		return objectType;
	}

	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public int getInstanceNr() {
		return instanceNr;
	}
	public void setInstanceNr(int instanceNr) {
		this.instanceNr = instanceNr;
	}
	public int getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(int propertyId) {
		this.propertyId = propertyId;
	}

	@Override
	public String getConnectionURI() {
		return "bacnet://foo?ipAddress=" + getIpAddress()
				+ "&deviceId=" + getDeviceId()
				+ "&objectType=" + getObjectType()
				+ "&instanceNr=" + getInstanceNr()
				+ "&propertyId=" + getPropertyId();
	}
}
