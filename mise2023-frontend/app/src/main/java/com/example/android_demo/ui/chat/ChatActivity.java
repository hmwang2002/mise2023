package com.example.android_demo.ui.chat;

import android.content.Context;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android_demo.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private TextView outputTextView;
    private EditText inputEditText;
    private Button commit;
    private Spinner spinner;

    private ArrayAdapter<String> spinnerAdapter;

    private String[] styles = {"推文", "诗歌", "小红书", "贴吧"};

    private String style = "article";

    private HashMap<String, String> map;

    // TODO: enter your own server address
    private static final String ADDR = "server address";

    private String outputArticle = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ChatFragment", "onCreateView");

        map = new HashMap<>();
        map.put("推文", "article");
        map.put("诗歌", "poem");
        map.put("小红书", "redbook");
        map.put("贴吧", "tieba");

        setContentView(R.layout.fragment_chat);
        outputTextView = findViewById(R.id.outputTextView);
        // 设置长按复制
        outputTextView.setOnLongClickListener(v -> {
            String text = outputTextView.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("AiText", text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ChatActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        spinner = findViewById(R.id.spinner);
        spinnerAdapter = new ArrayAdapter<String>(ChatActivity.this, android.R.layout.simple_spinner_item, styles);

        // 设置下拉框样式
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                style = map.get((String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        commit = findViewById(R.id.commitButton);
        inputEditText = findViewById(R.id.inputEditText);
        commit.setOnClickListener(v -> {
            String userInput = inputEditText.getText().toString();
            getArticle(userInput, style);
        });
    }


    private void getArticle(String json, String style) {
        // okHttp

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keywords", json);
            jsonObject.put("style", style);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
        Log.i("ChatFragment", jsonObject.toString());

        String url = "http://" + ADDR + "/api/article";
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 异步发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // failed
                Log.e("ChatFragment", "failed");
                outputArticle = "failed";
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // success
                final String responseData = response.body().string();
                Log.i("ChatFragment", responseData);

                try {
                    JSONObject json = new JSONObject(responseData);
                    String data = json.getString("data");
                    JSONObject dataJson = new JSONObject(data);
                    String article = dataJson.getString("article");
                    outputArticle = article;
                    Log.i("ChatFragment", article);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    outputTextView.setText(outputArticle);
                    if (style.equals("推文") && !outputArticle.equals("failed")) {
                        outputTextView.setTextSize(15);
                    }
                    else if (style.equals("诗歌") && !outputArticle.equals("failed")) {
                        outputTextView.setTextSize(21);
                    }
                    else if (style.equals("小红书") && !outputArticle.equals("failed")) {
                        outputTextView.setTextSize(15);
                    }
                    else if (style.equals("贴吧") && !outputArticle.equals("failed")) {
                        outputTextView.setTextSize(15);
                    }
                });
            }
        });
    }
}