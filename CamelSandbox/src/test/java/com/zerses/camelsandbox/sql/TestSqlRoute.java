package com.zerses.camelsandbox.sql;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSqlRoute extends CamelSpringTestSupport{

	@Override
  protected AbstractXmlApplicationContext createApplicationContext() {
      return new ClassPathXmlApplicationContext("META-INF/spring/sqlRoute.xml");
   } 
	
  @Override
  protected RouteBuilder createRouteBuilder() throws Exception {
    // TODO Auto-generated method stub
    return new SqlRoute();
  }

  

  @Test
  public void testFileToDb() throws Exception
  {
    template.sendBodyAndHeader("file://c:/test/in1a", "1,Jane,Doe\n2,John,Smith", Exchange.FILE_NAME, "test004.csv");
    Thread.sleep(3000);
    assertTrue("Out file was created",(new File("c:/test/out/test001.csv")).exists());
    
    template.sendBodyAndHeader("file://c:/test/in1a", "1,Jane,Doe\n2,John,Smith", Exchange.FILE_NAME, "test004.csv");
    Thread.sleep(3000);
    assertTrue("Out file was created",(new File("c:/test/out/test001.csv")).exists());
    
  }
}
