package com.zerses.camelsandbox.jms;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ActiveMQSpringHelloWorld {
  public static void main(String[] args) throws Exception {
    ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    CamelContext camelContext = SpringCamelContext.springCamelContext(appContext, false);
    try {
      ProducerTemplate template = camelContext.createProducerTemplate();
      camelContext.start();
      template.sendBody("activemq:test.queue", "Hello World Spring");
      Thread.sleep(2000);
    } finally {
      camelContext.stop();
    }
  }
}
