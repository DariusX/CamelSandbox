package com.zerses.camelsandbox.file;

import java.io.Serializable;

//Coming in Camel 2.18 :   import org.apache.camel.dataformat.bindy.annotation.BindyConverter;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",")
public class Customer1 implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @DataField(pos=1)
  private int id;
  
  @DataField(pos=2, defaultValue="", trim=true)
//Coming in Camel 2.18 :  @BindyConverter(value = com.xby2.dcooper.MyConverter.class)
  private String firstName;
  
  @DataField(pos=3)
  private String lastName;
  
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String toString()
  {
    return "Id:"+id+" Name:"+lastName+", "+firstName;
  }

}
