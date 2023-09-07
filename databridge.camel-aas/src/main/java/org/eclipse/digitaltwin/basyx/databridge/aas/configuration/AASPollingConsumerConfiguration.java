package org.eclipse.digitaltwin.basyx.databridge.aas.configuration;

import org.eclipse.digitaltwin.basyx.databridge.aas.api.ApiType;
import org.eclipse.digitaltwin.basyx.databridge.core.configuration.entity.DataSourceConfiguration;
/**
 * Sets the name
 *
 * @author Thomas
 */

public class AASPollingConsumerConfiguration extends DataSourceConfiguration {
    private static final String PROPERTY_TYPE = "PROPERTY";

    private String type;
    private String submodelEndpoint;
    private String idShortPath;

    private String api;
    public AASPollingConsumerConfiguration() {}

    public AASPollingConsumerConfiguration(String submodelEndpoint, String idShortPath, String uniqueId, String api) {
        super();
        this.setUniqueId(uniqueId);
        this.type = PROPERTY_TYPE;
        this.submodelEndpoint = submodelEndpoint;
        this.idShortPath = idShortPath;
        this.api = api;
    }

    @Override
    public String getConnectionURI() {
        String endpointDefinition = "aas:";
        endpointDefinition += this.submodelEndpoint;
        endpointDefinition += "?propertyPath=" + this.idShortPath;
        endpointDefinition += "&api=" + getApiIfConfigured();
        return endpointDefinition;
    }
    private String getApiIfConfigured() {
        return api != null ? api : ApiType.BASYX.getName();
    }

}
