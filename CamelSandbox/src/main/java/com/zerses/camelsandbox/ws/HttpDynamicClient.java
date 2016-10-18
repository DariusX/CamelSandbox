package com.zerses.camelsandbox.ws;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;

public class HttpDynamicClient extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:testMultiple")
        .split(body())
        .to("direct:httpClient");
        
        from("direct:httpClient")
        .log("starting httpClient route")
        .setHeader(Exchange.HTTP_URI, simple("${body}"))
        .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET))
        .to("http4://google.com")
        .process(new BodyToStringConverter())
        .log(LoggingLevel.INFO, "Output was ${body}");
    }

    private static class BodyToStringConverter implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            exchange.getOut().setBody(exchange.getIn().getBody(String.class));
        }
    }


    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            Logger logger = Logger.getLogger(HttpDynamicClient.class);

            context.addRoutes(new HttpDynamicClient());
            ProducerTemplate template = context.createProducerTemplate();
            context.start();
            Thread.sleep(1000);


            template.sendBody("direct:httpClient", "http://jsonplaceholder.typicode.com/posts/1");
            Thread.sleep(2000);
            
            template.sendBody("direct:testMultiple", new String [] {"http://jsonplaceholder.typicode.com/posts/1" , "http://jsonplaceholder.typicode.com/posts/1"});
            

        } finally {
            context.stop();
        }
    }

}
