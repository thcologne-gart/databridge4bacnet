package org.eclipse.digitaltwin.basyx.databridge.bacnet.configuration.factory;

import org.eclipse.digitaltwin.basyx.databridge.core.configuration.factory.DataSourceConfigurationFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configuration.BacnetPollingConsumerConfiguration;

public class BacnetPollingDefaultConfigurationFactory extends DataSourceConfigurationFactory {
	public static final String DEFAULT_FILE_PATH = "bacnetconsumer.json";
	
	public BacnetPollingDefaultConfigurationFactory(ClassLoader loader) {
		super(DEFAULT_FILE_PATH, loader, BacnetPollingConsumerConfiguration.class);
	}
	
	public BacnetPollingDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, BacnetPollingConsumerConfiguration.class);
	}
}
