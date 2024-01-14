package com.mise.communitycenter.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author whm,wlf
 * @date 2023/10/27 16:28
 */
@Data
@TableName("community")
public class Community {
    @TableId(value = "community_id", type = IdType.AUTO)
    private Long communityId;

    @TableField("is_public")
    private boolean isPublic;

    @TableField("create_time")
    private Date createTime;

    @TableField("name")
    private String name;
}
