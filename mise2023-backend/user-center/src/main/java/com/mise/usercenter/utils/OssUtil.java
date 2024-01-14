package com.mise.usercenter.utils;

import com.mise.usercenter.domain.entity.AliConfig;
import com.mise.usercenter.mapper.AliMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class OssUtil implements InitializingBean {
    @Resource
    private AliMapper aliMapper;

    public static String endpoint;

    public static String accessKeyId;

    public static String accessKeySecret;

    public static String bucketName;

    @Override
    public void afterPropertiesSet() throws Exception {
        endpoint = "https://oss-cn-hangzhou.aliyuncs.com";

        AliConfig config = aliMapper.selectById(1);
        accessKeyId = config.getAccessKey();
        accessKeySecret = config.getAccessSecret();;

        bucketName = "kiyotakawang";
    }
}
