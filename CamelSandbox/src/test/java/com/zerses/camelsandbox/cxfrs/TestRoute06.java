package com.zerses.camelsandbox.cxfrs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class TestRoute06 extends CamelBlueprintTestSupport{

	// override this method, and return the location of our Blueprint XML file
	// to be used for testing
	@Override
	protected String getBlueprintDescriptor() {
		return "/blueprint/blueprintCxfrs.xml";
	}
		 
	@Override
	public void setUp() throws Exception {

		super.setUp();
	}

	@Test
	public void test1() throws Exception {

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:9000/Z/A/inSubpath1/inSubpath2?parm1=myValue");

		GenericType<String> messageContent = new GenericType<String>() {};
		Response r = webTarget.request(MediaType.APPLICATION_JSON).get();
		String responseString = webTarget.request(MediaType.APPLICATION_JSON)
				.get(messageContent);
		System.out.println(responseString);
		// template.sendBody("cxfrs:bean:myRsResource1", "Test message");
		//Thread.sleep(2000);
	}
}
