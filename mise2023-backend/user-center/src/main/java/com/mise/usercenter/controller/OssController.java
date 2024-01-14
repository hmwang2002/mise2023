package com.mise.usercenter.controller;

import com.mise.usercenter.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/oss")
@RequiredArgsConstructor
public class OssController {
    private final OssService ossService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file){
        String url = ossService.uploadFile(file);
        return url;
    }
}
