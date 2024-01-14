package com.example.android_demo.entity;

/**
 * @author SummCoder
 * @date 2023/11/25 13:44
 */
public class LoginInfo {
    public int id;
    public String username;
    public String password;
    public boolean remember = false;

    public LoginInfo(){}

    public LoginInfo(String username, String password, boolean remember) {
        this.username = username;
        this.password = password;
        this.remember = remember;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", remember=" + remember +
                '}';
    }
}
