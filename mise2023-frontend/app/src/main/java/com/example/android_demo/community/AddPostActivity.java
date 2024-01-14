package com.example.android_demo.community;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android_demo.Constants.constant;
import com.example.android_demo.MainViewModel;
import com.example.android_demo.MyApplication;
import com.example.android_demo.R;
import com.example.android_demo.ui.chat.ChatActivity;
import com.example.android_demo.utils.FileUtils;
import com.example.android_demo.utils.ResponseData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPostActivity extends AppCompatActivity {
    EditText titleET,contentET,tagET;
    TextView to_ai;
    String title,content,communityId,userId,tagList;
    Button cancel,commit;
    ImageView postAva;

    public static MyApplication application;
    private AddViewModel addViewModel;
    private ActivityResultLauncher<Intent> launcher;
    private String currentPhotoPath;

    private static Boolean isSuccessful;

    // 用于获取子线程返回的url
    private String url = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        assert bundle != null;
        communityId=bundle.getString("community_id");


        addViewModel=new ViewModelProvider(this).get(AddViewModel.class);

        setContentView(R.layout.add_post_layout);
        titleET=findViewById(R.id.titleEditText);
        contentET=findViewById(R.id.contentEditText);
        tagET=findViewById(R.id.tagEditText);

        final Observer<String> avatarObserver = newAvatar -> {
            // 使用 Glide 加载图像并设置给 postAva
            Glide.with(this)
                    .load(newAvatar)
                    .into(postAva);
        };

        //给avatar加上观察器，当avatar值发生改变的时候，调用观察函数
        addViewModel.getAvatar().observe(this, avatarObserver);

        postAva=findViewById(R.id.postImage);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result != null && result.getResultCode() == RESULT_OK) {
                // 从相机获取图片处理
                if (result.getData() == null || result.getData().getData() == null) {
                    // 相机拍照的结果，直接上传
                    uploadPhoto(currentPhotoPath);
                } else {
                    // 相册选择的结果，直接上传
                    String uri = FileUtils.getRealPathFromUri(this, result.getData().getData());
                    uploadPhoto(uri);
                }
            }
        });
        cancel=findViewById(R.id.post_cancel_Button);
        commit=findViewById(R.id.post_commit_Button);
        to_ai=findViewById(R.id.to_AI);

        application = MyApplication.getInstance();
        userId=application.infoMap.get("loginId");

        cancel.setOnClickListener(v-> {
            addViewModel.getAvatar().setValue("http://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/img/image-20231228101219539.png");
            finish();
        });

        postAva.setOnClickListener(view -> {
            // 弹出一个对话框，选择拍照或者从相册选择
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("上传图片");
            builder.setPositiveButton("从相册选择", (dialogInterface, i) -> {
                // 调用相册选择照片
                Intent intent0 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent0);
            });
            builder.setNeutralButton("拍照", (dialogInterface, i) -> {
                // 调用相机拍照
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent1.resolveActivity(this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Error occurred while creating the File", ex);
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri uri = FileProvider.getUriForFile(this.getApplicationContext(), this.getPackageName() + ".fileProvider", photoFile);
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        launcher.launch(intent1);
                    }
                }
            });
            builder.show();
        });

        to_ai.setOnClickListener(v->{
            Intent intent1 = new Intent(this, ChatActivity.class);
            startActivity(intent1);
        });

        commit.setOnClickListener(v->{
            title = titleET.getText().toString();
            content = contentET.getText().toString();
            tagList=tagET.getText().toString();
            List<String> list= Arrays.asList(tagList.split(" "));
            JSONArray jsonArray=new JSONArray(list);
            sendPost(title,content,jsonArray);
        });
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("photoPath",currentPhotoPath);
        return image;
    }

    // 将照片文件上传
    private void uploadPhoto(String uri) {
        if (uri == null || uri.isEmpty()) {
            return;
        }
        File file = new File(uri);
        Thread thread = new Thread(() -> {
            // 创建HTTP客户端
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(60000, TimeUnit.MILLISECONDS)
                    .readTimeout(60000, TimeUnit.MILLISECONDS)
                    .build();
            MediaType mediaType = MediaType.parse("application/octet-stream");//设置类型，类型为八位字节流
            RequestBody requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体

            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), requestBody)//文件名
                    .build();
            // 创建HTTP请求
            Request request = new Request.Builder()
                    .url(constant.IP_ADDRESS + "/oss/upload")
                    .post(multipartBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    String reData=response.body().string();
                    Looper.prepare();
                    Toast.makeText(this, "图片上传成功", Toast.LENGTH_SHORT).show();
                    url=reData;
                } else {
                    Looper.prepare();
                    Toast.makeText(this, "图片上传失败！请检查网络状况", Toast.LENGTH_SHORT).show();

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        addViewModel.getAvatar().setValue(url);
    }
    private void sendPost(String title, String content, JSONArray jsonArray) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        JSONObject jsonObject = new JSONObject();
        Object nullObj = null; //解决put中因二义性引起的编译错误
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("communityId", communityId);
            jsonObject.put("title", title);
            jsonObject.put("content", content);
            jsonObject.put("isPublic", "true");
            jsonObject.put("tagList",jsonArray);
            jsonObject.put("photo",url);
            Log.d("jsonobject", String.valueOf(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
        Log.i("Addpost", jsonObject.toString());

        String url_ = constant.IP_ADDRESS + "/user/publish";
        Request request = new Request.Builder()
                .url(url_)
                .post(requestBody)
                .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                .build();

        // 异步发送请求
        final CountDownLatch latch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                latch.countDown();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // success
                assert response.body() != null;
                final String responseData = response.body().string();
                Log.i("Addpost", responseData);
                try {
                    JSONObject json = new JSONObject(responseData);
                    String code=json.getString("code");
                    if(code.equals("200")){
                        Looper.prepare();
                        Toast.makeText(AddPostActivity.this, "发帖成功", Toast.LENGTH_SHORT).show();
                        isSuccessful = true;
                    }else{
                        Looper.prepare();
                        Toast.makeText(AddPostActivity.this, "发帖失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await(); // 主线程等待子线程执行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (isSuccessful) {
            addViewModel.getAvatar().setValue("http://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/img/image-20231228101219539.png");
            isSuccessful = false;
            finish();
        }
    }
}
