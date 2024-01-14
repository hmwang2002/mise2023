package com.mise.usercenter.controller;

import com.mise.usercenter.domain.vo.Response;
import com.mise.usercenter.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author whm
 * @date 2023/10/25 10:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {
    private final SmsService smsService;

    @GetMapping("/send")
    public Response send(@RequestParam("phone") String phone) throws Exception {
        boolean flag = smsService.send(phone);
        if (flag) {
            return Response.success(200, "发送成功！");
        } else {
            return Response.failed(999, "发送失败！");
        }
    }

}
