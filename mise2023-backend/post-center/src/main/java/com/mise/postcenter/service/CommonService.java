package com.mise.postcenter.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface CommonService {

    String upload(MultipartFile file) throws IOException;

    InputStream download(String name) throws IOException;
}
