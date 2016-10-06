package com.zerses.camelsandbox.file;

import java.util.List;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

public class CustomerProcessor {


  public void processList(@Body List<Customer1> customerList , Exchange exchange) throws Exception {
    Logger.getLogger(this.getClass()).info("From processor bean for multi ");
    StringBuilder sb = new StringBuilder();
    for (Customer1 nxtCustomer: customerList)
    {
      sb.append(nxtCustomer.toString());
      sb.append("\n");
    }
    exchange.getIn().setBody(sb.toString());
  }
  
  public void processCustomer(@Body Customer1 customer , Exchange exchange) throws Exception {
    customer.setLastName(customer.getLastName()+".");
    Logger.getLogger(this.getClass()).info("From processor bean for 1. Value="+customer.toString()+" ["+customer.getFirstName()+"]");
    exchange.getIn().setBody(customer);
  }
  
}
