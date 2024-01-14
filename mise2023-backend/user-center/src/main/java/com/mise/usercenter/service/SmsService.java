package com.mise.usercenter.service;

/**
 * @author whm
 * @date 2023/10/24 21:16
 */
public interface SmsService {
    boolean send(String phone) throws Exception;
}
