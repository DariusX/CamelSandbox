package com.zerses.camelsandbox.jpa;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TestJpaPollEnrich extends CamelTestSupport {

  @Override
  protected RouteBuilder createRouteBuilder() throws Exception {

    return new JpaPollEnrichRoute();
  }

  
  @Override
  public void setUp() throws Exception {
    super.setUp();
  }


  @Test
  public void test1() throws Exception
  {
    template.sendBody("direct:personAgeRange", "70, 75");
    Thread.sleep(5000);
    //TODO: assertTrue("Out file was created",(new File("c:/test/outcsv/combo2.txt")).exists());
    
  }
}
