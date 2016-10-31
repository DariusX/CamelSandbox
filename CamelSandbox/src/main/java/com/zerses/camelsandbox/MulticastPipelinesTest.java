package com.zerses.camelsandbox;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.toolbox.AggregationStrategies;

public class MulticastPipelinesTest extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:start")
        .log("after direct:start body=${body}")
        .multicast().aggregationStrategy(AggregationStrategies.groupedExchange())
          .pipeline().to("direct:A").to("direct:B").end()
          .pipeline().to("direct:C").to("direct:D").end()
        .end();

        from("direct:A").log("A ${body}").transform(constant("Body changed by A "));
        from("direct:B").log("B ${body}");
        from("direct:C").log("C ${body}").transform(constant("Body changed by C "));
        from("direct:D").log("D ${body}");
    }

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new MulticastPipelinesTest());
            ProducerTemplate template = context.createProducerTemplate();
            context.start();

            template.sendBody("direct:start", "START");

        } finally {
            context.stop();
        }
    }
}
