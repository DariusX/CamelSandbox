package com.zerses.camelsandbox.jms;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.model.language.SimpleExpression;


public class JmsAndExceptionTest extends RouteBuilder {

  @Override
  public void configure() throws Exception {

	  //TODO: How to scope Exception-handling to just one route?
		onException(IllegalArgumentException.class)
		.handled(true)
		.bean(InvalidRecordHandler.class, "handleInvalidRecord")
		.end();

		from("direct:bulkRecordsRoute")
		.log("from bulk route: ${in.body}")
		.split(body().tokenize("\n")).streaming()
		.to("direct:singleRecordRoute");

		from("direct:singleRecordRoute")
		.log("from record route: ${in.body}")
		.inOut("jms:parseRecord")
		.to("direct:singleValueRoute");
		
        from("jms:parseRecord").bean(RecordParser.class, "parseThis");
        
		from("direct:singleValueRoute")
		//.dynamicRouter(method(RouteCalculator.class, "computeNewHop"))
		.log("from single-value route: ${in.body}")
		.setHeader("ID", new SimpleExpression("${in.body}"))
		.setProperty("ID", new ConstantExpression("MY_PROPERTY_VAL"))
//		.to("jetty:http://localhost:9080/mytest")
		.log("At end of Direct route: ${in.body}");
		
//
//		
//		from("jetty:http://localhost:9080/mytest")
//		.process(new RequestProcessor())
//		.log("Jetty route ... Body to be sent: ${body}")
//		.to("mock:extServer")
//		.log("Jetty route ... Body returned: ${body}")
//		.process(new ResponseProcessor())
//		.log("Jetty route ... Body after post-process is: ${body}")
//		.dynamicRouter(method(RouteCalculator.class, "computeNewHop"));

		from("direct:toJMSQueueMain")
		.log("from Main Queue: ${in.body} Correlation: ${header.JMSCorrelationID}")
		.to("jms:myQueueMain?concurrentConsumers=2")
		;

		from("jms:myQueueMain?selector=JMSCorrelationID%3D%27TestCorrId_A%27")
		.log("from JMS reader A : ${in.body} Correlation: ${header.JMSCorrelationID}")
        ;
		
		from("jms:myQueueMain?selector=JMSCorrelationID%3D%27TestCorrId_B%27")
		.log("from JMS reader B : ${in.body} Correlation: ${header.JMSCorrelationID}")
        ;
		
  }
  
	public static class RouteCalculator {
		static int theCount = 0;

		public String computeNewHop(Exchange exchange, String body, @Header(Exchange.SLIP_ENDPOINT) String previous) {
			theCount++;
			System.out.println("From Dynamic Router ----------------------. Previous = " + previous);
			String nextHop = "jms:parseRecord";
			if (theCount > 2) {
				return null;
			}
			return nextHop;
		}
	}
  
  
  private class RequestProcessor implements Processor {
      public void process(Exchange exchange) throws Exception {
    	  System.out.println("Body: "+exchange.getIn().getBody(String.class));
          String propId = exchange.getProperty("ID", String.class);
    	  System.out.println("property[ID] : "+propId);
          String hdrId = exchange.getIn().getHeader("ID", String.class);
    	  System.out.println("header[ID] : "+hdrId);

          exchange.getIn().setBody("Dear " + hdrId);
      }
  }
  
  private class ResponseProcessor implements Processor {
      public void process(Exchange exchange) throws Exception {
          String body = exchange.getIn().getBody(String.class);
         // String reply = ObjectHelper.after(body, "STATUS=");
          exchange.getIn().setBody("<html><body><h1>External service said: "+body+"</h1></body></html>");
      }
  }
  
  public static class RecordParser {
	    public static void parseThis(Exchange exchange) {
	    	  System.out.println("In RecordParser Bean");

	    	  String strBody = exchange.getIn().getBody(String.class);
	    	  System.out.println("In RecordParser Bean. Body was:"+strBody);
	    	  String [] fields = strBody.split(",");
	    	  if (fields.length < 3)
	    	  {
	        	  System.out.println("In RecordParser Bean: less than 3 fields. Will set Exception");
	    		  IllegalArgumentException e = new IllegalArgumentException("Input record did not have 3 (comma-delimited) fields");
	    		  //throw e;
	    		  exchange.setException(e);
	    		  
	    	  }
	    	  else
	    	  {
	        	  System.out.println("In RecordParser Bean: OK - 3 fields found");
	              exchange.getOut().setBody(fields[1]);
	    	  }


	    }
	}
  
  public static class InvalidRecordHandler {
	    public static void handleInvalidRecord(Exchange exchange) {

	            exchange.getIn().setBody("Default User");

	    }
	}

}
