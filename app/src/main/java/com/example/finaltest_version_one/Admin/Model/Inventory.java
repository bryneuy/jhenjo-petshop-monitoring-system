package com.example.finaltest_version_one.Admin.Model;

public class Inventory {

    private String name;
    private int quantity;
    private double price;
    private String category;
    private String status;
    private int criticalValue;
    private String role;
    private String downloadURL;

    public Inventory(String name, int quantity, double price, String category, String status, int criticalValue, String role, String downloadURL) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.status = status;
        this.criticalValue = criticalValue;
        this.role = role;
        this.downloadURL = downloadURL;
    }

    public Inventory() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCriticalValue() {
        return criticalValue;
    }

    public void setCriticalValue(int criticalValue) {
        this.criticalValue = criticalValue;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", criticalValue=" + criticalValue +
                ", role='" + role + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                '}';
    }
}

