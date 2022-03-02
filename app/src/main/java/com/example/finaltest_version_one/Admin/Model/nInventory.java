package com.example.finaltest_version_one.Admin.Model;

public class nInventory {

    private String QRcode;
    private String status;
    private String category;
    private String subcategory;
    private String name;
    private int quantity;
    private double capital;
    private double price;
    private int criticalValue;
    private String downloadURL;
    private String state;

    public nInventory(String QRcode, String status, String category, String subcategory, String name, int quantity, double capital, double price, int criticalValue, String downloadURL, String state) {
        this.QRcode = QRcode;
        this.status = status;
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.quantity = quantity;
        this.capital = capital;
        this.price = price;
        this.criticalValue = criticalValue;
        this.downloadURL = downloadURL;
        this.state = state;
    }

    public nInventory() {
    }

    public String getQRcode() {
        return QRcode;
    }

    public void setQRcode(String QRcode) {
        this.QRcode = QRcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCriticalValue() {
        return criticalValue;
    }

    public void setCriticalValue(int criticalValue) {
        this.criticalValue = criticalValue;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "nInventory{" +
                "QRcode='" + QRcode + '\'' +
                ", status='" + status + '\'' +
                ", category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", capital=" + capital +
                ", price=" + price +
                ", criticalValue=" + criticalValue +
                ", downloadURL='" + downloadURL + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
