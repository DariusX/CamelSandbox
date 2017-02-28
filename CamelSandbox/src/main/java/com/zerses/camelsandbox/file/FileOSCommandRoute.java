package com.zerses.camelsandbox.file;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import com.zerses.camelsandbox.common.DataTypeCheck;

public class FileOSCommandRoute extends RouteBuilder {


  @Override
  public void configure() throws Exception {

    //Handling an entire file as a single message, without splitting into records
      
    from("file://c:/test/in1a?move=../arch/${date:now:yyyyMMddhhmmss}.${file:name}")
   .process(new DataTypeCheck())
        .log(LoggingLevel.INFO, "Reading file: ${file:name}").choice()
        //Hand-convert fixed-layouts to a List
        .when(header("CamelFileName").endsWith(".txt")).bean(new CustomerConverter(), "fixedStringToCustomerList")
        //Hand-convert delimited-layouts to a List
        .when(header("CamelFileName").endsWith(".csv")).bean(new CustomerConverter(), "delimStringToCustomerList")
        .otherwise().to("file://c:/test/error").stop()
        .end()
        //Hand-convert the list back into delimited records
        .bean(new CustomerProcessor(), "processList")
        .to("file://c:/test/outcsv?fileName=combo1.txt&fileExist=Append")
        
        //Demonstrate calling an OS command (to sort)
        .to("exec:cmd?args=/C sort c:/test/outcsv/combo1.txt")
        .to("file://c:/test/outcsv?fileName=combo2.txt");
  }

}
