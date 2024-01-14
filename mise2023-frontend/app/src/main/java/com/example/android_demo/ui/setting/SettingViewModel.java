package com.example.android_demo.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel {

    private  MutableLiveData<String> mText;

    public SettingViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}