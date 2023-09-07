package org.eclipse.digitaltwin.basyx.databridge.bacnet;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class BacnetProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {
        String myString = exchange.getIn().getBody(String.class);
        String[] myArray = myString.split(System.getProperty("line.separator"));
        StringBuffer sb = new StringBuffer();
        for (String s : myArray) {
            sb.append(s).append(",");
        }
        exchange.getIn().setBody(sb.toString());
    }
    public BacnetProcessor() {}

}
