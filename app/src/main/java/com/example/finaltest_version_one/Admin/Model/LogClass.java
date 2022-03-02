package com.example.finaltest_version_one.Admin.Model;


import java.util.Date;

public class LogClass {
    Date ddate;
    double price;
    public LogClass(){}
    public LogClass(Date ddate, double price){
        this.ddate = ddate;
        this.price = price;
    }
    public Date getValue() {
        return ddate;
    }
    public void setValue(Date ddate) {
        this.ddate = ddate;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
