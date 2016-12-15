package com.zerses.camelsandbox;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class DynamicRouterTest {

    // A simple Camel test with timer (so, nothing external)
    // And no extra (non-core) component requirements
    public static void main(String[] args) throws Exception {

        Main main = new Main();

        main.addRouteBuilder(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer://myTimer?period=50000")
                    .setBody()
                    .simple("Route was fired at ${header.firedTime}")
                    .log("${body}")
                    .dynamicRouter(method(MyDynamicRouter.class, "computeNextHop"));

                from("direct:a")
                    .log("from direct:a");

                from("direct:b")
                    .log("from direct:b");

            }
        });
        main.run();
    }

    public static class MyDynamicRouter {
        public String computeNextHop(String body, @Header(Exchange.SLIP_ENDPOINT) String previous) {
            System.out.println("Dynamic Router. Previous was ... " + previous);
            String nextHop = null;
            if (previous == null) {
                nextHop = "direct://a";
            } else if ("direct://a".equals(previous)) {
                nextHop = "direct://b";
            }
            System.out.println("Dynamic Router. Next should be ... " + nextHop);
            return nextHop;
        }
    }

}
