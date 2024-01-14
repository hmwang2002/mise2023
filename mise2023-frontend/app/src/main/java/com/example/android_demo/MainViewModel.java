package com.example.android_demo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private static MutableLiveData<String> username;

    private static MutableLiveData<String> password;

    private static MutableLiveData<String> phone;

    private static MutableLiveData<String> avatar;


    public MainViewModel(){
    }

    public MutableLiveData<String> getUsername() {
        if (username == null) {
            username = new MutableLiveData<String>();
        }
        return username;
    }
    public MutableLiveData<String> getPassword() {
        if (password == null) {
            password = new MutableLiveData<String>();
        }
        return password;
    }

    public MutableLiveData<String> getPhone() {
        if (phone == null) {
            phone = new MutableLiveData<String>();
        }
        return phone;
    }

    public MutableLiveData<String> getAvatar() {
        if (avatar == null) {
            avatar = new MutableLiveData<String>();
        }
        return avatar;
    }


}
