package com.zerses.camelsandbox.csv;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.CsvDataFormat;

import com.zerses.camelsandbox.common.DataTypeCheck;

public class CsvComponentTest extends RouteBuilder {

	@Override
	public void configure() throws Exception {

        String inDir = "c:/test/in1a";

        final CsvDataFormat csv = new CsvDataFormat(",");
        csv.setLazyLoad(true);
        // csv.setSkipFirstLine(true);

        from("file://" + inDir + "?noop=true")
            .unmarshal(csv)
            .split(body()).streaming().parallelProcessing()
            .throttle(4)
            .log("done ${in.body}.")
            .process(new DataTypeCheck())

            // The following will not work as-is, because csv cannot marshal
            // from a List to a comma-delimited String.
            // It marshals from a Map or from a List of Maps
            // .marshal()
            // .csv()

            .end();

	}
	
	    public static void main(String[] args) throws Exception {
	        CamelContext context = new DefaultCamelContext();
	        try {
	            context.addRoutes(new CsvComponentTest());
	    //        ProducerTemplate template = context.createProducerTemplate();
	            context.start();
	            Thread.sleep(2000);

	            // template.sendBody("direct:test01", "My Input Message"); //InOnly
	   //         Object reply = template.requestBody("direct:test01", "My Input Message"); // InOut
	   //         System.out.println("Caller received reply: " + reply);

	        } finally {
	            context.stop();
	        }
	    }
	    

}
