package com.zerses.camelsandbox.file;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.language.ConstantExpression;

public class FileSplitterRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		String inDir = "c:/test/in1a";

		from("file://" + inDir + "?move=../arch/${date:now:yyyyMMddhhmmss}.${file:name}")
				.log(LoggingLevel.INFO, "Reading file: ${file:name}")
				.filter(PredicateBuilder.not(header("CamelFileName").contains("_TEST_")))
				.filter(PredicateBuilder.or(header("CamelFileName").endsWith(".csv"),
						header("CamelFileName").endsWith(".txt")))
				.log(LoggingLevel.INFO, "Production file: ${file:name}").filter(new Predicate() {

					public boolean matches(Exchange exchange) {
						String filename = exchange.getIn().getHeader("CamelFileName", String.class);
						return !filename.contains("123");
					}
				}).to("direct:splitFile");

		BindyCsvDataFormat bindyFormat = new BindyCsvDataFormat(Customer1.class);

		from("direct:splitFile")
		.id("direct:splitFile")
		.log(LoggingLevel.INFO, "${id} File to process: ${file:name}")
				.split(body()
				    .tokenize("\n"))
				.streaming()
				.choice()
				.when(header("CamelFileName").endsWith(".txt"))
				.bean(new CustomerConverter(), "fixedStringToCustomer")
				// .when(header("CamelFileName").endsWith(".csv")).bean(new CustomerConverter(), "delimStringToCustomer")
				.when(header("CamelFileName").endsWith(".csv"))
				.unmarshal(bindyFormat).end()
				.bean(new CustomerProcessor(), "processCustomer").log("${id} Done record from: ${file:name}").end()
				.log("${id} Spitting is done")
				.aggregate(new ConstantExpression("Z"), new CustomerAggregator())
				.completionSize(2).to("direct:processRecords");

		from("direct:processRecords").bean(new CustomerProcessor(), "processList")
				.log("${id} Final Body: ${file:name} ${body}");
		
		
		
//	      from("file://" + inDir + "?noop=true")
//              .split(body().tokenize("\n")).streaming().parallelProcessing()
//              .process(new DataTypeCheck("After split: "))
//              .unmarshal().csv()
//              .process(new DataTypeCheck("After csv: "))
//              .marshal().csv()
//              .process(new DataTypeCheck("After marshal: "))
//              .end()
//              //.aggregate(new ConstantExpression("Z"))
//              .process(new DataTypeCheck("After aggregate: "));


	}

}
