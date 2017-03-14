package com.zerses.camelsandbox;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class SplitterTest extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:in")
        .transform(constant("A,B,C"))
        .inOut("direct:inner")
        .log("CCC ${in.body}");

        from("direct:inner")
        .split()
        .tokenize(",")

      
        .log("AAA-1 ${in.body}")
        ;

    }

    public static class MyAggregationStrategy implements AggregationStrategy
    {

      @Override
      public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        System.out.println("Agg called with:"+newExchange.getIn().getBody());
        newExchange.setProperty(Exchange.AGGREGATION_COMPLETE_CURRENT_GROUP, true);
        return newExchange;
      }
      
    }
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new SplitterTest());
            ProducerTemplate template = context.createProducerTemplate();
            context.start();
            Thread.sleep(2000);

            template.sendBody("direct:in", "1,2,3"); 

        } finally {
            context.stop();
        }
    }
}
