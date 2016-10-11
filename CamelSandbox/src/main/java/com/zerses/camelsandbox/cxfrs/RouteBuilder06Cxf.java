package com.zerses.camelsandbox.cxfrs;

import java.util.Arrays;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.cxf.message.MessageContentsList;


public class RouteBuilder06Cxf extends RouteBuilder {

	@Override
	public void configure() throws Exception {

	//from("cxfrs:bean:myRsResource1?bindingStyle=SimpleConsumer")
            from("cxfrs://http://localhost:9000?resourceClasses="+MyRsResource1.class.getName()+"&bindingStyle=SimpleConsumer")
            .log(LoggingLevel.INFO, "Initial msg body: ${body}")
            .to("log:TEST1?showAll=true").process(new Processor() {

                @Override
                public void process(Exchange exchange) throws Exception {
                    Object bodyObj = exchange.getIn().getBody();
                    if (bodyObj instanceof MessageContentsList) {
                        MessageContentsList body = (MessageContentsList)bodyObj;
                        System.out.println(" >>>> Body list size = " + body.size());
                        for (int i = 0; i < body.size(); i++) {
                            System.out.println(" >>>> Body element [" + i + "] " + body.get(i));
                        }
                    } else {
                        System.out.println(" >>>> Body is not expected type");
                    }

                }
            });
	}
	
        public static void main(String[] args) throws Exception {
            CamelContext context = new DefaultCamelContext();
            try {
                context.addRoutes(new RouteBuilder06Cxf());
                ProducerTemplate template = context.createProducerTemplate();
                context.start();           
                Thread.sleep(10000);
                
                //To test this, open a browser and use;      http://localhost:9000/A/1/3?parm1=XXX
                
                //template.requestBody("cxfrs://http://localhost:9001?resourceClasses="+MyRsResource1.class.getName(), Arrays.asList(new String [] {"A", "B", "C"})); 

            } finally {
                context.stop();
            }
        }

}
