package com.zerses.camelsandbox.rest;

public class MyPojo {
    private String subpath1;
    private String subpath2;
    private String parm1;
    public String getSubpath1() {
        return subpath1;
    }
    public void setSubpath1(String subpath1) {
        this.subpath1 = subpath1;
    }
    public String getSubpath2() {
        return subpath2;
    }
    public void setSubpath2(String subpath2) {
        this.subpath2 = subpath2;
    }
    public String getParm1() {
        return parm1;
    }
    public void setParm1(String parm1) {
        this.parm1 = parm1;
    }
    @Override
    public String toString() {
        return "MyPojo [subpath1=" + subpath1 + ", subpath2=" + subpath2 + ", parm1=" + parm1 + "]";
    }
    
    

}
