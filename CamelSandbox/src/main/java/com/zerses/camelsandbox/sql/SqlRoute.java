package com.zerses.camelsandbox.sql;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;

public class SqlRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
	    from("file://c:/test/in1a?noop=true")
	    .idempotentConsumer(simple("${header.CamelFileAbsolutePath}"), MemoryIdempotentRepository.memoryIdempotentRepository(200))
	    .skipDuplicate(false)
        .log(LoggingLevel.INFO, "Reading file: ${file:name} / ${header.CamelFileAbsolutePath} / ${header.CamelFileLength}")
        .to("file://c:/test/out")
        .convertBodyTo(String.class)
        .setProperty("fileName", simple("${header.CamelFileAbsolutePath}"))
        .setProperty("fileContents", body())
        .to("sql:insert into TEST1.FILE_UPLOAD (FILE_NAME, FILE_CONTENTS) VALUES(:#${property.fileName} , :#${property.fileContents})?dataSource=dsTest1");

	    
	    
	}

}
