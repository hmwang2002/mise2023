package com.mise.communitycenter.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mise.communitycenter.enums.ApplicationStatus;
import lombok.Data;

import java.util.Date;

@Data
@TableName("application")
public class Application {

    /**
     * 申请id
     */
    @TableId(value = "application_id", type = IdType.AUTO)
    private long applicationID;

    /**
     * 申请者id
     */
    @TableField("user_id")
    private long userID;

    /**
     * 社区id
     */
    @TableField("community_id")
    private long communityID;

    /**
     * 申请时间
     */
    @TableField("apply_time")
    private Date applyTime;

    /**
     * 处理状态
     */
    @TableField("status")
    private ApplicationStatus status;

    /**
     * 最后处理时间
     */
    @TableField("handle_time")
    private Date handleTime;

    /**
     * 处理人id
     */
    @TableField("handler_id")
    private long handlerID;


}
