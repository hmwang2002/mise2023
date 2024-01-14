package com.example.android_demo.ui.chat;


import static androidx.core.content.ContextCompat.getSystemService;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android_demo.MainViewModel;
import com.example.android_demo.databinding.FragmentChatBinding;

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

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    private Spinner spinner;

    private ArrayAdapter<String> spinnerAdapter;

    private String[] styles = {"推文", "诗歌", "小红书", "贴吧"};

    private String style = "article";

    private HashMap<String, String> map;

    private static final String ADDR = "121.40.84.9:8000";
    // 121.40.84.9

    private String outputArticle = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i("ChatFragment", "onCreateView");

        map = new HashMap<>();
        map.put("推文", "article");
        map.put("诗歌", "poem");
        map.put("小红书", "redbook");
        map.put("贴吧", "tieba");

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 设置长按复制
        binding.outputTextView.setOnLongClickListener(v -> {
            String text = binding.outputTextView.getText().toString();
            // Gets a handle to the clipboard service.
            ClipboardManager clipboard = (ClipboardManager) getSystemService(getContext(), ClipboardManager.class);
            ClipData clip = ClipData.newPlainText("AiText", text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        spinner = binding.spinner;
        spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, styles);

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

        binding.commitButton.setOnClickListener(v -> {
            String userInput = binding.inputEditText.getText().toString();
            getArticle(userInput, style);
        });
        return root;
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
                String output = "";

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.outputTextView.setText(outputArticle);
                        if (style.equals("推文") && !outputArticle.equals("failed")) {
                            binding.outputTextView.setTextSize(15);
                        }
                        else if (style.equals("诗歌") && !outputArticle.equals("failed")) {
                            binding.outputTextView.setTextSize(21);
                        }
                        else if (style.equals("小红书") && !outputArticle.equals("failed")) {
                            binding.outputTextView.setTextSize(15);
                        }
                        else if (style.equals("贴吧") && !outputArticle.equals("failed")) {
                            binding.outputTextView.setTextSize(15);
                        }
                    }
                });
            }
        });
    }
}