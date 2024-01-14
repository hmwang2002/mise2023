package com.mise.communitycenter.domain.vo;

import lombok.Data;
/**
 * 社区成员
 *
 * @author wlf
 * @since 2023-11-13
 */
@Data
public class MemberVO {

    /**
     * 成员名称
     */
    private String name;

    /**
     * 成员id
     */
    private long userID;
}
