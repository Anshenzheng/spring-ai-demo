package org.an.springai.pojo;

public class User {
    private String userName;
    private String address;
    private int age;
    private String gender;

    public User(String userName, String address, int age, String gender) {
        this.userName = userName;
        this.address = address;
        this.age = age;
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
