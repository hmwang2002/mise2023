package com.example.android_demo.bean;

import java.util.Objects;

public class RegisterRequest {
    private String userName;
    private String password;
    private String phone;
    private String verifyCode;
    public RegisterRequest(String userName, String password, String phone, String verifyCode) {
        this.userName = userName;
        this.password = password;
        this.phone = phone;
        this.verifyCode = verifyCode;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterRequest that = (RegisterRequest) o;
        return Objects.equals(userName, that.userName)
                && Objects.equals(password, that.password)
                && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, phone, verifyCode);
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phone + '\'' +
                ", captcha='"+ verifyCode +'\''+
                '}';
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String checkPassword) {
        this.phone = checkPassword;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

}
