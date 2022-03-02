package com.example.finaltest_version_one.Admin.Model;

public class Updates {
    String time;
    double price;
    public Updates(){

    }
    public Updates(String time, double price){
     this.time = time;
     this.price = price;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
