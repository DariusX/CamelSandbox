package com.zerses.camelsandbox.jms;

import java.io.File;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TestJmsAndException extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        camelContext.addComponent("jms", JmsComponent.jmsComponentClientAcknowledge(connectionFactory));

        return camelContext;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        // TODO Auto-generated method stub
        RouteBuilder rb = new JmsAndExceptionTest();
        return rb;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    // TODO: Commented out. Some of these tests need Java 8, which won't work
    // with other stuff in this project

    @Test
    public void testBulkInput() throws Exception {
        createMockExtServer();

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpoints("direct:singleRecordRoute", "direct:singleValueRoute");
            }
        });

        MockEndpoint mockResult1 = getMockEndpoint("mock:direct:singleRecordRoute");
        MockEndpoint mockResult2 = getMockEndpoint("mock:direct:singleValueRoute");

        mockResult1.expectedBodiesReceived("1,Jane,Doe", "2,John,Smith", "3,Alfred,Einstein", "4,Marie,Curie", "5,Joan, d'Arc", "JUNK");

        // TODO: Why doesn't the second one work? it says the count received is
        // zero ?
        mockResult2.expectedMinimumMessageCount(6);

        template.sendBody("direct:bulkRecordsRoute", "1,Jane,Doe\n2,John,Smith\n3,Alfred,Einstein\n4,Marie,Curie\n5,Joan, d'Arc\nJUNK");

        Thread.sleep(3000);

        assertMockEndpointsSatisfied();

    }

    @Test
    public void testMainQueueSplitting() throws Exception {
        template.sendBodyAndHeader("direct:toJMSQueueMain", "Input message A1", "JMSCorrelationID", "TestCorrId_A");
        template.sendBodyAndHeader("direct:toJMSQueueMain", "Input message B1", "JMSCorrelationID", "TestCorrId_B");
        template.sendBodyAndHeader("direct:toJMSQueueMain", "Input message A2", "JMSCorrelationID", "TestCorrId_A");
        Thread.sleep(3000);

    }

    //
    // @Test
    // public void testSingleRecord() throws Exception {
    // createMockExtServer();
    //
    // context.getRouteDefinitions().get(0).adviceWith(context, new
    // AdviceWithRouteBuilder() {
    // @Override
    // public void configure() throws Exception {
    // mockEndpoints("direct:singleValueRoute");
    // }
    // });
    //
    // MockEndpoint mockResult2 =
    // getMockEndpoint("mock:direct:singleValueRoute");
    // // TODO: This test fails because I put in a dynamic router that sends
    // things to a wrong location.
    // // commenting that out will get the right result
    // mockResult2.expectedBodiesReceived("Jane");
    //
    // template.sendBody("direct:bulkRecordsRoute","1,Jane,Doe");
    //
    // Thread.sleep(3000);
    //
    // assertMockEndpointsSatisfied();
    //
    // }
    //
    // @Test
    // public void testSingleValue() throws Exception {
    // createMockExtServer();
    //
    // context.getRouteDefinitions().get(0).adviceWith(context, new
    // AdviceWithRouteBuilder() {
    // @Override
    // public void configure() throws Exception {
    // mockEndpoints("jetty:http://localhost:9080/mytest");
    // }
    // });
    //
    // MockEndpoint mockResult2 =
    // getMockEndpoint("mock:jetty:http://localhost:9080/mytest");
    // mockResult2.expectedBodiesReceived("Spidey");
    //
    // // TODO: Why doesn't this work?
    // mockResult2.expectedMinimumMessageCount(6);
    //
    // template.sendBody("direct:singleValueRoute","Spidey");
    //
    // Thread.sleep(3000);
    //
    // assertMockEndpointsSatisfied();
    //
    // }

    private void createMockExtServer() {
        MockEndpoint mock = getMockEndpoint("mock:extServer");
        // mock.expectedBodiesReceived("ID=123");
        mock.whenAnyExchangeReceived(new Processor() {
            public void process(Exchange exchange) throws Exception {
                String strBody = exchange.getIn().getBody(String.class);
                if (strBody == null || strBody.length() == 0) {
                    strBody = "Dear user";
                }
                exchange.getIn().setBody(strBody + ", All is well.");
            }
        });
    }

}
