package com.example.android_demo.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String convert(String originalTime) {
        // 将输入的时间字符串解析为 OffsetDateTime 对象
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(originalTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            // 使用自定义的 DateTimeFormatter 格式化为字符串
            return offsetDateTime.format(formatter);
        }
        return originalTime;
    }
}
