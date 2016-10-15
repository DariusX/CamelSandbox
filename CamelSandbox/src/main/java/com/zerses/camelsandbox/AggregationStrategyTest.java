package com.zerses.camelsandbox;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.zerses.camelsandbox.common.DataTypeCheck;

public class AggregationStrategyTest extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("file://c:/test/in1a?noop=true")
        .process(new DataTypeCheck())
        .split()
        .tokenize("\n")
        .streaming()
        .aggregate()
        .constant(true)
        .eagerCheckCompletion()
        .aggregationStrategy(new MyAggregationStrategy())
        // Alternate method: Split the way we need it...no
        // aggregation... .split(method(new MyFileSplitter(),
        // "iterator"))
        .log(LoggingLevel.INFO, "Reading file: ${body}");

    }

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new AggregationStrategyTest());
            context.start();
            Thread.sleep(7000);
        } finally {
            context.stop();
        }
    }

    public static class MyAggregationStrategy implements AggregationStrategy, Predicate {

        String lastKey = null;
        String cachedBody = null;
        int recordCount = 0;

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            String oldBody = oldExchange == null ? "" : oldExchange.getIn().getBody(String.class);
            String newBody = newExchange == null ? "" : newExchange.getIn().getBody(String.class);
            System.out.println("Old Body:" + oldBody);
            System.out.println("New Body:" + newBody);

            lastKey = newBody.substring(0, 3);

            // If a body has been cached, it means either
            // We need to close down the current aggregate, or
            // We need to start the new aggregate with the cached body
            // and use it the next time
            if (cachedBody != null) {
                if (oldExchange != null) {
                    return oldExchange;
                } else {
                    recordCount++;
                    oldBody = cachedBody;
                    cachedBody = null;
                }
            }
         
            if (oldExchange == null) {
                oldExchange = newExchange;
            }

            if ("".equals(oldBody)) {
                oldBody = newBody;
            } else {
                oldBody += "\n" + newBody;
            }
            oldExchange.getIn().setBody(oldBody);

            recordCount++;

            return oldExchange;
        }

        @Override
        public boolean matches(Exchange exchange) {
            cachedBody = null;
            String theBody = exchange == null ? "" : exchange.getIn().getBody(String.class);
            System.out.println("Checking for completion. Eager... so this is pre-aggregate:" + theBody);
            String nextKey = theBody.substring(0, 3);
            if (recordCount >= 5 && !nextKey.equals(lastKey)) {
                System.out.println("That's it... agg complete");
                recordCount = 0;
                cachedBody = theBody;
                return true;
            }
            return false;
        }

        // TODO: Camel 2.16+ can implement PreCompletionAwareAggregationStrategy
        // This is a pre-check instead of a post-check. If used, the predicate
        // will be ignored.
        // @Override
        // public boolean preComplete(Exchange oldExchange, Exchange
        // newExchange) {
        // String theBody = newExchange == null ? "" :
        // newExchange.getIn().getBody(String.class);
        // System.out.println("PreComplete NewBody:" + theBody);
        // String nextKey = theBody.substring(0, 3);
        // if (recordCount >= 5 && !nextKey.equals(lastKey)) {
        // System.out.println("That's it... we're pre-complete");
        // recordCount = 0;
        // return true;
        // }
        // return false;
        // }

    }

}
