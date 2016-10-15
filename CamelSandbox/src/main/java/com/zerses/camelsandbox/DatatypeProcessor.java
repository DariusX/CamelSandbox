package com.zerses.camelsandbox;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public final class DatatypeProcessor implements Processor {

    private String headerFieldName = "bodyDatatype";

    public DatatypeProcessor() {
        super();
    }

    public DatatypeProcessor(String headerFieldName) {
        super();
        if (headerFieldName != null && headerFieldName.length() > 0) {
            this.headerFieldName = headerFieldName;
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object objBody = exchange.getIn().getBody();
        String bodyType = objBody == null ? "null" : objBody.getClass().getName();
        exchange.getOut().setHeader(headerFieldName, bodyType);
        
    }
}