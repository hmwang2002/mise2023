package com.mise.postcenter.service.impl;

import com.mise.postcenter.service.CommonService;
import com.obs.services.ObsClient;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.mise.postcenter.utils.Converter.convert;

@Service
public class CommonServiceImpl implements CommonService {

    @Value("${obs.endPoint}")
    private String endPoint;

    @Value("${obs.ak}")
    private String ak;

    @Value("${obs.sk}")
    private String sk;

    @Value("${obs.bucketName}")
    private String bucketName;

    /**
     * 文件上传
     *
     * @param file
     * @throws IOException
     */
    public String upload(MultipartFile file) throws IOException {
        // 获取原始文件名并使用UUID重新生成
        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;

        // 创建OBS客户端
        try (ObsClient obsClient = new ObsClient(ak, sk, endPoint)) {
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, convert(file));
            obsClient.putObject(request);
        }

        return fileName;
    }

    /**
     * 文件下载
     *
     * @param name
     * @return
     * @throws IOException
     */
    @Override
    public InputStream download(String name) throws IOException {
        InputStream result;
        // 创建OBS客户端
        try (ObsClient obsClient = new ObsClient(ak, sk, endPoint)) {
            GetObjectRequest request = new GetObjectRequest(bucketName, name);
            ObsObject obsObject = obsClient.getObject(request);
            result = obsObject.getObjectContent();
        }
        return result;
    }

}
