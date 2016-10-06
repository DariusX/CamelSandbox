package com.zerses.camelsandbox.common;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class DataTypeCheck implements Processor {

    private String marker;
    
    public DataTypeCheck() {
        this("");
    }

    public DataTypeCheck(String marker) {
       this.marker = marker;
    }
    
    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        if (body != null) {
            System.out.println(marker+body.getClass().getName());
        }
    }

}
