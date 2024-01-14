package com.example.android_demo.user;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.android_demo.Constants.constant;
import com.example.android_demo.R;
import com.example.android_demo.bean.Message;
import com.example.android_demo.bean.RegisterRequest;
import com.example.android_demo.utils.ConvertType;
import com.example.android_demo.utils.ResponseData;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    int senCaptchaFlag = 0;

    private static Message message;

    private TextView toLogin;
    private Button register, sendCaptcha;
    private EditText et_username, et_password, et_check, et_phone, et_captcha;
    private String userName, password, checkPassword, phoneNumber, captcha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = findViewById(R.id.re_username);
        et_password = findViewById(R.id.re_password);
        et_check = findViewById(R.id.re_check);
        et_phone = findViewById(R.id.re_phone);
        et_captcha = findViewById(R.id.re_captcha);

        //处理去登陆界面
        toLogin = findViewById(R.id.to_login);
        toLogin.setOnClickListener(view -> toLoginActivity());

        //点击发送验证码按钮
        sendCaptcha = findViewById(R.id.send_captcha);
        sendCaptcha.setOnClickListener(v -> {
            phoneNumber=et_phone.getText().toString().trim();
            //TODO 手机号验证
            sendCaptcha(phoneNumber);
            if(senCaptchaFlag == 1){
                Toast.makeText(RegisterActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(RegisterActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
            }
        });

        //处理注册
        register = findViewById(R.id.re_register);
        register.setOnClickListener(view -> {
            getEditString();
            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(RegisterActivity.this, "请输入您的用户名", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "请输入您的密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(checkPassword)) {
                Toast.makeText(RegisterActivity.this, "请确认您的密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(captcha)) {
                Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(checkPassword)) {
                Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            //生成注册请求体
            RegisterRequest registerRequest = new RegisterRequest(userName, password, phoneNumber, captcha);
            registerToBackend(registerRequest);

            if (message.isFlag()) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("username", userName);
                    bundle.putString("password", password);
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    //跳转到登录界面中
                    toLoginActivity();
                    RegisterActivity.this.finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    //注册结果标志位

    public void registerToBackend(RegisterRequest registerRequest) {
        Thread thread = new Thread(() -> {
            try {
                String json = ConvertType.beanToJson(registerRequest);
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求
                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/user/register")
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();
                // 执行发送的指令
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    String reData=response.body().string();
                    Gson gson = new Gson();
                    ResponseData rdata= gson.fromJson(reData, ResponseData.class);
                    if (rdata.getCode().equals("200")) {
                        message = new Message(true, rdata.getMessage());
                    } else {
                        message = new Message(false, rdata.getMessage());
                    }
                } else {
                    message = new Message(false, "注册失败！请检查网络状况");
                }
            } catch (Exception e) {
                e.printStackTrace();
                message = new Message(false, "注册失败！请检查网络状况");
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendCaptcha(String phoneNum) {
        Thread thread = new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求

                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/sms/send?phone=" + phoneNum)
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData=response.body().string();
                Gson gson = new Gson();
                ResponseData rdata= gson.fromJson(reData, ResponseData.class);
                if (rdata.getCode().equals("200")) {
                    senCaptchaFlag = 1;
               } else {
                    senCaptchaFlag = 0;
                }

            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "网络或进程问题", Toast.LENGTH_SHORT).show());
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getEditString() {
        userName = et_username.getText().toString().trim();
        password = et_password.getText().toString().trim();
        checkPassword = et_check.getText().toString().trim();
        phoneNumber = et_phone.getText().toString().trim();
        captcha = et_captcha.getText().toString().trim();
    }

    private void toLoginActivity() {
        this.finish();
    }

}

