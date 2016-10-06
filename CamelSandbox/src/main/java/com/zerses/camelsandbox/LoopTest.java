package com.zerses.camelsandbox;

import java.util.Arrays;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class LoopTest extends RouteBuilder {



    @Override
    public void configure() throws Exception {

        from("direct:loopTest")
        .loop(simple("${body.size}"))
        .doTry()
        .process(new Processor1())
        .to("log:Point 4")
        .doCatch(Exception.class)
        .setProperty("CamelRouteStop", constant(Boolean.TRUE))
        .to("log:exception")
        .endDoTry();
        

    }

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new LoopTest());
            ProducerTemplate template = context.createProducerTemplate();
            context.start();           
            Thread.sleep(2000);
            template.sendBody("direct:loopTest", Arrays.asList(new String [] {"A", "B", "C"})); 

        } finally {
            context.stop();
        }
    }
    
    public class Processor1 implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
           Integer idx = exchange.getProperty("CamelLoopIndex", Integer.class);
           if (idx!=null && idx.intValue() > 0)
           {
             //  exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
            throw new Exception();
           }
        }

    }
}
