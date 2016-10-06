package com.zerses.camelsandbox.file;

import java.io.File;
import java.util.Collection;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TestFileOsCommand extends CamelTestSupport {

  @Override
  protected RouteBuilder createRouteBuilder() throws Exception {
    // TODO Auto-generated method stub
    return new FileOSCommandRoute();
  }

  
  @Override
  public void setUp() throws Exception {
    deleteDirectory("c:/test/outcsv");
    super.setUp();
  }


  @Test
  public void testOneCsvFile() throws Exception
  {
      System.out.println("********************************************** **************************************");
      
    Collection<Endpoint> endPoints = context.getEndpoints();   
    for (Endpoint nxtEndPoint : endPoints)
    {
        System.out.println("Listing endpoints... :"+ nxtEndPoint.getEndpointUri());
    }
    template.sendBodyAndHeader("file://c:/test/in1a", "1,Jane,Doe\n2,John,Smith", Exchange.FILE_NAME, "test05.csv");
    Thread.sleep(5000);
    assertTrue("Out file was created",(new File("c:/test/outcsv/combo2.txt")).exists());
    
  }
}
