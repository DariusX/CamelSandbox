package com.zerses.camelsandbox.file;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TestFileSplitter extends CamelTestSupport {

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		// TODO Auto-generated method stub
		return new FileSplitterRoute();
	}

	@Override
	public void setUp() throws Exception {
		deleteDirectory("c:/test/out");
		super.setUp();
	}

	@Test
	public void testOneCsvFile() throws Exception {
		MockEndpoint mockResult = getMockEndpoint("mock:direct:processRecords");
		//TODO: This is not really a valid test. Just a stand in
		mockResult.expectedMinimumMessageCount(0);

		template.sendBodyAndHeader("file://c:/test/in1a",
				"1,Jane,Doe\n2,John,Smith", Exchange.FILE_NAME, "test05.csv");
		Thread.sleep(5000);
		assertMockEndpointsSatisfied();

	}
}
