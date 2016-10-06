package com.zerses.camelsandbox;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class InOutTest extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:in")
        .to("log:fromMainRouteOriginal?showExchangeId=true")
        .transform(constant("Parm A --> to() ... i.e. default "))
        .to("log:fromMainRouteA_In?showExchangeId=true")
        .to("seda:subRoute1")
        .to("log:fromMainRouteA_Out?showExchangeId=true")
        
        .transform(constant("Parm B --> inOnly() "))
        .to("log:fromMainRouteB_In?showExchangeId=true")
        .inOnly("seda:subRoute1")
        .to("log:fromMainRouteB_Out?showExchangeId=true")
        
        .transform(constant("Parm C --> inOut() "))
        .to("log:fromMainRouteC_In?showExchangeId=true")
        .inOut("seda:subRoute1")
        .to("log:fromMainRouteC_Out?showExchangeId=true")

        .transform(constant("Parm D --> to() and waitForTaskToComplete=Always"))
        .to("log:fromMainRouteD_In?showExchangeId=true")
        .to("seda:subRoute1?waitForTaskToComplete=Always")
        .to("log:fromMainRouteD_Out?showExchangeId=true")
        
        .transform(constant("Parm E --> to() and waitForTaskToComplete=Never"))
        .to("log:fromMainRouteE_In?showExchangeId=true")
        .to("seda:subRoute1?waitForTaskToComplete=Never")
        .to("log:fromMainRouteE_Out?showExchangeId=true");
        
        from("seda:subRoute1")
        .setBody(constant("Changed to BBB"));

    }

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new InOutTest());
            ProducerTemplate template = context.createProducerTemplate();
            context.start();
            Thread.sleep(2000);

            Object reply = template.requestBody("direct:in", "template.requestBody() ... implies inOut"); 
            System.out.println("Response received on completion: " + reply);
            
            Thread.sleep(2000);
            template.sendBody("direct:in", "template.sendBody() ... implies inOnly"); 

        } finally {
            context.stop();
        }
    }
}
