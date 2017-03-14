package com.zerses.camelsandbox;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

public class MulticastAggregationTest extends RouteBuilder {



    @Override
    public void configure() throws Exception {


        from("direct:start")
        .log("after direct:start body=${body}")
        .multicast(new MyAggregationStrategy()).parallelProcessing()
          .to("direct:A")
          .to("direct:B")
        .end()
        .to("direct:C");
        
        from("direct:A").log("A: ${body}").transform(constant("Body changed by A "));
        from("direct:B").log("B: ${body}").transform(constant("Body changed by B "));
        from("direct:C").log("C: ${body}").transform(constant("C - Post-aggregation "));
    }

    public static class MyAggregationStrategy implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            try {
                String oldBody = (oldExchange==null)?null:oldExchange.getIn().getBody(String.class);
                String newBody = (newExchange==null)?null:newExchange.getIn().getBody(String.class);
                System.out.println("============================================= ");
                Logger logger = Logger.getLogger(MulticastAggregationTest.class);
                logger.info(oldBody+" <--old  / new -->"+newBody);
            } catch (Exception e) {
                System.out.println("============   EXCEPTION ================================= ");
                System.out.println(e);
            }
            return null;
        }

    }
    
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new MulticastAggregationTest());
            ProducerTemplate template = context.createProducerTemplate();
            context.start();

            template.sendBody("direct:start", "START");
            
            Thread.sleep(5000);

        } finally {
            context.stop();
        }
    }
}
