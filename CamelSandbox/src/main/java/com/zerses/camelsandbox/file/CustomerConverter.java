package com.zerses.camelsandbox.file;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Body;
import org.apache.camel.Converter;
import org.apache.log4j.Logger;

public class CustomerConverter {

  public List<Customer1> delimStringToCustomerList(@Body String strLines)
  {
    return linesToCustomerList(strLines, true);
  }

  public List<Customer1> fixedStringToCustomerList(@Body String strLines)
  {
    return linesToCustomerList(strLines, false);
  }
  
  private List<Customer1> linesToCustomerList(String strLines, boolean delimited) {
    Logger.getLogger(this.getClass()).info("strLines: " + strLines);

    String[] lines = strLines.split("\\r?\\n");
    List<Customer1> theList = new ArrayList<Customer1>();
    for (String nxtLine : lines) {
      Logger.getLogger(this.getClass()).info("Next line [" + nxtLine + "]");
      Customer1 customer =
          delimited ? delimStringToCustomer(nxtLine) : fixedStringToCustomer(nxtLine);
      theList.add(customer);
    }

    System.out.println("theList.size(): " + theList.size());
    return theList;
  }
  
  @Converter
  public Customer1 delimStringToCustomer(String delimString) {
    Logger.getLogger(this.getClass()).info("delimString: " + delimString);

    String[] tokens = delimString.split(",");
    Customer1 customer = new Customer1();
    customer.setId(Integer.parseInt(tokens[0]));
    customer.setFirstName(tokens[1]);
    customer.setLastName(tokens[2]);
    return customer;
  }
  
  @Converter
  public Customer1 fixedStringToCustomer(String fixedString) {
    Logger.getLogger(this.getClass()).info("fixedString: " + fixedString);

    Customer1 customer = new Customer1();
    customer.setId(Integer.parseInt(fixedString.substring(0,5).trim()));
    customer.setFirstName(fixedString.substring(5,15).trim());
    customer.setLastName(fixedString.substring(15).trim());
    return customer;
  }
  

}
