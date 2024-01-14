package com.example.android_demo.bean;

public class Message {
    private boolean flag;

    private String message;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }
}
