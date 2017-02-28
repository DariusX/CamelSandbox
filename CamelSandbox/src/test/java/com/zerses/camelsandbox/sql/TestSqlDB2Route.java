package com.zerses.camelsandbox.sql;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSqlDB2Route extends CamelSpringTestSupport{

	@Override
  protected AbstractXmlApplicationContext createApplicationContext() {
      return new ClassPathXmlApplicationContext("META-INF/spring/sqlRoute.xml");
   } 
	
  @Override
  protected RouteBuilder createRouteBuilder() throws Exception {
    // TODO Auto-generated method stub
    return new SqlDB2Route();
  }

  

  @Test
  public void test01() throws Exception
  {
    template.sendBodyAndHeader("direct:sqlDb2Route", "Test Message", "hdr1", "val1");
    Thread.sleep(3000);

    
  }
}
