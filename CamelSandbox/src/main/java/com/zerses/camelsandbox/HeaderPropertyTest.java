package com.zerses.camelsandbox;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HeaderPropertyTest extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Reading a Header that has never been set will return a property of
        // the same name, if available
        // However, pulling a property which has not been set will not return
        // anything
        // The property is a higher-level: for the Exchange
        // The header is on the particular message, potentially different at
        // each step along the route and possibly different for In and Out
        // messages of a step
        from("timer://myTimer?period=10000")
        
        .setProperty("A", constant("MyPropertyValue"))
        .log("After setting Property")
        .log("Hdr.A : ${header.A}")
        .log("Prop.A: ${exchangeProperty.A}")
        
        .setHeader("A", constant("MyHeaderValue"))
        .log("After setting Header")
        .log("Hdr.A : ${header.A}")
        .log("Prop.A: ${exchangeProperty.A}")
        
        .setProperty("A", constant("MyPropertyValue"))
        .log("After setting Property")
        .log("Hdr.A : ${header.A}")
        .log("Prop.A: ${exchangeProperty.A}")

        .setBody().simple("Hello World Camel fired at ${header.firedTime}")
        .to("stream:out");

    }

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new HeaderPropertyTest());
            context.start();
            Thread.sleep(10000);
        } finally {
            context.stop();
        }
    }
}
