package com.example.finaltest_version_one.Admin.Model;

public class Accounts {
    private String email;
    private String fullName;
    private String address;
    private int pNumber;
    private int age;
    private String role;
    private String downloadURL;
    private String status;
    private String password;

    public Accounts(String email, String fullName, String address, int pNumber, int age, String role, String downloadURL, String status, String password) {
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.pNumber = pNumber;
        this.age = age;
        this.role = role;
        this.downloadURL = downloadURL;
        this.status = status;
        this.password = password;
    }

    public Accounts() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getpNumber() {
        return pNumber;
    }

    public void setpNumber(int pNumber) {
        this.pNumber = pNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Accounts{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", pNumber=" + pNumber +
                ", age=" + age +
                ", role='" + role + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                ", status='" + status + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
