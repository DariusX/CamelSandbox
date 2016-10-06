package com.zerses.camelsandbox;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.main.Main;

//From https://examples.javacodegeeks.com/enterprise-java/apache-camel/apache-camel-hello-world-example/
public class PropertyFileTest {

    public static void main(String[] args) throws Exception {

        Main main = new Main();

        main.addRouteBuilder(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer://myTimer?period={{millisecs}}")
                .setBody()
                .simple("Hello Main Camel fired at ${header.firedTime}")
                .log("${body}");
            }
        });

        
        
        PropertiesComponent pc = new PropertiesComponent();
        //Set the name of the properties file. Will look along the classpath
        pc.setLocation("proptest.properties");
        main.bind("properties", pc);
        
        main.run(args);


    }

}
