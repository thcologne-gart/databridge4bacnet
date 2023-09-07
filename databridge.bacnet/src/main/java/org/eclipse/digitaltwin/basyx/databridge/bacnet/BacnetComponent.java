package org.eclipse.digitaltwin.basyx.databridge.bacnet;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

@Component("bacnet")
public class BacnetComponent extends DefaultComponent {
    
    @Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		Endpoint endpoint = new BacnetEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
    
    @Override
	public boolean useRawUri() {
		return true;
	}
}
