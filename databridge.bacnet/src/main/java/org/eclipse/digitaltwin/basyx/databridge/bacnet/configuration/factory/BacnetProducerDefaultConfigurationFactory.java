package org.eclipse.digitaltwin.basyx.databridge.bacnet.configuration.factory;

import org.eclipse.digitaltwin.basyx.databridge.aas.configuration.AASDatasinkConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configuration.BacnetDatasinkConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.core.configuration.factory.DataSinkConfigurationFactory;

public class BacnetProducerDefaultConfigurationFactory extends DataSinkConfigurationFactory {
	public static final String DEFAULT_FILE_PATH = "bacnetconsumer.json";
	
	public BacnetProducerDefaultConfigurationFactory(ClassLoader loader) {
		super(DEFAULT_FILE_PATH, loader, BacnetDatasinkConfiguration.class);
	}
	
	public BacnetProducerDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, BacnetDatasinkConfiguration.class);
	}
}
