package com.mise.communitycenter.service;


import com.mise.communitycenter.domain.vo.ApplicationCheckVO;
import com.mise.communitycenter.domain.vo.CommunityVO;
import org.apache.catalina.User;

import java.util.List;
import java.util.Map;

public interface ApplicationService {

    /**
     * 申请加入社区
     * @param userID 用户id
     * @param communityID 社区id
     * @return 申请结果
     */
    boolean applyForCommunity(long userID, long communityID);

    /**
     * 退出社区
     * @param userID 用户id
     * @param communityID 社区id
     * @return 退出结果
     */
    boolean exitCommunity(long userID, long communityID);


    /**
     * 同意用户加入社区
     * @param userID 用户id
     * @param communityID 社区id
     * @param handlerID 处理人id
     * @return 处理结果
     */
    boolean accept(long userID, long communityID, long handlerID);

    /**
     * 拒绝用户加入社区
     * @param userID 用户id
     * @param communityID 社区id
     * @param handlerID 处理人id
     * @return 处理结果
     */
    boolean refuse(long userID, long communityID, long handlerID);

    /**
     * 根据管理员id查找其所管理的社区的申请信息
     * @param adminId 管理员id
     * @return key-管理的社区id， value-申请加入社区的用户idList
     */
    List<ApplicationCheckVO> getApplicationByAdminId(long adminId);

    List<Long> getApplicationOfCommunity(long communityId);
}
