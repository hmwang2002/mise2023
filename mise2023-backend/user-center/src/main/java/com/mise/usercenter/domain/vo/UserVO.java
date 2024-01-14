package com.mise.usercenter.domain.vo;

import lombok.Data;

/**
 * @author whm
 * @date 2023/10/24 15:56
 */
@Data
public class UserVO {
    private String userName;

    private String password;

    private String phone;

    private String verifyCode;
}
