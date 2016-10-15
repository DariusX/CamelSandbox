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
		
		final CsvDataFormat csv = new CsvDataFormat(";");
		csv.setLazyLoad(true);
		//csv.setSkipFirstLine(true);

	        from("file://" + inDir + "?noop=true")
		.unmarshal(csv)
		.split(body()).streaming().parallelProcessing()
		//.bean(validator, "validateNumber")
		//.filter(header(ValidateProcess.Valid).isEqualTo(true))
		.throttle(4)
		//.bean(validator, "validate")
		.marshal()
		.csv()
		.process(new DataTypeCheck())
		//.to(out)
		.log("done.")
		.end();
		
		
//	      from("file://" + inDir + "?noop=true")
//              .split(body().tokenize("\n")).streaming().parallelProcessing()
//              .process(new DataTypeCheck("After split: "))
//              .unmarshal().csv()
//              .process(new DataTypeCheck("After csv: "))
//              .marshal().csv()
//              .process(new DataTypeCheck("After marshal: "))
//              .end()
//              //.aggregate(new ConstantExpression("Z"))
//              .process(new DataTypeCheck("After aggregate: "));


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
