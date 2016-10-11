package com.zerses.camelsandbox.rest;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.cxf.message.MessageContentsList;
import org.apache.log4j.Logger;
import org.hibernate.id.CompositeNestedGeneratedValueGenerator.GenerationContextLocator;


public class RestConsumerTest extends RouteBuilder {

	@Override
	public void configure() throws Exception {
	    
	    restConfiguration()
	    .component("jetty")
	    .host("0.0.0.0")
	    .port(9000)
	    ;
	    
            rest("/")
            .get("/A/{subpath1}/{subpath2}").to("direct:Aget");
            
            from("direct:Aget")
            .log(" hdrs: ${in.header.subpath1} / ${in.header.subpath2} /  ${in.header.parm1}")
            //.log(LoggingLevel.INFO, "Initial msg body: ${body}")
            .to("log:TEST1?showAll=true").process(new Processor() {

                @Override
                public void process(Exchange exchange) throws Exception {
                    Object bodyObj = exchange.getIn().getBody(String.class);
                    Logger logger = Logger.getLogger(this.getClass());
                    logger.info(" >>>> Body type is: "+bodyObj.getClass().getName());
                    logger.info(" Body is: "+bodyObj.toString());

                }
            });
	}
	
        public static void main(String[] args) throws Exception {
            CamelContext context = new DefaultCamelContext();
            try {
                context.addRoutes(new RestConsumerTest());
                ProducerTemplate template = context.createProducerTemplate();
                context.start();           
                Thread.sleep(20000);
                
                //To test this, open a browser and use;      http://localhost:9000/A/1/3?parm1=XXX
                
                //template.requestBody("cxfrs://http://localhost:9001?resourceClasses="+MyRsResource1.class.getName(), Arrays.asList(new String [] {"A", "B", "C"})); 

            } finally {
                context.stop();
            }
        }

}
