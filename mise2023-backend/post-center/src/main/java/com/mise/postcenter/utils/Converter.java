package com.mise.postcenter.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class Converter {

    /**
     * MultipartFile类型转为File类型
     *
     * @param multipartFile xx
     * @return xx
     * @throws IOException xx
     */
    public static File convert(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("temp", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

}
