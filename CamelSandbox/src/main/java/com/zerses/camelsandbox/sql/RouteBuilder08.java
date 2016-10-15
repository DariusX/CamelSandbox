package com.zerses.camelsandbox.sql;

import org.apache.camel.builder.RouteBuilder;

import com.zerses.camelsandbox.DatatypeProcessor;


public class RouteBuilder08 extends RouteBuilder {

    public static final String DIRECT_JDBC_INSERT = "direct:jdbcInsert";
    public static final String DIRECT_JDBC_SELECT_LIST = "direct:jdbcSelectList";
    public static final String DIRECT_JDBC_SELECT_ONE = "direct:jdbcSelectOne";
    public static final String DIRECT_INSPECT_RESULT = "direct:inspectResult";
	
    @Override
    public void configure() throws Exception {
            
        from(DIRECT_JDBC_INSERT)
        .id(DIRECT_JDBC_INSERT)
        .setBody(constant("insert into TEST1.FILE_UPLOAD (FILE_NAME, FILE_CONTENTS) VALUES('A', 'A-Contents')"))
        .to("jdbc:dsTest1")
        .log("Counts Rows/Updates: ${header.CamelJdbcRowCount} / ${header.CamelJdbcUpdateCount}")
        .to(DIRECT_INSPECT_RESULT);
        
      from(DIRECT_JDBC_SELECT_LIST)
        .id(DIRECT_JDBC_SELECT_LIST)
        .setBody(constant("select * from TEST1.FILE_UPLOAD where FILE_NAME = 'A'  "))
        .to("jdbc:dsTest1?outputType=SelectList")
        .log("Counts Rows/Updates: ${header.CamelJdbcRowCount} / ${header.CamelJdbcUpdateCount} Names: ${header.CamelJdbcColumnNames}")
        .to(DIRECT_INSPECT_RESULT);
      
      from(DIRECT_JDBC_SELECT_ONE)
      .id(DIRECT_JDBC_SELECT_ONE)
      .setBody(constant("select count(*) from TEST1.FILE_UPLOAD where FILE_NAME = :?THE_FILE_NAME  "))
      .to("jdbc:dsTest1?outputType=SelectOne&useHeadersAsParameters=true")
      .log("Counts Rows/Updates: ${header.CamelJdbcRowCount} / ${header.CamelJdbcUpdateCount} Names: ${header.CamelJdbcColumnNames}")
      .to(DIRECT_INSPECT_RESULT);

       
        
      //----------------------------------------------------
      from(DIRECT_INSPECT_RESULT).id(DIRECT_INSPECT_RESULT)
      .process(new DatatypeProcessor())
      .log("Body Type: ${header.bodyDatatype} ")
      .log("Property ${property.testProp1}");

    }

}
