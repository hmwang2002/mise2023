package com.mise.usercenter.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author whm
 * @date 2023/10/27 16:32
 */
@Data
@NoArgsConstructor
public class CommunityVO {

    /**
     * 社区id
     */
    private long communityID;

    /**
     * 是否公开
     */
    public boolean isPublic;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 社区名称
     */
    private String name;
}
