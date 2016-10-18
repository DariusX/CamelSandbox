package com.zerses.camelsandbox.ws;

import java.io.Serializable;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.DataFormat;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class RestJsonClientTest extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        DataFormat dataformatJackson = new JacksonDataFormat(MyObject.class);
        DataFormat dataformatGson = new GsonDataFormat(MyObject.class);

        from("direct:restJsonClient").log("starting route").setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET))
            .inOut("http4://jsonplaceholder.typicode.com/posts/1").process(new BodyToStringConverter()).log(LoggingLevel.INFO, "Output was ${body}").multicast()
            .to("direct:testMapFromJackson", "direct:testMapFromGson", "direct:testBindUsingJackson", "direct:testBindUsingGson", "direct:testJacksonBindWithImplicitDataformat");

        from("direct:testMapFromJackson").routeId("direct:testMapFromJackson").unmarshal().json(JsonLibrary.Jackson).process(new DataTypeCheck())
            .log(LoggingLevel.INFO, "Umarshalled type ${in.header.MyDataType} was ${body}");

        from("direct:testMapFromGson").routeId("direct:testMapFromGson").unmarshal().json(JsonLibrary.Gson).process(new DataTypeCheck())
            .log(LoggingLevel.INFO, "Umarshalled type ${in.header.MyDataType} was ${body}");

        from("direct:testBindUsingJackson").routeId("direct:testBindUsingJackson").unmarshal(dataformatJackson).process(new DataTypeCheck())
            .log(LoggingLevel.INFO, "Umarshalled type ${in.header.MyDataType} was ${body}");

        from("direct:testBindUsingGson").routeId("direct:testBindUsingGson").unmarshal(dataformatGson).process(new DataTypeCheck())
            .log(LoggingLevel.INFO, "Umarshalled type ${in.header.MyDataType} was ${body}");

        from("direct:testJacksonBindWithImplicitDataformat").routeId("direct:testJacksonBindWithImplicitDataformat").unmarshal().json(JsonLibrary.Jackson, MyObject.class)
            .process(new DataTypeCheck()).log(LoggingLevel.INFO, "Umarshalled type ${in.header.MyDataType} was ${body}");

    }

    private static class BodyToStringConverter implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            exchange.getOut().setBody(exchange.getIn().getBody(String.class));
        }
    }

    private static class DataTypeCheck implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Object body = exchange.getIn().getBody();
            if (body != null) {
                String dataTypeName = body.getClass().getName();
                // Logger.getLogger(this.getClass()).info(body.getClass().getName());
                exchange.getIn().setHeader("MyDataType", dataTypeName);
            }
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MyObject implements Serializable {

        private static final long serialVersionUID = 1L;
        private int userId;
        private String id;
        private String title;
        private String body;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        @Override
        public String toString() {
            return "MyObject [userId=" + userId + ", id=" + id + ", title=" + title + ", body=" + body + "]";
        }

    }

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            Logger logger = Logger.getLogger(RestJsonClientTest.class);

            context.addRoutes(new RestJsonClientTest());
            ProducerTemplate template = context.createProducerTemplate();
            context.start();
            Thread.sleep(1000);

            // This will retrieve a JSON stream from a REST call, and will
            // multicast it through various routes to demonstrate different
            // approaches to unmarshalling
            template.sendBody("direct:restJsonClient", "Test Input");
            Thread.sleep(2000);

            // This will demonstrate that values missing from the JSON are left
            // as null on the business object
            template.sendBody("direct:testJacksonBindWithImplicitDataformat", "{\"userId\":4}");
            Thread.sleep(2000);

            // This will demonstrate that a value on the JSON not found in the
            // object will be ignored as long as we annotate the business object
            // to ignore such value. Change the annotation from ignoreUnknown
            // true--> to--> false to see an exception instead
            template.sendBody("direct:testBindUsingJackson", "{\"userId\":4, \"someNewField\":99}");
            Thread.sleep(1000);

            // This demonstrates how aJSON array is unmarshalled as a List inside the Map.
            Object ret = template.requestBody("direct:testMapFromJackson", "{\"testArray\": [1,3,5]}");
            if (ret instanceof Map) {
                Object retArray = ((Map)ret).get("testArray");
                if (retArray == null) {
                    logger.info("Array was null or not present");
                } else {
                    logger.info("Return type was: " + retArray.getClass().getName() + ", Values:" + retArray);
                }
            } else {
                logger.info("The expected type -- Map -- was not returned");
            }

        } finally {
            context.stop();
        }
    }

}
