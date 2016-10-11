package com.zerses.camelsandbox.rest;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.log4j.Logger;


public class RestConsumerBindingTest extends RouteBuilder {

	@Override
	public void configure() throws Exception {
	    
	    restConfiguration()
	    .component("jetty")
	    .host("0.0.0.0")
	    .port(9000)
	    .bindingMode(RestBindingMode.auto)
	    ;
	    
            rest("/")
            .put("/A/{subpath1}/{subpath2}")
            .type(MyPojo.class)
            .to("direct:Aget");
            
            from("direct:Aget")
            .log(" hdrs: ${in.header.subpath1} / ${in.header.subpath2} /  ${in.header.parm1}")
            //.log(LoggingLevel.INFO, "Initial msg body: ${body}")
            .to("log:TEST1?showAll=true").process(new Processor() {

                @Override
                public void process(Exchange exchange) throws Exception {
                    Object bodyObj = exchange.getIn().getBody();
                    Logger logger = Logger.getLogger(this.getClass());
                    logger.info(" >>>> Body type is: "+bodyObj.getClass().getName());
                    logger.info(" Body is: "+bodyObj.toString()+".  Nothing will print here unless the http BODY has some content");

                }
            });
	}
	
        public static void main(String[] args) throws Exception {
            CamelContext context = new DefaultCamelContext();
            try {
                context.addRoutes(new RestConsumerBindingTest());
                ProducerTemplate template = context.createProducerTemplate();
                context.start();           
                Thread.sleep(1000);
                
                URL obj = new URL("http://localhost:9000/A/1/3?parm1");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                //Using PUT 
                con.setRequestMethod("PUT");
                
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes("{\"subpath1\" : \"AFGH\" }");  //Sending JSO as the body
                wr.flush();
                wr.close();
                
                int responseCode = con.getResponseCode();
                System.out.println("Reponse code: "+responseCode);


            } finally {
                context.stop();
            }
        }

}
