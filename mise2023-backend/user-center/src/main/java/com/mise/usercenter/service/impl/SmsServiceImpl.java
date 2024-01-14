package com.mise.usercenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import com.mise.usercenter.domain.entity.AliConfig;
import com.mise.usercenter.mapper.AliMapper;
import com.mise.usercenter.service.SmsService;
import com.mise.usercenter.utils.RedisCache;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author whm
 * @date 2023/10/24 21:16
 */
@Service
public class SmsServiceImpl implements SmsService {
    private com.aliyun.dysmsapi20170525.Client client;

    private Random random;

    private RedisCache redisCache;

    public SmsServiceImpl(AliMapper aliMapper, RedisCache redisCache) {
        this.random = new Random();
        this.redisCache = redisCache;

        AliConfig aliConfig = aliMapper.selectById(1);
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(aliConfig.getAccessKey())
                .setAccessKeySecret(aliConfig.getAccessSecret());
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        try {
            this.client = new com.aliyun.dysmsapi20170525.Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean send(String phone) {
        int code = random.nextInt(100000, 999999);

        SendSmsRequest request = new SendSmsRequest();
        request.phoneNumbers = phone;
        request.signName = "mise2023";
        request.templateCode = "SMS_463628838";
        request.templateParam = JSONObject.toJSONString(new JSONObject().fluentPut("code", code));
        SendSmsResponse response;
        try {
            response = client.sendSms(request);
            System.out.println(111);
            System.out.println(response.body.code);
            if (response.body.code.equals("OK")) {
                redisCache.setCacheObject(phone, String.valueOf(code), 60 * 5L, TimeUnit.SECONDS);
                return true;
            } else {
                System.out.println(new Gson().toJson(response));
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
