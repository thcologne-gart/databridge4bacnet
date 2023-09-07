package org.eclipse.digitaltwin.basyx.databridge.bacnet;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;
import org.apache.camel.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BacnetConsumer extends DefaultConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BacnetConsumer.class);
    private final BacnetEndpoint endpoint;

    public BacnetConsumer(BacnetEndpoint endpoint, BacnetProcessor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {

    }

    @Override
    protected void doStop() throws Exception {

    }
}
