package com.mise.communitycenter.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ApplicationVO {

    /**
     * 申请者id
     */
    private long userID;

    /**
     * 社区id
     */
    private long communityID;
}
