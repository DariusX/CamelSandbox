package com.zerses.camelsandbox.jpa;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class JpaPollEnrichRoute extends RouteBuilder {

    //Tests JPA. Needs a few dependencies for caml-jpa, Hibernate and MySql
    //Needs the persistence.xml file to define the persistence unit
    //Mysql needs to be running
	@Override
	public void configure() throws Exception {

		from("direct:personAgeRange")
		.log(LoggingLevel.INFO, "Initial msg: ${body}")
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				String body = exchange.getIn().getBody(String.class);
				String [] tokens = body.replaceAll(" ", "").split(",");
				List<Integer> x = Arrays.asList(new Integer[] {Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])});
				exchange.getIn().setBody(x);
			}
		})
		.log(LoggingLevel.INFO, "Next msg: ${body}")
		.pollEnrich("jpa://com.xby2.dcooper.entity.Person?consumeDelete=false&consumer.namedQuery=Person.findAll&persistenceUnit=test1jpa")
		.log(LoggingLevel.INFO, "third msg: ${body}");
	}

}
