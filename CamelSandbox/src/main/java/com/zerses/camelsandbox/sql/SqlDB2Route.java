package com.zerses.camelsandbox.sql;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class SqlDB2Route extends RouteBuilder {

	@Override
	public void configure() throws Exception {
	    from("direct:sqlDb2Route")
        .to("sql:select * from WSUTDBB.WSUTP1T0 where PL_TRANSACTION_ID = 43668?dataSource=AODB2")
        .log("After SQL: ${body}")
        .marshal()
        .json(JsonLibrary.Jackson)
        .log("After Marshal: ${body}");  
	}

}
