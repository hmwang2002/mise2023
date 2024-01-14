package com.example.android_demo.user;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_demo.Constants.constant;
import com.example.android_demo.MyApplication;
import com.example.android_demo.R;
import com.example.android_demo.bean.Message;
import com.example.android_demo.bean.RegisterRequest;
import com.example.android_demo.database.DBHelper;
import com.example.android_demo.entity.LoginInfo;
import com.example.android_demo.utils.ResponseData;
import com.example.android_demo.utils.ConvertType;
import com.example.android_demo.utils.UserUtils;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author SummCoder
 * @date 2023/11/26 22:15
 */
public class LoginActivity extends AppCompatActivity implements View.OnFocusChangeListener{
    private DBHelper mHelper;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox cb_remember;

    private String phoneNumber, verify;
    private String username;
    private String password;

    private String avatar = "https://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        showLoginDialog();
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("登录");

        // 设置对话框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        builder.setView(view);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 获取输入框和登录按钮
        usernameEditText = view.findViewById(R.id.et_username);
        passwordEditText = view.findViewById(R.id.et_password);
        Button loginButton = view.findViewById(R.id.btn_login);
        cb_remember = view.findViewById(R.id.cb_remember);

        // 阻止用户进行其他操作
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        passwordEditText.setOnFocusChangeListener(this);

        ActivityResultLauncher<Intent> register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result != null){
                Intent intent = result.getData();
                if(intent != null && result.getResultCode() == Activity.RESULT_OK){
                    Bundle bundle = intent.getExtras();
                    username = bundle.getString("username");
                    password = bundle.getString("password");
                    login(username, password);
                    dialog.dismiss();
                    Intent intent1 = new Intent();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("username", username);
                    bundle1.putString("password", password);
                    bundle1.putString("avatar", avatar);

                    intent1.putExtras(bundle1);
                    setResult(Activity.RESULT_OK, intent1);

                    finish();
                }
            }
        });


        TextView tvRegister = dialog.findViewById(R.id.tv_register);
        assert tvRegister != null;
        tvRegister.setOnClickListener(v -> {
            // 启动RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            register.launch(intent);
        });

        //忘记密码
        TextView tvForget = dialog.findViewById(R.id.tv_forget);
        assert tvForget != null;

        tvForget.setOnClickListener(v -> {
             showResetDialog();
        });

        // 设置登录按钮的点击事件
        loginButton.setOnClickListener(v -> {
            username = usernameEditText.getText().toString();
            password = passwordEditText.getText().toString();
            // 进行登录验证，这里可以根据具体的登录逻辑进行处理
            Message message = login(username, password);
            if (message.isFlag()) {
                // 记住密码
                LoginInfo loginInfo = new LoginInfo(username, password, cb_remember.isChecked());
                mHelper.saveLoginInfo(loginInfo);
                // 登录成功，关闭对话框
                dialog.dismiss();

                Intent intent1 = new Intent();
                Bundle bundle1 = new Bundle();
                bundle1.putString("username", username);
                bundle1.putString("password", password);

                getAvatar();
                bundle1.putString("avatar", avatar);
                intent1.putExtras(bundle1);
                setResult(Activity.RESULT_OK, intent1);
                Toast.makeText(LoginActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // 登录失败，提示用户登录失败
                Toast.makeText(LoginActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 登录验证的方法
    private Message login(String username, String password) {
        // 进行登录验证，这里可以根据具体的登录逻辑进行处理
        return UserUtils.login(username, password);
    }

    private void getAvatar() {
        Thread thread = new Thread(() -> {
            // 获取头像
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求

                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/user/getAvatar")
                        .addHeader("satoken", Objects.requireNonNull(MyApplication.getInstance().infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData=response.body().string();
                System.out.println(reData);
                Gson gson = new Gson();
                ResponseData rdata= gson.fromJson(reData, ResponseData.class);
                if (rdata.getCode().equals("200")) avatar = rdata.getData().toString();
                Log.i(TAG, "getAvatar: " + avatar);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 打开数据库读写连接
        mHelper = DBHelper.getInstance(this);
        mHelper.openReadLink();
        mHelper.openWriteLink();
        reload();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 关闭数据库连接
        mHelper.closeLink();
    }

    // 进入页面时加载数据库中存储的用户名和密码
    private void reload(){
        LoginInfo info = mHelper.queryTop();
        if(info != null && info.remember){
            usernameEditText.setText(info.username);
            passwordEditText.setText(info.password);
            cb_remember.setChecked(true);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(view.getId() == R.id.et_password && hasFocus){
            LoginInfo info = mHelper.queryByUsername(usernameEditText.getText().toString());
            if(info != null){
                passwordEditText.setText(info.password);
                cb_remember.setChecked(info.remember);
            }else {
                passwordEditText.setText("");
                cb_remember.setChecked(false);
            }
        }
    }

    private void showResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改密码");

        // 设置对话框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change, null);
        builder.setView(view);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 获取输入框和登录按钮
        EditText passwordEditText = view.findViewById(R.id.et_password);
        EditText phoneEditText = view.findViewById(R.id.et_phone);
        EditText verifyEditText = view.findViewById(R.id.et_verify);

        Button sendButton = view.findViewById(R.id.btn_send);
        Button resetButton = view.findViewById(R.id.btn_reset);
        Button quitButton = view.findViewById(R.id.btn_quit);

        // 设置发送验证码按钮的点击事件
        sendButton.setOnClickListener(v -> {

            phoneNumber = phoneEditText.getText().toString().trim();
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
                            .url(constant.IP_ADDRESS + "/sms/send?phone=" + phoneNumber)
                            .build();
                    // 执行发送的指令，获得返回结果
                    Response response = client.newCall(request).execute();
                    String reData=response.body().string();
                    Gson gson = new Gson();
                    ResponseData rdata= gson.fromJson(reData, ResponseData.class);
                    if (rdata.getCode().equals("200")) {
                        Looper.prepare();
                        Toast.makeText(this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Looper.prepare();
                        Toast.makeText(this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    Looper.prepare();
                    Toast.makeText(this, "网络或进程问题", Toast.LENGTH_SHORT).show();
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 设置reset按钮的点击事件
        resetButton.setOnClickListener(v -> {
            password = passwordEditText.getText().toString().trim();
            phoneNumber = phoneEditText.getText().toString().trim();
            verify = verifyEditText.getText().toString().trim();

            RegisterRequest registerRequest = new RegisterRequest(username,password, phoneNumber, verify);

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
                            .url(constant.IP_ADDRESS + "/user/update")
                            .post(RequestBody.create(MediaType.parse("application/json"), json))
                            .build();
                    // 执行发送的指令
                    Response response = client.newCall(request).execute();

                    if (response.code() == 200) {
                        String reData = response.body().string();
                        System.out.println("redata" + reData);
                        Gson gson = new Gson();
                        ResponseData rdata = gson.fromJson(reData, ResponseData.class);
                        System.out.println(rdata.getData());
                        if (rdata.getCode().equals("200")) {
                            Looper.prepare();
                            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                            // 关闭对话框
                            dialog.dismiss();
                        } else {
                            Looper.prepare();
                            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        quitButton.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
    }

}
