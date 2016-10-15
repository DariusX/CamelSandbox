package com.zerses.camelsandbox.sql;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestRoute08 extends CamelSpringTestSupport{

	@Override
  protected AbstractXmlApplicationContext createApplicationContext() {
      return new ClassPathXmlApplicationContext("META-INF/spring/jdbcRoute.xml");
   } 
	
  @Override
  protected RouteBuilder createRouteBuilder() throws Exception {
    // TODO Auto-generated method stub
    return new RouteBuilder08();
  }

  

  @Test
  public void testJdbcInsert() throws Exception
  {
    template.sendBodyAndHeader(RouteBuilder08.DIRECT_JDBC_INSERT, "A", "hdr1","B");
    Thread.sleep(2000);
  }
  
  @Test
  public void testJdbcSelectList() throws Exception
  {
    template.sendBodyAndHeader(RouteBuilder08.DIRECT_JDBC_SELECT_LIST , "A", "hdr1","B");
    Thread.sleep(2000);
  }
 
  @Test
  public void testJdbcSelectOne() throws Exception
  {
    template.sendBodyAndHeader(RouteBuilder08.DIRECT_JDBC_SELECT_ONE , "A", "THE_FILE_NAME","A");
    Thread.sleep(2000);
  }
  
  
}
