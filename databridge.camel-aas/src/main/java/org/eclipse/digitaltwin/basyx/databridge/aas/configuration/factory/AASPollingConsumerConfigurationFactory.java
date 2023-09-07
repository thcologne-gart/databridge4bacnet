package org.eclipse.digitaltwin.basyx.databridge.aas.configuration.factory;

import org.eclipse.digitaltwin.basyx.databridge.aas.configuration.AASPollingConsumerConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.core.configuration.factory.DataSourceConfigurationFactory;
/**
 * Sets the name
 *
 * @author Thomas
 */

public class AASPollingConsumerConfigurationFactory extends DataSourceConfigurationFactory {
    public static final String DEFAULT_FILE_PATH = "aasserver.json";

    public AASPollingConsumerConfigurationFactory(ClassLoader loader) {
        super(DEFAULT_FILE_PATH, loader, AASPollingConsumerConfiguration.class);
    }

    public AASPollingConsumerConfigurationFactory(String filePath, ClassLoader loader) {
        super(filePath, loader, AASPollingConsumerConfiguration.class);
    }
}
