package com.zerses.camelsandbox.file;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import org.apache.log4j.Logger;

public class CustomerAggregator extends AbstractListAggregationStrategy<Customer1> {

  // @Override
  // public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
  //
  // Customer nxtCustomer = newExchange.getIn().getBody(Customer.class);
  //
  // List<Customer> customerList = null;
  // if (oldExchange == null) {
  // customerList = new ArrayList<>();
  // oldExchange = newExchange;
  // } else {
  // customerList = oldExchange.getIn().getBody(List.class);
  // }
  //
  // customerList.add(nxtCustomer);
  // oldExchange.getIn().setBody(customerList);
  //
  // Logger.getLogger(this.getClass())
  // .info("From Aggregator Class is: " + newExchange.getIn().getBody().getClass().toString());
  // Logger.getLogger(this.getClass())
  // .info("From Aggregator: " + newExchange.getIn().getBody().toString());
  //
  // if (customerList.size() >= 2) {
  // Logger.getLogger(this.getClass())
  // .info("From Aggregator Marking complete. Group size is: " + customerList.size());
  // oldExchange.setProperty(Exchange.AGGREGATION_COMPLETE_CURRENT_GROUP, true);
  // }
  // return oldExchange;
  // }

  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

    oldExchange = super.aggregate(oldExchange, newExchange);
    Logger.getLogger(this.getClass())
    .info("Aggregated body type:"+oldExchange.getProperty(Exchange.GROUPED_EXCHANGE, List.class).getClass().toString());
    List<Customer1> customerList = oldExchange.getProperty(Exchange.GROUPED_EXCHANGE, List.class);
    
    try {
     // int listSize = oldExchange.getProperty(Exchange.AGGREGATED_SIZE, Integer.class);
      int listSize = customerList.size();
      if (listSize >= 2) {
        Logger.getLogger(this.getClass())
            .info("From Aggregator Marking complete. Group size is: " + listSize);
        oldExchange.setProperty(Exchange.AGGREGATION_COMPLETE_CURRENT_GROUP, true);
      }
    } catch (Exception e) {
      Logger.getLogger(this.getClass())
      .info("Aggregated size not found"
          + "");
    }
    return oldExchange;
  }

  @Override
  public Customer1 getValue(Exchange exchange) {
    return exchange.getIn().getBody(Customer1.class);
  }

}
